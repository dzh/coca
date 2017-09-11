/**
 * 
 */
package coca.co;

import java.util.EventListener;

/**
 * @author dzh
 * @date Nov 14, 2016 1:12:20 PM
 * @since 1.0
 */
public interface CoListener extends EventListener {

    void accept(CoEvent<?> event);

}
