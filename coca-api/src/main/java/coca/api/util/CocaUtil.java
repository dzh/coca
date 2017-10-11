/**
 * 
 */
package coca.api.util;

import java.lang.management.ManagementFactory;

/**
 * @author dzh
 * @date Nov 14, 2016 2:33:29 PM
 * @since 1.0
 */
public class CocaUtil {

    /**
     * 机器名+进程ID+2位随机数
     * 
     * @return
     */
    @Deprecated
    public static final String genCocaId() {
        return "" + getPID() + "";
    }

    public static final String getPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.substring(0, name.indexOf('@'));
    }

}
