/**
 * 
 */
package coca.co;

import java.util.Collection;

/**
 * Co-Group Features:
 * <ul>
 * <li>sync CoIns in CoGroup</li>
 * </ul>
 * 
 * @author dzh
 * @date Nov 12, 2016 11:07:41 PM
 * @since 0.0.1
 */
public interface CoGroup {

    String name();

    Collection<Co> members();

    boolean contain(Co member);

    boolean join(Co co);

    boolean quit(Co co);

}
