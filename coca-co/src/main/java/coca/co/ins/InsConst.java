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
     * name: CoGropu.name
     * id: Co.id
     */
    Ins JOIN = new Ins(1, "join", "name id");
    /**
     * name: CoGropu.name
     * id: Co.id
     */
    Ins QUIT = new Ins(2, "quit", "name id");

    /****************************** Ins Constants ******************************/
    String ID = "id";
    String NAME = "name";
    String GROUP = "group";
}
