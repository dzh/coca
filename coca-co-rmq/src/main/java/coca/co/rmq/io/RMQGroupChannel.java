/**
 * 
 */
package coca.co.rmq.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

import coca.co.Co;
import coca.co.io.ChannelSelector;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.CoChannelException;
import coca.co.io.channel.GroupChannel;
import coca.co.io.channel.PacketFuture;
import coca.co.io.channel.PacketResult;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 8, 2017 6:02:39 PM
 * @since 0.0.1
 */
public class RMQGroupChannel extends GroupChannel {

    static final Logger LOG = LoggerFactory.getLogger(RMQGroupChannel.class);

    private DefaultMQPushConsumer consumer;
    private DefaultMQProducer producer;

    public static final String P_CO_RMQ_NAMESRV = "co.rmq.namesrv";
    public static final String P_CO_RMQ_TOPIC_KEY = "co.rmq.topic.key";
    public static final String P_CO_RMQ_TOPIC_QUEUENUM = "co.rmq.topic.queuenum";

    private String namesrvAddr;

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public RMQGroupChannel(String name) {
        super(name);
    }

    @Override
    public CoChannel init(ChannelSelector selector) throws CoChannelException {
        super.init(selector);
        setNamesrvAddr(namesrv());
        try {
            startProducer();
            startConsumer();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            try {
                close();
            } catch (IOException e1) {}
        }
        return this;
    }

    private String namesrv() {
        return selector.io().co().conf().get(P_CO_RMQ_NAMESRV, "127.0.0.1:9876");
    }

    private String topicKey() {
        return selector.io().co().conf().get(P_CO_RMQ_TOPIC_KEY, "DefaultCluster");
    }

    private int topicQueueNum() {
        return selector.io().co().conf().getInt(P_CO_RMQ_TOPIC_QUEUENUM, "8");
    }

    private String consumeTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public void startConsumer() throws Exception {
        consumer = new DefaultMQPushConsumer(name());// TODO
        consumer.setNamesrvAddr(getNamesrvAddr());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        consumer.setConsumeTimestamp(consumeTimestamp());
        consumer.setConsumeTimeout(6);
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.subscribe(name(), name() + " || " + String.valueOf(selector.io().co().hashCode()));// TODO
        consumer.setVipChannelEnabled(false);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                LOG.info("Receive New Messages:{}", msgs);
                msgs.forEach(msg -> { // FIXME receive from offset 0 why
                    try {
                        ByteBuffer packet = ByteBuffer.wrap(msg.getBody());
                        packet.position(4);
                        int v = packet.getShort();
                        packet.rewind();
                        InsPacket ins = RMQGroupChannel.this.codec(v).decode(packet);
                        LOG.info("read packet {}", ins);
                        // TODO miss packet
                        if (!RMQGroupChannel.this.rq.offer(ins)) LOG.info("discard {}", ins);
                    } catch (Exception e) {
                        LOG.info(e.getMessage(), e);// TODO miss
                    }
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // consumer.registerMessageListener(new MessageListenerOrderly() {
        // @Override
        // public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        // // context.setAutoCommit(false);
        // msgs.forEach(msg -> {
        // try {
        // ByteBuffer packet = ByteBuffer.wrap(msg.getBody());
        // packet.position(4);
        // int v = packet.getShort();
        // packet.rewind();
        // InsPacket ins = RMQGroupChannel.this.codec(v).decode(packet);
        // LOG.info("read packet {}", ins);
        // // TODO miss packet
        // if (!RMQGroupChannel.this.rq.offer(ins)) LOG.info("discard {}", ins);
        // } catch (Exception e) {
        // LOG.info("miss-" + e.getMessage(), e);// TODO miss
        // }
        // });
        // return ConsumeOrderlyStatus.SUCCESS;
        //
        // }
        // });

        consumer.start();
    }

    public void startProducer() throws Exception {
        producer = new DefaultMQProducer(name());
        producer.setNamesrvAddr(getNamesrvAddr());
        producer.setRetryTimesWhenSendFailed(3);
        producer.setVipChannelEnabled(false);
        producer.start();
        producer.createTopic(topicKey(), name(), topicQueueNum());
    }

    @Override
    public void close() throws IOException {
        producer.shutdown();
        consumer.shutdown();
        super.close();
    }

    @Override
    protected void writeImpl(PacketFuture pf) throws Exception {
        InsPacket packet = pf.send();
        packet.type(InsPacket.Type.GROUP.ordinal());
        ByteBuffer bytes = codec(packet.version()).encode(packet);
        String topic = packet.ins().toGroup().name();
        List<Co> toCo = packet.ins().toCo();
        if (toCo.isEmpty()) {
            Message multicast = new Message(topic, name(), packet.ins().id(), bytes.array());
            send(multicast, pf);
        } else {
            for (Co co : toCo) {
                Message unicast = new Message(topic, String.valueOf(co.hashCode()), packet.ins().id(), bytes.array());
                send(unicast, pf);
            }
        }
        LOG.info("write packet {}", packet);
    }

    private void send(Message msg, PacketFuture pf) throws Exception {
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                LOG.info(sendResult.toString());
                pf.result(new PacketResult(PacketResult.IOSt.SEND_SUCC));
            }

            @Override
            public void onException(Throwable e) {
                LOG.error(e.getMessage(), e);
                pf.result(new PacketResult(PacketResult.IOSt.SEND_FAIL));
            }
        });
    }

}
