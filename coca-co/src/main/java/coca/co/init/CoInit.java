/**
 * 
 */
package coca.co.init;

import coca.co.Co;

/**
 * Initializer for Co
 * 
 * @author dzh
 * @date Sep 2, 2017 10:06:36 PM
 * @since 0.0.1
 */
public interface CoInit {

    /**
     * invoked after
     * 
     * @param co
     */
    Co init(Co co);

}
