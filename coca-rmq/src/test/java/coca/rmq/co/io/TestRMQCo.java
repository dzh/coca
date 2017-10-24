/**
 * 
 */
package coca.rmq.co.io;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.BasicCo;
import coca.co.Co;
import coca.co.init.CoInit;
import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;

/**
 * @author dzh
 * @date Sep 15, 2017 2:58:52 AM
 * @since 0.0.1
 */
public class TestRMQCo {

    static final Logger LOG = LoggerFactory.getLogger(TestRMQCo.class);

    static Map<String, String> CoConf = new HashMap<String, String>();
    static {
        CoConf.put(CoInit.P_CO_IO_SELECTOR, RMQChannelSelector.class.getName());
        // CoConf.put(RMQGroupChannel.P_CO_RMQ_NAMESRV, "127.0.0.1:9876");
        // CoConf.put(RMQGroupChannel.P_CO_RMQ_TOPIC_KEY, "DefaultCluster");
        // CoConf.put(RMQGroupChannel.P_CO_RMQ_TOPIC_QUEUENUM, "8");
    }

    @BeforeClass
    public static void InitTest() {
        System.getProperties().setProperty("org.slf4j.simpleLogger.log.TestRMQCo", "warn");
    }

    @Test
    public void testCo() throws Exception {
        // init
        try (Co co = BasicCo.newCo(CoConf)) {

            // join
            co.join("co_test").get();

            // co1 pub
            CoIns<?> ins = co.insFactory().newIns(new Ins(2000, "test1", "nil")).from(co).to(co.group("co_test", true));
            co.pub(ins);

            ins = co.sub(5, TimeUnit.SECONDS);
            LOG.info("sub {} to update cache", ins);

            Thread.sleep(5000);
        }

    }

}
