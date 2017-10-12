/**
 * 
 */
package coca.ca.stack.policy;

import coca.ca.stack.CaStack;

/**
 * @author dzh
 * @date Nov 14, 2016 12:34:29 PM
 */
public interface CaPolicy<K, V> extends CaRPolicy<K, V>, CaWPolicy<K, V> {

    void stack(CaStack<K, V> s);

    CaStack<K, V> stack();

}
