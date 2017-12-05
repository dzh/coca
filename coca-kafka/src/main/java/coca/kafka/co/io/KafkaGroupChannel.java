/**
 * 
 */
package coca.kafka.co.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.io.ChannelSelector;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.CoChannelException;
import coca.co.io.channel.GroupChannel;
import coca.co.io.channel.PacketFuture;
import coca.co.io.channel.PacketResult;
import coca.co.io.packet.InsPacket;
import coca.kafka.KafkaConst;

/**
 * @author dzh
 * @date Nov 8, 2017 4:20:00 PM
 * @since 1.0.0
 */
public class KafkaGroupChannel extends GroupChannel implements KafkaConst {

    static final Logger LOG = LoggerFactory.getLogger(KafkaGroupChannel.class);

    private Producer<String, Bytes> producer;

    private Consumer<String, Bytes> consumer;

    private Thread consumerThread;
    private volatile boolean closed = false;

    public KafkaGroupChannel(String name) {
        super(name);
    }

    @Override
    public CoChannel init(ChannelSelector selector) throws CoChannelException {
        super.init(selector);
        try {
            startConsumer();
            startProducer();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            try {
                close();
            } catch (IOException e1) {}
        }
        closed = false;
        return this;
    }

    /**
     * http://kafka.apache.org/documentation.html#newconsumerconfigs
     */
    private void startConsumer() {
        Properties props = new Properties();
        // high
        props.put("bootstrap.servers", conf(P_CO_KAFKA_BOOTSTRAP_SERVERS, "localhost:9092"));
        props.put("key.deserializer", conf(P_CO_KAFKA_C_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer"));
        props.put("value.deserializer", conf(P_CO_KAFKA_C_VALUE_DESERIALIZER, "org.apache.kafka.common.serialization.BytesDeserializer"));
        props.put("fetch.min.bytes", Integer.parseInt(conf(P_CO_KAFKA_C_FETCH_MIN_BYTES, "1")));
        props.put("group.id", conf(P_CO_KAFKA_C_GROUP_ID, selector.io().co().id())); // unique id
        props.put("heartbeat.interval.ms", Integer.parseInt(conf(P_CO_KAFKA_C_HEARTBEAT_INTERVAL_MS, "3000")));
        props.put("max.partition.fetch.bytes", Integer.parseInt(conf(P_CO_KAFKA_C_MAX_PARTITION_FETCH_BYTES, "1048576")));
        props.put("session.timeout.ms", Integer.parseInt(conf(P_CO_KAFKA_C_SESSION_TIMEOUT_MS, "10000")));
        // ssl
        if (containConf(P_CO_KAFKA_SSL_KEY_PASSWORD)) props.put("ssl.key.password", conf(P_CO_KAFKA_SSL_KEY_PASSWORD, ""));
        if (containConf(P_CO_KAFKA_SSL_KEYSTORE_LOCATION)) props.put("ssl.keystore.location", conf(P_CO_KAFKA_SSL_KEYSTORE_LOCATION, ""));
        if (containConf(P_CO_KAFKA_SSL_KEYSTORE_PASSWORD)) props.put("ssl.keystore.password", conf(P_CO_KAFKA_SSL_KEYSTORE_PASSWORD, ""));
        if (containConf(P_CO_KAFKA_SSL_TRUSTSTORE_LOCATION))
            props.put("ssl.truststore.location", conf(P_CO_KAFKA_SSL_TRUSTSTORE_LOCATION, ""));
        if (containConf(P_CO_KAFKA_SSL_TRUSTSTORE_PASSWORD))
            props.put("ssl.truststore.password", conf(P_CO_KAFKA_SSL_TRUSTSTORE_PASSWORD, ""));
        // medium
        // props.put("auto.offset.reset", conf(P_CO_KAFKA_C_AUTO_OFFSET_RESET, "latest"));// [latest,earliest, none]
        props.put("isolation.level", conf(P_CO_KAFKA_C_ISOLATION_LEVEL, "read_uncommitted"));
        props.put("max.poll.records", Integer.parseInt(conf(P_CO_KAFKA_C_MAX_POLL_RECORDS, "500")));
        props.put("enable.auto.commit", conf(P_CO_KAFKA_C_ENABLE_AUTO_COMMIT, "false"));
        props.put("auto.commit.interval.ms", Integer.parseInt(conf(P_CO_KAFKA_C_AUTO_COMMIT_INTERVAL_MS, "5000")));

        consumer = new KafkaConsumer<>(props);
        // consumer.subscribe(Arrays.asList(name(), name() + "_" + Math.abs(selector.io().co().hashCode())));
        consumer.subscribe(Arrays.asList(name()));// TODO
        consumer.seekToEnd(Collections.emptyList());
        consumerThread = new Thread(() -> {
            int ignoreTime = messageIgnoreTimeout() * 1000;
            while (true) {
                if (closed) break;

                try {
                    ConsumerRecords<String, Bytes> records = consumer.poll(200L);
                    if (records.isEmpty()) continue;

                    boolean consumedSucc = true;
                    for (ConsumerRecord<String, Bytes> record : records) {
                        if ((System.currentTimeMillis() - record.timestamp()) > ignoreTime) {
                            LOG.warn("discard kafka timeout msg-{}", record);
                            continue;
                        }

                        if (record.key() != null && !record.key().equals(KafkaGroupChannel.this.selector.io().co().id())) {
                            LOG.warn("discard kafka other msg-{}", record);
                            continue;
                        }

                        LOG.info("recv kafka msg-{}", record);
                        ByteBuffer packet = ByteBuffer.wrap(record.value().get());
                        packet.position(4);
                        int v = packet.getShort();
                        packet.rewind();
                        InsPacket ins = KafkaGroupChannel.this.codec(v).decode(packet);
                        LOG.info("read packet-{}", ins);

                        if (!receive(ins)) {
                            consumedSucc = false;
                            LOG.error("recv msg-{}", ins);
                            break;
                        }
                    }

                    if (consumedSucc) {
                        consumer.commitAsync();
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            consumer.close(30, TimeUnit.SECONDS);
        }, "KafkaConsumerThread-" + selector.io().co().hashCode());
        consumerThread.start();
    }

    protected int messageIgnoreTimeout() {
        return selector.io().co().conf().getInt(P_CO_KAFKA_C_MESSAGE_IGNORE_TIMEOUT, "10");
    }

    /**
     * http://kafka.apache.org/documentation.html#producerconfigs
     */
    private void startProducer() {
        Properties props = new Properties();
        // high
        props.put("bootstrap.servers", conf(P_CO_KAFKA_BOOTSTRAP_SERVERS, "localhost:9092"));
        props.put("key.serializer", conf(P_CO_KAFKA_P_KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer"));
        props.put("value.serializer", conf(P_CO_KAFKA_P_VALUE_SERIALIZER, "org.apache.kafka.common.serialization.BytesSerializer"));
        props.put("acks", conf(P_CO_KAFKA_P_ACKS, "all")); // [all, -1, 0, 1]
        props.put("buffer.memory", Long.parseLong(conf(P_CO_KAFKA_P_BUFFER_MEMORY, "33554432"))); // bytes
        // none, gzip, snappy, or lz4
        props.put("compression.type", conf(P_CO_KAFKA_P_COMPRESSION_TYPE, "none"));
        props.put("retries", Integer.parseInt(conf(P_CO_KAFKA_P_RETRIES, "1")));
        // ssl
        if (containConf(P_CO_KAFKA_SSL_KEY_PASSWORD)) props.put("ssl.key.password", conf(P_CO_KAFKA_SSL_KEY_PASSWORD, ""));
        if (containConf(P_CO_KAFKA_SSL_KEYSTORE_LOCATION)) props.put("ssl.keystore.location", conf(P_CO_KAFKA_SSL_KEYSTORE_LOCATION, ""));
        if (containConf(P_CO_KAFKA_SSL_KEYSTORE_PASSWORD)) props.put("ssl.keystore.password", conf(P_CO_KAFKA_SSL_KEYSTORE_PASSWORD, ""));
        if (containConf(P_CO_KAFKA_SSL_TRUSTSTORE_LOCATION))
            props.put("ssl.truststore.location", conf(P_CO_KAFKA_SSL_TRUSTSTORE_LOCATION, ""));
        if (containConf(P_CO_KAFKA_SSL_TRUSTSTORE_PASSWORD))
            props.put("ssl.truststore.password", conf(P_CO_KAFKA_SSL_TRUSTSTORE_PASSWORD, ""));
        // medium
        props.put("batch.size", Integer.parseInt(conf(P_CO_KAFKA_P_BATCH_SIZE, "16384")));
        props.put("client.id", conf(P_CO_KAFKA_P_CLIENT_ID, selector.io().co().id()));
        props.put("connections.max.idle.ms", Integer.parseInt(conf(P_CO_KAFKA_P_CONNECTIONS_MAX_IDLE_MS, "540000")));
        props.put("linger.ms", Long.parseLong(conf(P_CO_KAFKA_P_LINGER_MS, "0")));
        // props.put("producer.type", conf(P_CO_KAFKA_P_PRODUCER_TYPE, "sync"));

        producer = new KafkaProducer<String, Bytes>(props);
    }

    /*
     * (non-Javadoc)
     * @see coca.co.io.channel.GroupChannel#writeImpl(coca.co.io.channel.PacketFuture)
     */
    @Override
    protected void writeImpl(PacketFuture pf) throws Exception {
        InsPacket packet = pf.send();
        LOG.info("write packet-{}", packet);

        packet.type(InsPacket.Type.GROUP.ordinal());
        ByteBuffer bytes = codec(packet.version()).encode(packet);
        String topic = packet.ins().toGroup().name();
        List<Co> toCo = packet.ins().toCo();
        if (toCo.isEmpty()) {
            ProducerRecord<String, Bytes> multicast = new ProducerRecord<String, Bytes>(topic, Bytes.wrap(bytes.array()));
            send(multicast, pf);
        } else {
            for (Co co : toCo) { // TODO
                // ProducerRecord<String, Bytes> unicast = new ProducerRecord<String, Bytes>(topic + "_" +
                // Math.abs(co.hashCode()),
                // packet.ins().id(), Bytes.wrap(bytes.array()));
                ProducerRecord<String, Bytes> unicast = new ProducerRecord<String, Bytes>(topic, co.id(), Bytes.wrap(bytes.array()));
                send(unicast, pf);
            }
        }
    }

    private void send(ProducerRecord<String, Bytes> msg, PacketFuture pf) throws Exception {
        producer.send(msg, (m, e) -> {
            if (m != null) {
                LOG.info(m.toString());
                pf.result(new PacketResult(PacketResult.IOSt.SEND_SUCC));
            } else {
                LOG.error(e.getMessage(), e);
                pf.result(new PacketResult(PacketResult.IOSt.SEND_FAIL));
            }
        });
    }

    @Override
    public void close() throws IOException {
        super.close();
        closed = true;
        if (producer != null) producer.close(60, TimeUnit.SECONDS);
        try {
            consumerThread.join(60L);
        } catch (Exception e) {}
        LOG.info("{} close!", name());
    }

}
