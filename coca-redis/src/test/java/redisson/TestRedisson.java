/**
 * 
 */
package redisson;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 15, 2017 7:43:21 PM
 * @since 0.0.1
 */
public class TestRedisson {
    private RedissonClient redis;
    static Logger LOG = LoggerFactory.getLogger(TestRedisson.class);

    @Before
    public void before() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redis = Redisson.create(config);
    }

    /**
     * 每次测试方法运行完之后 运行此方法
     * 用于关闭客户端连接服务器的redisson对象
     */
    @After
    public void after() {
        redis.shutdown(2, 15, TimeUnit.SECONDS);
    }

    @Test
    public void testBucket() throws Exception {
        RBucket<String> bucket = redis.getBucket("str", new StringCodec("utf-8"));
        bucket.set("x", 6, TimeUnit.SECONDS);
        LOG.info("get {}", bucket.get());
        Thread.sleep(4000);
        LOG.info("get {} after {}s", bucket.get(), 4);
        Thread.sleep(2000);
        LOG.info("get {} after {}s", bucket.get(), 6);
        bucket.delete();

        RBucket<TestClazz> bucket1 = redis.getBucket("clazz");
        bucket1.set(new TestClazz("c", 3));
        LOG.info("get {}", bucket1.get());
        bucket1.delete();
    }

    @Test
    public void testList() throws Exception {
        RList<TestClazz> list = redis.getList("list");
        list.add(new TestClazz("a", 1));
        LOG.info("get {}", list.get(0));
        list.add(new TestClazz("b", 2));
        // LOG.info("get {}", list.get(0, 1));
        LOG.info("get {}", redis.getList("list"));

        LOG.info("size {}", list.size());
        list.delete();
    }
    
    @Test
    public void test1k() {
        long s0 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            RBucket<String> bucket = redis.getBucket("test1k" + i, new StringCodec("utf-8"));
            bucket.set(String.valueOf(System.nanoTime()), 1, TimeUnit.HOURS);
        }
        long s1 = System.currentTimeMillis();
        LOG.info("cost {}ms", s1 - s0);
    }

    static class TestClazz {
        public String a;
        public int b;

        public TestClazz() {}

        public TestClazz(String a, int b) {
            this.a = a;
            this.b = b;
        }

        public String toString() {
            return a + ":" + b;
        }
    }

}
