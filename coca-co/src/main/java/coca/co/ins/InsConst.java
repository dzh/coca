/**
 * 
 */
package coca.co.ins;

import coca.co.ins.CoIns.Ins;

/**
 * Ins Constants
 * 
 * @author dzh
 * @date Sep 6, 2017 2:06:48 PM
 * @since 0.0.1
 */
public interface InsConst {
    /****************************** Ins Definition ******************************/
    // [0,1024]
    Ins VOID = new Ins(0, "void", "");

    /**
     * code: 0-succ 1-fail
     * msg: code's detail that maybe nil
     */
    Ins ACK = new Ins(1, "ack", "code msg");
    /**
     * name: CoGropu.name
     * id: Co.id
     */
    Ins JOIN = new Ins(2, "join", "name id");
    /**
     * name: CoGropu.name
     * id: Co.id
     */
    Ins QUIT = new Ins(3, "quit", "name id");

    Ins HEARTBEAT = new Ins(4, "heartbeat", "");

    /****************************** Ins Constants ******************************/
    String ID = "id";
    String NAME = "name";
    String GROUP = "group";
}
