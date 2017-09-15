/**
 * 
 */
package rmq;

import org.junit.Ignore;
import org.junit.Test;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

@Ignore
public class TestProducer {

    @Test
    public void testSyncProducer() throws Exception {
        // Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        // Launch the instance.
        producer.start();
        for (int i = 0; i < 100; i++) {
            // Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest" /* Topic */, "TagA" /* Tag */,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            // Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        // Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }

    @Test
    public void testAsyncProducer() throws Exception {
        // Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        // Launch the instance.
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);
        for (int i = 0; i < 100; i++) {
            final int index = i;
            // Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest", "TagA", "OrderID188", "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception %s %n", index, e);
                    e.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testOnewayProducer() throws Exception {
        // Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        // Launch the instance.
        producer.start();
        for (int i = 0; i < 100; i++) {
            // Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest" /* Topic */, "TagA" /* Tag */,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            // Call send message to deliver message to one of brokers.
            producer.sendOneway(msg);

        }
        // Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }

}
