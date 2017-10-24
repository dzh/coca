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
import coca.demo.benchmark.redis.CocaRedisBenchmark;
import coca.demo.sample.CocaSample;
import coca.rmq.co.io.RMQChannelSelector;

/**
 * @author dzh
 * @date Oct 15, 2017 9:33:34 PM
 * @since 0.0.1
 */
public class CocaRMQBenchmark {

    static final Logger LOG = LoggerFactory.getLogger(CocaRedisBenchmark.class);

    static Map<String, String> CONF = new HashMap<>(8);
    static {
        CONF.put(CocaConst.P_CO_IO_SELECTOR, RMQChannelSelector.class.getName());
    }

    /**
     * benchmark:(MacPro 2.9 GHz Intel Core i5 | RMQ 4.2.0-incubating-SNAPSHOT)
     * 
     * <pre>
     * 
     * </pre>
     * 
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configurator.initialize("CocaRMQBenchmark", Thread.currentThread().getContextClassLoader(), "redis-log4j2.xml");

        // init coca
        CocaSample pub = new CocaSample("appPub", "syncGroup");
        pub.initCoca(CONF);
        CocaSample sub = new CocaSample("appSub", "syncGroup");
        sub.initCoca(CONF);

        // warm up
        int preCount = 0;
        for (int i = 0; i < preCount; i++) {
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }

        long s0 = System.currentTimeMillis();
        int writeCount = 1000;
        for (int i = 0; i < writeCount; i++) {
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }
        long s1 = System.currentTimeMillis();

        Thread.sleep(3000L);
        pub.close();
        sub.close();

        LOG.info("pub-{} sub-{} time-{} {}/s", pub.countPub() - preCount, sub.countSub() - preCount, s1 - s0,
                (int) ((pub.countPub() - preCount) / ((s1 - s0) / 1000.0)));
    }

}
