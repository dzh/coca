package rmq;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestFilterProducer {

    // rocketmq version > 4
    @Before
    public void testFilterConsumer() throws Exception {
        // DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_4");
        //
        // // only subsribe messages have property a, also a >=0 and a <= 3
        // consumer.subscribe("TopicTest", MessageSelector.bySql("a between 0 and 3"));
        //
        // consumer.registerMessageListener(new MessageListenerConcurrently() {
        // @Override
        // public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        // return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        // }
        // });
        // consumer.start();
    }

    @Test
    public void testFilterProducer() throws Exception {
        // DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        // producer.start();
        //
        // Message msg = new Message("TopicTest", tag, ("Hello RocketMQ " +
        // i).getBytes(RemotingHelper.DEFAULT_CHARSET));
        // // Set some properties.
        // msg.putUserProperty("a", String.valueOf(i));
        //
        // SendResult sendResult = producer.send(msg);
        //
        // producer.shutdown();
    }

}
