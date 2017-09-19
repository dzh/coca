/**
 * 
 */
package coca.co.init;

import coca.co.Co;
import coca.co.CoConst;

/**
 * Initializer of Co
 * 
 * @author dzh
 * @date Sep 2, 2017 10:06:36 PM
 * @since 0.0.1
 */
public interface CoInit<T> extends CoConst {

    /**
     * @throws CoInitException
     */
    Co init(T conf);

}
