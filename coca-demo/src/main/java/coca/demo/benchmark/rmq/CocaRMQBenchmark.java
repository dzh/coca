/**
 * 
 */
package coca.demo.benchmark.rmq;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.shade.io.netty.util.internal.ThreadLocalRandom;

import coca.api.CocaConst;
import coca.demo.sample.CocaSample;
import coca.rmq.RMQConst;
import coca.rmq.co.io.RMQChannelSelector;

/**
 * Start order: CocaRMQSub -&gt; CocaRMQBenchmark
 * 
 * 
 * @author dzh
 * @date Oct 15, 2017 9:33:34 PM
 * @since 0.0.1
 */
public class CocaRMQBenchmark {

    static final Logger LOG = LoggerFactory.getLogger(CocaRMQBenchmark.class);

    static Map<String, String> CONF = new HashMap<>(8);
    static {
        CONF.put(CocaConst.P_CO_IO_SELECTOR, RMQChannelSelector.class.getName());
        // CONF.put(RMQConst.P_CO_RMQ_NAMESRV, "192.168.60.42:9876");
        // CoConf.put(RMQGroupChannel.P_CO_RMQ_TOPIC_KEY, "DefaultCluster");
        CONF.put(RMQConst.P_CO_RMQ_TOPIC_QUEUENUM, "8");
        // CONF.put(CocaConst.P_COCA_HANDLER_THREDNUM, "10");
    }

    public static int warmCount = 10000;
    public static int writeCount = 60000;

    // public static String PubRunFlag = System.getProperties().getProperty("user.dir") + "/" + "pub.lock";

    /**
     * benchmark:(MacPro 2.9 GHz Intel Core i5 | RMQ 4.2.0-incubating-SNAPSHOT jvm-4g)
     * 
     * <pre>
     * P_CO_RMQ_TOPIC_QUEUENUM = 8 sub-1 warmCount-10000
     * 
     * pub-1000     time-907    1102/s
     * pub-10000    time-9083   1100/s
     * pub-20000    time-12323  1597/s
     * pub-60000    time-32081  1835/s
     * </pre>
     * 
     * <pre>
     * P_CO_RMQ_TOPIC_QUEUENUM = 8 sub-4 warmCount-10000
     * 
     * pub-1000     time-1249   800/s
     * pub-10000    time-11071  903/s
     * pub-20000    time-20429  979/s
     * pub-60000    time-53037  1131/s
     * </pre>
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configurator.initialize("CocaRMQBenchmark", Thread.currentThread().getContextClassLoader(), "rmq-log4j2.xml");

        // init coca
        CocaSample pub = new CocaSample("appPub", "syncGroup");
        pub.initCoca(CONF);

        Thread.sleep(6000L);
        LOG.info("start warm-{}", warmCount);
        for (int i = 0; i < warmCount; i++) {  // warm up
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }
        LOG.info("start write-{}", writeCount);
        long s0 = System.currentTimeMillis();
        for (int i = 1; i <= writeCount; i++) {
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }
        long s1 = System.currentTimeMillis();

        Thread.sleep(3000L);
        pub.close();

        LOG.info("pub-{} time-{} {}/s", pub.countPub() - warmCount, s1 - s0, (int) ((pub.countPub() - warmCount) / ((s1 - s0) / 1000.0)));
    }

    /**
     * FIXME {@link DefaultMQPushConsumer#pullThresholdForQueue} DefaultMQPushConsumerImpl
     * set P_CO_RMQ_C_PULL_THRESHOLD=10000
     * [WARN ] [PullMessageService] RocketmqClient - the consumer message buffer is full, so do flow control,
     * minOffset=132691, maxOffset=133692,
     * size=1002, pullRequest=PullRequest [consumerGroup=syncGroup, messageQueue=MessageQueue [topic=syncGroup,
     * brokerName=dzh-laptop.local, queueId=0], nextOffset=133693], flowControlTimes=1
     */

    /**
     * FIXME co.rmq.c.max.span = 10000
     * [WARN ] [PullMessageService] RocketmqClient - the queue's messages, span too long, so do flow control,
     * minOffset=13713, maxOffset=15714, maxSpan=2001, pullRequest=PullRequest [consumerGroup=syncGroup,
     * messageQueue=MessageQueue [topic=syncGroup, brokerName=dzh-laptop.local, queueId=2], nextOffset=15715],
     * flowControlTimes=1001
     */

}
