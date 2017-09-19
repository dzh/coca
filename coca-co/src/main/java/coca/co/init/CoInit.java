/**
 * 
 */
package coca.co.init;

import java.util.Map;

import coca.co.Co;

/**
 * Initializer of Co
 * 
 * @author dzh
 * @date Sep 2, 2017 10:06:36 PM
 * @since 0.0.1
 */
public interface CoInit {

    String P_CO_INIT = "co.init";

    String P_CO = "co";

    String P_CO_IO = "co.io";

    String P_CO_IO_SELECTOR = "co.io.selector";

    /**
     * invoked after
     * 
     * @param co
     * @throws CoInitException
     */
    Co init(Map<String, String> conf);

}
