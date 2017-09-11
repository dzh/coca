/**
 * 
 */
package coca.co;

import java.util.Collection;

/**
 * <p>
 * Co-Group Features:
 * <ul>
 * <li>sync CoIns in CoGroup</li>
 * </ul>
 * </p>
 * 
 * @author dzh
 * @date Nov 12, 2016 11:07:41 PM
 * @since 0.0.1
 */
public interface CoGroup {

    String name();

    Collection<Co> members();

    boolean join(Co co);

    boolean quit(Co co);

}
