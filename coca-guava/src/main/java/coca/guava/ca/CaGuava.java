/**
 * 
 */
package coca.guava.ca;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import coca.ca.BasicCa;
import coca.ca.CaValue;

/**
 * @author dzh
 * @date Oct 12, 2017 11:46:06 AM
 * @since 0.0.1
 */
public class CaGuava<K, V> extends BasicCa<K, V> {

    private Cache<K, V> ca;

    public CaGuava() {
        this("ca-guava");
    }

    public CaGuava(String name) {
        this(name, CacheBuilder.newBuilder().concurrencyLevel(16).maximumSize(Integer.MAX_VALUE).expireAfterWrite(10, TimeUnit.SECONDS)
                .build());
    }

    public CaGuava(String name, Cache<K, V> ca) {
        super(name, CaType.Local);
        this.ca = ca;
    }

    // custom
    public CaGuava(String name, CacheBuilder<K, V> builder) {
        super(name, CaType.Local);
        this.ca = builder.build();
    }

    @Override
    protected CaValue<K, V> doRead(K key) {
        V val = ca.getIfPresent(key);
        return CaValue.newVal(key, val);
    }

    @Override
    protected boolean doWrite(CaValue<K, V> val) {
        if (val.value() == null) {
            ca.invalidate(val.key());
        } else {
            ca.put(val.key(), val.value());
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        super.close();
        ca.cleanUp();
    }

}
