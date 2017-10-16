/**
 * 
 */
package coca.ca.stack;

import coca.ca.stack.policy.StackPolicy;

/**
 * @author dzh
 * @date Oct 12, 2017 10:21:50 AM
 * @since 0.0.1
 */
public class StackManager {

    public static final <K, V> CaStack<K, V> newStack(String name) {
        CaStack<K, V> stack = new BasicStack<K, V>(name);
        stack.withPolicy(new StackPolicy<K, V>()); // TODO
        return stack;
    }

}
