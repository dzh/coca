/**
 * 
 */
package coca.co;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author dzh
 * @date Sep 8, 2017 6:23:18 PM
 * @since 0.0.1
 */
public interface CoConst {

    Charset UTF8 = StandardCharsets.UTF_8;

    /********************** Co configuration **********************/
    // Co
    String P_CO_INIT = "co.init";
    String P_CO = "co";
    String P_CO_HEARTBEAT_TICK = "co.heartbeat.tick"; // millisecond
    // CoIns
    String P_CO_INS_FACTORY = "co.ins.factory";
    String P_CO_INS_CODECS = "co.ins.codecs";
    // CoIO
    String P_CO_IO = "co.io";
    String P_CO_IO_SELECTOR = "co.io.selector";
    String P_CO_IO_ACTORS = "co.io.actors";

}
