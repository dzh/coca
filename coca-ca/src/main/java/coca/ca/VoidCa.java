/**
 * 
 */
package coca.ca;

/**
 * @author dzh
 * @date Oct 13, 2017 5:17:02 PM
 * @since 0.0.1
 */
public class VoidCa<K, V> extends BasicCa<K, V> {

    public VoidCa(String name) {
        super(name);
    }

    @Override
    protected CaValue<K, V> doRead(K key) {
        return null;
    }

    @Override
    protected boolean doWrite(CaValue<K, V> val) {
        return false;
    }

}
