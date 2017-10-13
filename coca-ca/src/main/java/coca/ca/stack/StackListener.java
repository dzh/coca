/**
 * 
 */
package coca.ca.stack;

import java.util.EventListener;

/**
 * @author dzh
 * @date Oct 12, 2017 9:20:32 PM
 * @since 0.0.1
 */
public interface StackListener extends EventListener {

    void stackChange(StackEvent evnt);

}
