/**
 * 
 */
package coca.ca.stack;

import java.util.List;

import coca.ca.Ca;
import coca.ca.CaValue;
import coca.ca.stack.policy.CaPolicy;

/**
 * TODO async to read write
 * 
 * @author dzh
 * @date Sep 29, 2017 12:40:44 PM
 * @since 0.0.1
 */
public interface CaStack<K, V> {

    String name();

    /**
     * 
     * @param ca
     * @return true if push successfully
     */
    boolean push(Ca<K, V> ca);

    /**
     * 
     * @return stack top's ca or null if stack is empty
     */
    Ca<K, V> pop();

    /**
     * 
     * @return stack top's ca or null if stack is empty
     */
    Ca<K, V> peek();

    List<Ca<K, V>> asList();

    /**
     * 
     * @return Ca count
     */
    int size();

    /**
     * <pre>
     * Top index is 0
     * Bottom index is size()-1
     * </pre>
     * 
     * @param index
     * @return Ca at index location of stack
     */
    Ca<K, V> ca(int index);

    Ca<K, V> ca(String name);

    CaStack<K, V> withPolicy(CaPolicy<K, V> p);

    CaValue<K, V> read(K key);

    CaStack<K, V> write(CaValue<K, V> val);

    void addListener(StackListener l);

    void removeListener(StackListener l);

    void close();
}
