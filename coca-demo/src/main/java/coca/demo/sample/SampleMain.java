/**
 * 
 */
package coca.demo.sample;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.config.Configurator;

import com.alibaba.rocketmq.shade.io.netty.util.internal.ThreadLocalRandom;

import coca.api.CocaConst;
import coca.redis.co.io.RedisChannelSelector;

/**
 * @author dzh
 * @date Oct 16, 2017 12:44:41 AM
 * @since 0.0.1
 */
public class SampleMain {

    static Map<String, String> CONF = new HashMap<>();
    static {
        CONF.put(CocaConst.P_CO_IO_SELECTOR, RedisChannelSelector.class.getName());
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // load log4j2.xml
        // System.setProperty("log4j.configurationFile", "");
        Configurator.initialize("SampleMain", Thread.currentThread().getContextClassLoader(), "sample-log4j.xml");

        // initialize coca instance
        CocaSample app1 = new CocaSample("app1", "syncGroup");
        app1.initCoca(CONF);
        CocaSample app2 = new CocaSample("app2", "syncGroup");
        app2.initCoca(CONF);

        // testRead(app1, app2);

        testShareWrite(app1, app2);

        // close
        app1.close();
        app2.close();
    }

    public static void testShareWrite(CocaSample app1, CocaSample app2) throws InterruptedException {
        String key = "6";
        // clear key
        app1.writeKey(key, null, 0, true); // write:reids->app1 evict:app2
        Thread.sleep(1000L);
        app1.readShareKey(key);  // null:app1->redis
        app2.readShareKey(key); // null:app2->redis

        app2.writeKey(key, ThreadLocalRandom.current().nextInt(100, 1000), 3000, true); // write:redis->app2 evict:app1

        app1.readShareKey(key); // got:app1(null)->redis
        app2.readShareKey(key); // got:app2

        Thread.sleep(5000);

        app1.readShareKey(key);  // null:app1->redis
        app2.readShareKey(key); // got:app2 because of @link{#CaGuava}.expireAfterWrite 10s

        Thread.sleep(6000);
        app2.readShareKey(key); // null:app2->redis
    }

    public static void testRead(CocaSample app1, CocaSample app2) {
        app1.readSelfKey("1");
        app2.readSelfKey("1");

        app1.readShareKey("5");
        app2.readShareKey("5");
    }

}
