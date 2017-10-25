/**
 * 
 */
package coca.demo.sample;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.api.Coca;
import coca.api.ca.CocaListener;
import coca.api.handler.InsHandler;
import coca.ca.Ca;
import coca.ca.CaValue;
import coca.ca.stack.StackEvent;
import coca.co.CoException;
import coca.co.ins.CoIns;
import coca.guava.ca.CaGuava;
import coca.redis.ca.CaRedis;

/**
 * @author dzh
 * @date Oct 15, 2017 10:56:52 PM
 * @since 0.0.1
 */
public class CocaSample {

    // static {
    // System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
    // }
    static Logger LOG = LoggerFactory.getLogger(CocaSample.class);

    public Coca coca;

    public final String appName;

    public final String stackName;

    private AtomicLong pubCount = new AtomicLong(0);

    private AtomicLong subCount = new AtomicLong(0);

    // private volatile boolean closed = false;

    public CocaSample(String name, String stack) {
        this.appName = name;
        this.stackName = stack;
    }

    private final Coca newCoca(String name, Map<String, String> conf) {
        Coca coca = new Coca(name) {
            protected InsHandler<?> customHandler(CoIns<?> coIns) {
                subCount.incrementAndGet();
                return super.customHandler(coIns);
            }
        };
        coca.init(conf);
        return coca;
    }

    public boolean initCoca(Map<String, String> conf) {
        coca = newCoca(appName, conf);
        Ca<String, Integer> guava = new CaGuava<String, Integer>("CocaSample-Guava");
        Ca<String, Integer> redis = new CaRedis<String, Integer>("CocaSample-Redis");
        try {
            coca.withStack(stackName, Arrays.asList(guava, redis), new CocaListener() {
                protected void pubEvict(StackEvent evnt) {
                    pubCount.incrementAndGet();
                    super.pubEvict(evnt);
                }
            });
        } catch (CoException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public void writeKey(String key, Integer value, long ttl, boolean sync) {
        key = sync ? "coca.share." + key : "coca." + appName + "." + key;
        CaValue<String, Integer> val = CaValue.newVal(key, value).sync(sync).ttl(ttl);
        coca.<Integer> stack(stackName).write(val);
        LOG.info("writeKey {} {}, {}", appName, key, val);
    }

    public void readShareKey(String key) {
        key = "coca.share." + key;
        CaValue<String, Integer> val = coca.<Integer> stack(stackName).read(key);
        LOG.info("readKey {} {}, {}", appName, key, val);
    }

    public void readSelfKey(String key) {
        key = "coca." + appName + "." + key;
        CaValue<String, Integer> val = coca.<Integer> stack(stackName).peek().read(key);
        LOG.info("readKey {} {}, {}", appName, key, val);
    }

    // static int[] SelfKey = { 0, 1, 2, 3, 4 }; // Not sync keys' suffix

    // static int[] SyncKey = { 5, 6, 7, 8, 9 }; // Sync keys's suffix

    // public void startWriteThread() {
    // String name = sample + "-write";
    // String prefix = "coca." + sample + ".";
    // Thread t = new Thread(() -> {
    // for (;;) {
    // int ran = ThreadLocalRandom.current().nextInt(10);
    // CaValue<String, Integer> val = CaValue.newVal(prefix + ran, ThreadLocalRandom.current().nextInt(100, 1000));
    // if (ran < SyncKey[0]) {
    // val.sync(false);
    // }
    // coca.<Integer> stack(sample).write(val);
    // // exit
    // if (closed) break;
    //
    // try {
    // Thread.sleep(100L);
    // } catch (InterruptedException e) {
    // LOG.warn(e.getMessage());
    // break;
    // }
    // }
    // LOG.info("thread {} closed", name);
    // }, name);
    // t.start();
    // }

    // public void startReadThread() {
    // String name = sample + "-read";
    // Thread t = new Thread(() -> {
    // for (;;) {
    //
    // if (closed) break;
    // }
    // LOG.info("thread {} closed", name);
    // }, name);
    // t.start();
    // }

    public void close() {
        // closed = true;
        try {
            List<Ca<String, Integer>> list = coca.<Integer> stack(stackName).asList();
            coca.close();
            for (Ca<?, ?> ca : list) {
                ca.close();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.info("{} pub-{} sub-{}", coca.name(), countPub(), countSub());
    }

    public long countPub() {
        return pubCount.get();
    }

    public long countSub() {
        return subCount.get();
    }

}
