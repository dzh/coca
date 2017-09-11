/**
 * 
 */
package coca.co.io;

import coca.co.ins.CoIns;

/**
 * Ins Send Status
 * 
 * @author dzh
 * @date Sep 8, 2017 4:49:29 PM
 * @since 0.0.1
 */
public class PacketResult {

    /**
     * {@link CoIns}'s id
     */
    public String id;

    /**
     * io status
     */
    public IOSt st;

    public static enum IOSt {
        SEND, RECV, SUCC, FAIL
    }

}
