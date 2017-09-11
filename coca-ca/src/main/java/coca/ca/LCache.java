/**
 * 
 */
package coca.ca;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * cache level
 * 
 * @author dzh
 * @date Nov 14, 2016 1:53:40 PM
 */
public class LCache<C> implements Closeable {

    static final Logger LOG = LoggerFactory.getLogger(LCache.class);

    private String name;
    private C cache;
    private CaView view;

    public String name() {
        return name;
    }

    public C cache() {
        return cache;
    }

    public CaView view() {
        return view;
    }

    public LCache<?> prev() {
        return view.prev();
    }

    public LCache<?> next() {
        return view.next();
    }

    private LCache() {
    }

    public static final <T> LCache<T> of(T c) {
        LCache<T> lc = new LCache<T>();
        lc.cache = c;
        return lc;
    }

    @Override
    public String toString() {
        return name + "@" + String.valueOf(view) + "/" + String.valueOf(cache);
    }

    @Override
    public void close() throws IOException {
        // if (cache instanceof Closeable) {
        // ((Closeable) cache).close();
        // }
    }

}
