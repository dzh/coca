/**
 * 
 */
package coca.api;

import coca.co.CoConst;
import coca.co.ins.CoIns.Ins;
import coca.co.ins.InsConst;

/**
 * @author dzh
 * @date Oct 13, 2017 11:19:16 AM
 * @since 0.0.1
 */
public interface CocaConst extends CoConst, InsConst {

    String F_stack = "stack";
    String F_ca = "ca";
    String F_key = "key";

    /************************** Ins Definition[1025,2048] ************************/
    Ins EVICT = new Ins(1025, "evict", "stack ca key"); // evict key from cache

    /********************** Coca configuration **********************/
    String P_COCA_HANDLER_THREDNUM = "coca.handler.threadnum";
    String P_COCA_INS_IGNORE_SELF = "coca.ins.ignore.self";
}
