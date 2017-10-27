/**
 * 
 */
package coca.demo.benchmark.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.api.CocaConst;
import coca.demo.sample.CocaSample;
import coca.redis.co.io.RedisChannelSelector;

/**
 * @author dzh
 * @date Oct 15, 2017 9:33:06 PM
 * @since 0.0.1
 */
public class CocaRedisBenchmark {

    static final Logger LOG = LoggerFactory.getLogger(CocaRedisBenchmark.class);

    static Map<String, String> CONF = new HashMap<>(8);
    static {
        CONF.put(CocaConst.P_CO_IO_SELECTOR, RedisChannelSelector.class.getName());
    }

    /**
     * benchmark:(MacPro 2.9 GHz Intel Core i5 | Redis 4.0.1)
     * 
     * <pre>
     * pub-1000             sub-1000            time-534            1872/s
     * pub-10000            sub-10000           time-4069           2457/s
     * pub-100000           sub-100000          time-21486          4654/s
     * pub-1000000          sub-1000000         time-217791         4591/s
     * pub-2000000          sub-2000000         time-401279         4984/s
     * </pre>
     * 
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configurator.initialize("CocaRedisBenchmark", Thread.currentThread().getContextClassLoader(), "redis-log4j2.xml");

        // init coca
        CocaSample pub = new CocaSample("appPub", "syncGroup");
        pub.initCoca(CONF);
        CocaSample sub = new CocaSample("appSub", "syncGroup");
        sub.initCoca(CONF);

        int warmCount = 10000;
        for (int i = 0; i < warmCount; i++) { // warm up
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }

        long s0 = System.currentTimeMillis();
        int writeCount = 2000000;
        for (int i = 0; i < writeCount; i++) {
            String key = String.valueOf(ThreadLocalRandom.current().nextInt(6, 10));
            pub.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true);
        }
        long s1 = System.currentTimeMillis();

        Thread.sleep(3000L);
        pub.close();
        sub.close();

        LOG.info("pub-{} sub-{} time-{} {}/s", pub.countPub() - warmCount, sub.countSub() - warmCount, s1 - s0,
                (int) ((pub.countPub() - warmCount) / ((s1 - s0) / 1000.0)));
    }

}
