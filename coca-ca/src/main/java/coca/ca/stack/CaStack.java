/**
 * 
 */
package coca.ca.stack;

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
public interface CaStack {

    String name();

    /**
     * 
     * @param ca
     * @return true if push successfully
     */
    boolean push(Ca<?> ca);

    /**
     * 
     * @return stack top's ca or null if stack is empty
     */
    Ca<?> pop();

    /**
     * 
     * @return Ca count
     */
    int size();

    /**
     * Top index is 0 </br>
     * Bottom index is size()-1
     * 
     * @param index
     * @return Ca at index location of stack
     */
    Ca<?> cache(int index);

    CaStack withPolicy(CaPolicy p);

    <T> CaValue<T> read(String key);

    <T> CaStack write(CaValue<T> val);

    void close();
}
