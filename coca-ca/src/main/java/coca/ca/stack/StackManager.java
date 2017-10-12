/**
 * 
 */
package coca.ca.stack;

/**
 * @author dzh
 * @date Oct 12, 2017 10:21:50 AM
 * @since 0.0.1
 */
public class StackManager {

    public static final <K, V> CaStack<K, V> newStack(String name) {
        return new BasicStack<K, V>(name);
    }

}
