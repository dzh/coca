/**
 * 
 */
package coca.redis.co.io;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.BasicCo;
import coca.co.Co;
import coca.co.init.CoInit;
import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;
import coca.co.ins.TextCoIns;

/**
 * @author dzh
 * @date Sep 15, 2017 7:42:23 PM
 * @since 0.0.1
 */
public class TestRedisCo {

    static final Logger LOG = LoggerFactory.getLogger(TestRedisCo.class);

    static Map<String, String> CoConf = new HashMap<String, String>();
    static {
        CoConf.put(CoInit.P_CO_IO_SELECTOR, RedisChannelSelector.class.getName());
        CoConf.put(RedisGroupChannel.P_CO_REDIS_ADDR, "redis://127.0.0.1:6379");
    }

    @Test
    public void testCo() throws Exception {
        // init
        try (Co co1 = BasicCo.newCo(CoConf)) {
            // join
            co1.join("co_test");

            // co1 pub
            CoIns<?> ins = (TextCoIns) new TextCoIns(new Ins(2000, "test1", "nil")).from(co1).to(co1.group("co_test", true));
            co1.pub(ins);

            ins = co1.sub(5, TimeUnit.SECONDS);
            LOG.info("sub {} to update cache", ins);

            Thread.sleep(30000);
        }

    }

}
