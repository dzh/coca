/**
 * 
 */
package coca.redis.ca;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import coca.ca.BasicCa;
import coca.ca.CaValue;

/**
 * @author dzh
 * @date Oct 12, 2017 4:12:55 PM
 * @since 0.0.1
 */
public class CaRedis<K, V> extends BasicCa<K, V> {

    private RedissonClient redisson;

    public CaRedis(String name) {
        this(name, Redisson.create());
    }

    public CaRedis(String name, RedissonClient redisson) {
        super(name, CaType.Remote);
        this.redisson = redisson;
    }

    @Override
    protected CaValue<K, V> doRead(K key) {
        RBucket<V> bucket = redisson.getBucket(key.toString());
        return CaValue.newVal(key, bucket.get());
    }

    @Override
    protected boolean doWrite(CaValue<K, V> val) {
        RBucket<V> bucket = redisson.getBucket(val.key().toString());
        V v = val.value();
        if (v == null) {
            bucket.delete();
        } else {
            long ttl = val.ttl();
            if (ttl > 0) {
                bucket.set(v, ttl, TimeUnit.MILLISECONDS);
            } else {
                bucket.set(v);
            }
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        if (isClosed()) return;
        super.close();
        if (redisson != null) redisson.shutdown(2, 15, TimeUnit.SECONDS);// TODO
    }

}
