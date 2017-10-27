/**
 * 
 */
package coca.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.ca.BasicCa;
import coca.ca.Ca;
import coca.ca.CaValue;
import coca.ca.stack.CaStack;
import coca.co.CoException;

/**
 * @author dzh
 * @date Oct 12, 2017 8:04:15 PM
 * @since 0.0.1
 */
public class TestCoca {

    static Logger LOG = LoggerFactory.getLogger(TestCoca.class);
    Coca coca;

    @Before
    public void initCoca() {
        Map<String, String> conf = new HashMap<>();
        // conf.put(CocaConst.P_CO_INS_FACTORY, CocaInsFactory.class.getName());

        coca = Coca.newCoca(conf);
    }

    @After
    public void closeCoca() {
        try {
            coca.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

    @Test
    public void testWithStack() throws IOException, CoException {
        List<? extends Ca<String, String>> caList =
                Arrays.asList(new CaLocal<String, String>("local"), new CaRemote<String, String>("remote"));
        LOG.info("index-{} name-{}", 1, caList.get(1).name());
        CaStack<String, String> stack = coca.<String> withStack("test", caList);
        Ca<String, String> ca = stack.ca(0);
        Assert.assertEquals("local", ca.name());

        LOG.info("index-{} name-{}", 1, stack.ca(1).name());
    }

    public static class CaLocal<K, V> extends BasicCa<K, V> {

        public CaLocal(String name) {
            super(name, CaType.Local);
        }

        @Override
        protected CaValue<K, V> doRead(K key) {
            return null;
        }

        @Override
        protected boolean doWrite(CaValue<K, V> val) {
            return false;
        }

    }

    public static class CaRemote<K, V> extends BasicCa<K, V> {

        public CaRemote(String name) {
            super(name, CaType.Remote);
        }

        @Override
        protected CaValue<K, V> doRead(K key) {
            return null;
        }

        @Override
        protected boolean doWrite(CaValue<K, V> val) {
            return false;
        }

    }

}
