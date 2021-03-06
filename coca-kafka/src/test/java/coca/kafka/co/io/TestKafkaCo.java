/**
 * 
 */
package coca.kafka.co.io;

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

/**
 * @author dzh
 * @date Nov 21, 2017 2:42:05 PM
 * @since 1.0.0
 */
public class TestKafkaCo {

    static final Logger LOG = LoggerFactory.getLogger(TestKafkaCo.class);

    static Map<String, String> CoConf = new HashMap<String, String>();
    static {
        CoConf.put(CoInit.P_CO_IO_SELECTOR, KafkaChannelSelector.class.getName());
    }

    @Test
    public void testCo() throws Exception {
        // init
        try (Co co1 = BasicCo.newCo(CoConf)) {
            // join
            co1.join("co_test").get();

            CoIns<?> ins = co1.insFactory().newIns(new Ins(2000, "test1", "nil")).from(co1).to(co1.group("co_test", true));
            co1.pub(ins);

            ins = co1.sub(5, TimeUnit.SECONDS);
            LOG.info("sub {}", ins);

            Thread.sleep(3000);
        }

    }

}
