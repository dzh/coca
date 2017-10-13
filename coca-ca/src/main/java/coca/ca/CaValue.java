/**
 * 
 */
package coca.ca;

/**
 * @author dzh
 * @date Sep 29, 2017 2:04:53 PM
 * @since 0.0.1
 */
public class CaValue<K, V> {

    public static final CaValue<Void, Void> EMPTY = new CaValue<>();

    private K key;

    /**
     * If value==null, then cache will evict the key
     */
    private V value;

    // time to live
    private long ttl;

    public CaValue() {}

    public CaValue(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public static final <K, V> CaValue<K, V> newVal(K k, V v) {
        return new CaValue<K, V>(k, v);
    }

    public CaValue<K, V> key(K key) {
        this.key = key;
        return this;
    }

    public CaValue<K, V> value(V value) {
        this.value = value;
        return this;
    }

    public K key() {
        return this.key;
    }

    public V value() {
        return value;
    }

    public long ttl() {
        return ttl;
    }

    public CaValue<K, V> ttl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public String toString() {
        return key + ":" + value;
    }

}
