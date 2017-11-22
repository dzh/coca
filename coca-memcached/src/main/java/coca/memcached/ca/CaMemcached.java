/**
 * 
 */
package coca.memcached.ca;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.ca.BasicCa;
import coca.ca.CaValue;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * http://fnil.net/xmemcached/
 * 
 * @author dzh
 * @date Nov 22, 2017 5:18:29 PM
 * @since 1.0.0
 */
public class CaMemcached<K, V> extends BasicCa<K, V> {

    static Logger LOG = LoggerFactory.getLogger(CaMemcached.class);

    private MemcachedClient mc;

    public CaMemcached() {
        this("ca-memcached");
    }

    public CaMemcached(String name) {
        this(name, simpleClient());
    }

    private static MemcachedClient simpleClient() {
        // AuthDescriptor ad = AuthDescriptor.typical("coca", "coca");
        MemcachedClient mc = null;
        try {
            // mc = new MemcachedClient(new
            // ConnectionFactoryBuilder().setProtocol(ConnectionFactoryBuilder.Protocol.BINARY).build(),
            // AddrUtil.getAddresses("localhost:11211"));
            MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:11211"));
            mc = builder.build();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return mc;
    }

    /**
     * 
     * @param name
     * @param mc
     */
    public CaMemcached(String name, MemcachedClient mc) {
        super(name, CaType.Remote);
        this.mc = mc;
        if (mc == null) throw new RuntimeException("mc is null");
    }

    @Override
    protected CaValue<K, V> doRead(K key) {
        try {
            return CaValue.newVal(key, mc.<V> get(key.toString()));
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            LOG.error(e.getMessage(), e);
        }
        return CaValue.newVal(key);
    }

    @Override
    protected boolean doWrite(CaValue<K, V> val) {
        K k = val.key();
        V v = val.value();
        try {
            if (v == null) {
                mc.delete(k.toString());
            } else {
                long ttl = val.ttl();
                if (ttl > 0) {
                    mc.set(k.toString(), (int) ttl / 1000, v);
                } else {
                    mc.set(k.toString(), 0, v);
                }
            }
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        if (isClosed()) return;
        super.close();
        // if (mc != null) {
        // mc.flush();
        // mc.shutdown(60, TimeUnit.SECONDS);
        // }
        if (mc != null) {
            try {
                mc.flushAll();
            } catch (TimeoutException | InterruptedException | MemcachedException e) {
                LOG.warn(e.getMessage(), e);
            } finally {
                mc.shutdown();
            }
        }
    }

}
