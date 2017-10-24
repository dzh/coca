/**
 * 
 */
package coca.rmq.co.io;

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
import coca.rmq.RMQConst;

/**
 * @author dzh
 * @date Sep 8, 2017 6:02:39 PM
 * @since 0.0.1
 */
public class RMQGroupChannel extends GroupChannel implements RMQConst {

    static final Logger LOG = LoggerFactory.getLogger(RMQGroupChannel.class);

    private DefaultMQPushConsumer consumer;
    private DefaultMQProducer producer;

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

    protected String namesrv() {
        return selector.io().co().conf().get(P_CO_RMQ_NAMESRV, "127.0.0.1:9876");
    }

    protected String topicKey() {
        return selector.io().co().conf().get(P_CO_RMQ_TOPIC_KEY, "DefaultCluster");
    }

    protected int topicQueueNum() {
        return selector.io().co().conf().getInt(P_CO_RMQ_TOPIC_QUEUENUM, "8");
    }

    protected int consumeMessageBatchMaxSize() {
        return selector.io().co().conf().getInt(P_CO_RMQ_C_MESSAGE_BATCH_MAXSIZE, "10");
    }

    protected int consumeThreadMax() {
        return selector.io().co().conf().getInt(P_CO_RMQ_C_THREAD_MAX, "64");
    }

    protected int consumeThreadMin() {
        return selector.io().co().conf().getInt(P_CO_RMQ_C_THREAD_MIN, "20");
    }

    protected int messageIgnoreTimeout() {
        return selector.io().co().conf().getInt(P_CO_RMQ_C_MESSAGE_IGNORE_TIMEOUT, "30");
    }

    protected String consumeTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    protected int consumeTimeout() {
        return selector.io().co().conf().getInt(P_CO_RMQ_C_TIMEOUT, "15");
    }

    public void startConsumer() throws Exception {
        consumer = new DefaultMQPushConsumer(name() + "_" + consumeTimestamp());
        consumer.setNamesrvAddr(getNamesrvAddr());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP);
        consumer.setConsumeTimestamp(consumeTimestamp());
        // consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize());
        consumer.setConsumeTimeout(consumeTimeout());
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.subscribe(topic(), name() + " || " + String.valueOf(selector.io().co().hashCode()));// TODO
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeThreadMax(consumeThreadMax());
        consumer.setConsumeThreadMin(consumeThreadMin());
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                int ignoreTime = messageIgnoreTimeout() * 1000;
                msgs.forEach(msg -> {
                    try {
                        if ((System.currentTimeMillis() - msg.getBornTimestamp()) > ignoreTime) {
                            LOG.warn("ignore rmq msg-{}", msg);
                            return;
                        }

                        LOG.info("recv rmq msg-{}", msg);
                        ByteBuffer packet = ByteBuffer.wrap(msg.getBody());
                        packet.position(4);
                        int v = packet.getShort();
                        packet.rewind();
                        InsPacket ins = RMQGroupChannel.this.codec(v).decode(packet);
                        LOG.info("read packet-{}", ins);
                        // TODO miss packet
                        if (!receive(ins)) LOG.info("discard {}", ins);
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

    protected int produceRetryTimes() {
        return selector.io().co().conf().getInt(P_CO_RMQ_P_RETRY_TIMES, "3");
    }

    public void startProducer() throws Exception {
        producer = new DefaultMQProducer(name());
        producer.setNamesrvAddr(getNamesrvAddr());
        producer.setRetryTimesWhenSendFailed(produceRetryTimes());
        producer.setVipChannelEnabled(false);
        producer.start();
        producer.createTopic(topicKey(), topic(), topicQueueNum());
    }

    protected String topic() {
        return name();
    }

    @Override
    public void close() throws IOException {
        producer.shutdown();
        consumer.shutdown();
        // TODO 测试时发现这个线程偶尔没停止
        consumer.getDefaultMQPushConsumerImpl().getmQClientFactory().getPullMessageService().shutdown(true);
        LOG.warn("rmq stop!");
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
        LOG.info("write packet-{}", packet);
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
