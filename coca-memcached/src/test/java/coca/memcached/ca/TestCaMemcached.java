/**
 * 
 */
package coca.memcached.ca;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.ca.CaValue;

/**
 * @author dzh
 * @date Nov 22, 2017 6:34:49 PM
 * @since 1.0.0
 */
public class TestCaMemcached {

    static Logger LOG = LoggerFactory.getLogger(TestCaMemcached.class);

    @Test
    public void testXmemcached() throws Exception {
        CaMemcached<String, String> ca = new CaMemcached<String, String>();

        String key = "key1";
        CaValue<String, String> val = ca.read(key);
        LOG.info("{}", val);

        key = "key2";
        ca.write(val.key(key).value("val2"));
        LOG.info("{}", ca.read(key));

        key = "key3";
        ca.write(val.key(key).value("val3").ttl(3000));
        LOG.info("{}", ca.read(key));

        Thread.sleep(3000L);
        LOG.info("{}", ca.read(key));

        ca.close();
    }

}
