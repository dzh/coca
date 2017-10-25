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
import coca.rmq.RMQConst;

/**
 * @author dzh
 * @date Sep 15, 2017 2:58:52 AM
 * @since 0.0.1
 */
public class TestRMQCo2 {

    static final Logger LOG = LoggerFactory.getLogger(TestRMQCo2.class);

    static Map<String, String> CoConf = new HashMap<String, String>();
    static {
        CoConf.put(CoInit.P_CO_IO_SELECTOR, RMQChannelSelector.class.getName());
        CoConf.put(RMQConst.P_CO_RMQ_NAMESRV, "192.168.60.42:9876");
        // CoConf.put(RMQConst.P_CO_RMQ_TOPIC_KEY, "DefaultCluster");
        CoConf.put(RMQConst.P_CO_RMQ_TOPIC_QUEUENUM, "2");
    }

    @BeforeClass
    public static void InitTest() {
        // System.getProperties().setProperty("org.slf4j.simpleLogger.log.TestRMQCo", "warn");
    }

    @Test
    public void testCo() throws Exception {
        // init
        try (Co co2 = BasicCo.newCo(CoConf)) {

            Thread.sleep(2000);
            // join
            co2.join("co_test").get();

            // pub
            CoIns<?> ins = co2.insFactory().newIns(new Ins(2000, "co2-test", "nil")).from(co2).to(co2.group("co_test", true));
            co2.pub(ins);

            ins = co2.sub(5, TimeUnit.SECONDS);
            LOG.info("co2-sub {}", ins);
            ins = co2.sub(5, TimeUnit.SECONDS);
            LOG.info("co2-sub {}", ins);
            ins = co2.sub(5, TimeUnit.SECONDS);
            LOG.info("co2-sub {}", ins);

            Thread.sleep(2000);
        }

    }

}
