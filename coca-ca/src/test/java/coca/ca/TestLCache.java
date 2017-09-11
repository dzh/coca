/**
 * 
 */
package coca.ca;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import coca.ca.LCache;

/**
 * @author dzh
 * @date Nov 14, 2016 2:38:27 PM
 * @since 1.0
 */
public class TestLCache {

    public void ofTest() {
        List<String> cache = new LinkedList<>();
        cache.add("1");
        cache.add("2");

        LCache<List<String>> lc = LCache.of(cache);
        System.out.println(lc.cache().get(0));
    }

    @Test
    public void encode() throws Exception {
        System.out.println(URLEncoder.encode(",", "utf-8"));
        System.out.println(URLEncoder.encode(URLEncoder.encode(",", "utf-8"), "utf-8"));
    }

}
