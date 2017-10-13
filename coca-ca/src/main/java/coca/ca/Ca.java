/**
 * 
 */
package coca.ca;

import java.io.Closeable;

/**
 * 
 * @author dzh
 * @date Nov 14, 2016 12:46:26 PM
 * @since 0.0.1
 */
public interface Ca<K, V> extends Closeable {

    String name();

    CaValue<K, V> read(K key);

    /**
     * 
     * @param val
     * @return true if written successfully, otherwise to return false
     */
    boolean write(CaValue<K, V> val);

    boolean isClosed();

    CaType type();

    public static enum CaType {
        Local, Remote
    }

}