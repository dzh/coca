/**
 * 
 */
package coca.demo.benchmark.rmq;

import java.io.File;
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
 * Start order: CocaRMQBenchmark -> CocaRMQSub
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
        CONF.put(RMQConst.P_CO_RMQ_TOPIC_QUEUENUM, "2");
    }

    public static int warmCount = 10000;
    public static int writeCount = 1000;

    public static String PubRunFlag = System.getProperties().getProperty("user.dir") + "/" + "pub.lock";

    /**
     * benchmark:(MacPro 2.9 GHz Intel Core i5 | RMQ 4.2.0-incubating-SNAPSHOT)
     * 
     * <pre>
     * pub-1000 time-502 1992/s
     * </pre>
     * 
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configurator.initialize("CocaRMQBenchmark", Thread.currentThread().getContextClassLoader(), "rmq-log4j2.xml");

        // init coca
        CocaSample pub = new CocaSample("appPub", "syncGroup");
        pub.initCoca(CONF);

        // create lock
        new File(PubRunFlag).delete();
        new File(PubRunFlag).createNewFile();

        Thread.sleep(6000L);

        for (int i = 0; i < warmCount; i++) {  // warm up
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }

        long s0 = System.currentTimeMillis();
        for (int i = 0; i < writeCount; i++) {
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }
        long s1 = System.currentTimeMillis();

        Thread.sleep(3000L);
        pub.close();

        LOG.info("pub-{} time-{} {}/s", pub.countPub() - warmCount, s1 - s0, (int) ((pub.countPub() - warmCount) / ((s1 - s0) / 1000.0)));
        // delete lock
        new File(PubRunFlag).delete();
    }

}
