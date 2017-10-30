/**
 * 
 */
package coca.ehcache.ca;

import org.ehcache.Cache;

import coca.ca.BasicCa;
import coca.ca.CaValue;

/**
 * @author dzh
 * @date Oct 30, 2017 12:23:43 PM
 * @since 0.0.1
 */
public class CaEhcache<K, V> extends BasicCa<K, V> {

    private Cache<K, V> ca;

    public CaEhcache(String name, Cache<K, V> ca) {
        super(name, CaType.Local);
        this.ca = ca;
    }

    @Override
    protected CaValue<K, V> doRead(K key) {
        return CaValue.newVal(key, ca.get(key));
    }

    @Override
    protected boolean doWrite(CaValue<K, V> val) {
        if (val.value() == null) {
            ca.remove(val.key());
        } else {
            ca.put(val.key(), val.value());
        }
        return true;
    }

}
