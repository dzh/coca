/**
 * 
 */
package coca.co.rmq;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;
import coca.co.ins.TextCoIns;
import coca.co.io.BasicCoIO;
import coca.co.rmq.io.RMQChannelSelector;

/**
 * @author dzh
 * @date Sep 15, 2017 2:58:52 AM
 * @since 0.0.1
 */
public class TestRMQCo {

    static final Logger LOG = LoggerFactory.getLogger(TestRMQCo.class);

    @Test
    public void testCo() throws Exception {
        // init
        try (Co co1 = new RMQCo().io(new BasicCoIO().selector(new RMQChannelSelector())).init()) {

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
