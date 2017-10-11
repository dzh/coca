/**
 * 
 */
package coca.ca;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * @author dzh
 * @date Nov 14, 2016 2:38:27 PM
 * @since 1.0
 */
public class TestCache {

    @Test
    public void ofTest() {
        List<String> cache = new LinkedList<>();
        cache.add("1");
        cache.add("2");

        int i = 0;
        System.out.println(cache.get(++i));
        System.out.println(i);
    }

    public void encode() throws Exception {
        System.out.println(URLEncoder.encode(",", "utf-8"));
        System.out.println(URLEncoder.encode(URLEncoder.encode(",", "utf-8"), "utf-8"));
    }

}
