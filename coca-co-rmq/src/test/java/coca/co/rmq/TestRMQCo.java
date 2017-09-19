/**
 * 
 */
package coca.co.rmq;

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
import coca.co.rmq.io.RMQChannelSelector;
import coca.co.rmq.io.RMQGroupChannel;

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
    }

    @Test
    public void testCo() throws Exception {
        // init
        System.setProperty(RMQGroupChannel.P_NAMESRC, "192.168.60.42:9876");// rmq namesrv
        try (Co co1 = BasicCo.create(CoConf)) {

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
