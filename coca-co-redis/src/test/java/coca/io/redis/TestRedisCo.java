/**
 * 
 */
package coca.io.redis;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.BasicCo;
import coca.co.Co;
import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;
import coca.co.ins.TextCoIns;
import coca.co.io.BasicCoIO;
import coca.co.redis.io.RedisChannelSelector;

/**
 * @author dzh
 * @date Sep 15, 2017 7:42:23 PM
 * @since 0.0.1
 */
public class TestRedisCo {

    static final Logger LOG = LoggerFactory.getLogger(TestRedisCo.class);

    @Test
    public void testCo() throws Exception {
        // init
        // System.setProperty(RMQGroupChannel.P_NAMESRC, "192.168.60.42:9876");// rmq namesrv
        try (Co co1 = new BasicCo().io(new BasicCoIO().selector(new RedisChannelSelector())).init()) {

            // join
            co1.join("co_test");

            // co1 pub
            CoIns<?> ins = (TextCoIns) new TextCoIns(new Ins(2000, "test1", "nil")).from(co1).to(co1.group("co_test", true));
            co1.pub(ins);

            ins = co1.sub(5, TimeUnit.SECONDS);
            LOG.info("sub {} to update cache", ins);

            Thread.sleep(5000);
        }

    }

}
