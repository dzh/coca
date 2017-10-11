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
public interface CaPointer {

    boolean hasNext();

    Ca<?> next();

    CaStack stack();

    int index();

    CaPointer index(int index);

    /**
     * 
     */
    CaPointer reverse();

}
