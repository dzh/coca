/**
 * 
 */
package coca.co.rmq.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

import coca.co.io.ChannelSelector;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.GroupChannel;
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

    public static final String TAG_ALL = "all";

    public RMQGroupChannel(String name) {
        super(name);
    }

    @Override
    public CoChannel init(ChannelSelector selector) {
        super.init(selector);
        // TODO
        return this;
    }

    public void startConsumer() throws Exception {
        consumer = new DefaultMQPushConsumer(this.name());
        consumer.setNamesrvAddr("localhost:9876");// TODO
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.subscribe(name(), TAG_ALL + " || " + selector.io().co().hashCode());
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                LOG.info("Receive New Messages:{}", msgs.toString());
                msgs.forEach(msg -> {
                    ByteBuffer packet = ByteBuffer.wrap(msg.getBody());
                    packet.position(4);
                    int v = packet.getShort();
                    packet.rewind();
                    InsPacket ins = RMQGroupChannel.this.codec(v).decode(packet);
                    // TODO
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
    }

    public void startProducer() throws Exception {
        producer = new DefaultMQProducer(this.name());
        producer.setNamesrvAddr("localhost:9876");// TODO
        producer.start();

        // for (int i = 0; i < 100; i++) {
        // Message msg = new Message("TopicTest", "TagA", "OrderID188", "Hello
        // world".getBytes(RemotingHelper.DEFAULT_CHARSET));
        // SendResult sendResult = producer.send(msg);
        // System.out.printf("%s%n", sendResult);
        // }

    }

    @Override
    public void close() throws IOException {
        producer.shutdown();
        consumer.shutdown();
        super.close();
    }

}
