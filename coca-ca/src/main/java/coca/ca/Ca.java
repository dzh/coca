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
public interface Ca<C> extends Closeable {

    String name();

    /**
     * @return cache instance
     */
    C ca();

    <T> CaValue<T> read(String key);

    /**
     * 
     * @param val
     * @return true if written successfully, otherwise to return false
     */
    <T> boolean write(CaValue<T> val);

    boolean isClosed();

    CaType type();

    public static enum CaType {
        Local, Remote
    }

}