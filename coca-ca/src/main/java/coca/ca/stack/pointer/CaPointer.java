/**
 * 
 */
package coca.ca.stack.pointer;

import coca.ca.Ca;
import coca.ca.stack.CaStack;

/**
 * @author dzh
 * @date Sep 29, 2017 6:35:45 PM
 * @since 0.0.1
 */
public interface CaPointer<K, V> {

    boolean hasNext();

    Ca<K, V> next();

    CaStack<K, V> stack();

    int index();

    CaPointer<K, V> index(int index);

    /**
     * 
     */
    CaPointer<K, V> reverse();

}
