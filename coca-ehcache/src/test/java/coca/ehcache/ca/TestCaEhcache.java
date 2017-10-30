/**
 * 
 */
package coca.ehcache.ca;

import java.util.concurrent.TimeUnit;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.ca.CaValue;

/**
 * @author dzh
 * @date Oct 30, 2017 1:15:48 PM
 * @since 0.0.1
 */
public class TestCaEhcache {

    static Logger LOG = LoggerFactory.getLogger(TestCaEhcache.class);

    private static CacheManager cacheManager;
    static String NAME = "ca-ehcache";

    @BeforeClass
    public static void InitEhcache() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(NAME,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10))
                                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(6, TimeUnit.SECONDS))))
                .build();
        cacheManager.init();
    }

    @Test
    public void testCaEhcache() throws Exception {
        @SuppressWarnings("resource")
        CaEhcache<String, String> ca = new CaEhcache<String, String>(NAME, cacheManager.getCache(NAME, String.class, String.class));
        String key = "key1";

        CaValue<String, String> val = ca.read(key);
        LOG.info(val.toString());
        Assert.assertEquals(null, val.value());

        ca.write(CaValue.newVal(key, "val1"));
        val = ca.read(key);
        LOG.info(val.toString());
        Assert.assertEquals("val1", val.value());

        Thread.sleep(6000L);
        val = ca.read(key);
        LOG.info(val.toString());
        Assert.assertEquals(null, val.value());
    }

    @AfterClass
    public static void CloseEhcache() {
        cacheManager.close();
    }

}
