/**
 * 
 */
package redisson;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
    @Ignore
    public void testBucket() throws Exception {
        RBucket<String> bucket = redis.getBucket("str", new StringCodec("utf-8"));
        bucket.set("x", 6, TimeUnit.SECONDS);
        LOG.info("get {}", bucket.get());
        Thread.sleep(4000);
        LOG.info("get {} after {}s", bucket.get(), 4);
        Thread.sleep(2000);
        LOG.info("get {} after {}s", bucket.get(), 6);
        bucket.delete();
    }

    @Test
    public void testList() throws Exception {
        RList<ListClazz> list = redis.getList("list");
        list.add(new ListClazz("a", 1));
        LOG.info("get {}", list.get(0));
        list.add(new ListClazz("b", 2));
        // LOG.info("get {}", list.get(0, 1));
        LOG.info("get {}", redis.getList("list"));

        LOG.info("size {}", list.size());
        list.delete();
    }

    static class ListClazz {
        public String a;
        public int b;

        public ListClazz() {}

        public ListClazz(String a, int b) {
            this.a = a;
            this.b = b;
        }

        public String toString() {
            return a + ":" + b;
        }
    }

}
