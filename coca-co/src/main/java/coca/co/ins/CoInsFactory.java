/**
 * 
 */
package coca.co.ins;

import coca.co.ins.CoIns.Ins;

/**
 * @author dzh
 * @date Sep 2, 2017 5:32:04 PM
 * @since 0.0.1
 */
public class CoInsFactory {

    public CoInsFactory() {}

    public CoIns<?> newIns(CoIns.Ins ins) {
        if (ins == null) return VoidCoIns.VOID;

        CoIns<?> coIns = isInnerIns(ins) ? innerIns(ins) : customIns(ins);

        return coIns == null ? defaultIns(ins) : coIns;
    }

    protected CoIns<?> innerIns(Ins ins) {
        // if (Ins.JOIN.equals(ins) || Ins.QUIT.equals(ins)) { return new TextCoIns(ins); }
        return new TextCoIns(ins);
    }

    protected CoIns<?> customIns(Ins ins) {
        return null;
    }

    protected CoIns<?> defaultIns(Ins ins) {
        return new TextCoIns(ins);
    }

    // public <T> AckCoIns toAck(CoIns<T> ins, InsFormat<T, AckCoIns.Data> format) {
    // AckCoIns ack = new AckCoIns(ins.ins());
    // ack.to(ins.toGroup())
    // }
    public final CoIns<AckCoIns.Ack> newAck(CoIns<?> ins, int code, String msg) {
        return new AckCoIns(InsConst.ACK).data(code, msg).id(ins.id());
    }

    /**
     * 
     * @param ins
     *            {@link Ins}
     * @return true if code in [0,1024]
     */
    public boolean isInnerIns(Ins ins) {
        int code = ins.code();
        return code >= 0 && code <= 1024;
    }

    /**
     * 
     * @param name
     *            CoGroup.name
     * @param id
     *            Co.id
     * @return
     */
    public final CoIns<String> newJoin(String name, String... ids) {
        return new TextCoIns(Ins.JOIN).data(toTextIns(name, ids));
    }

    /**
     * 
     * @param name
     *            CoGroup.name
     * @param id
     *            Co.id
     * @return
     */
    public final CoIns<String> newQuit(String name, String... ids) {
        return new TextCoIns(Ins.QUIT).data(toTextIns(name, ids));
    }

    protected String toTextIns(String data, String... others) {
        if (data == null) data = "";
        StringBuilder buf = new StringBuilder(others.length * (32 + Ins.FMT_SEPR.length()));
        buf.append(data);
        for (int i = 0; i < others.length; i++) {
            buf.append(Ins.FMT_SEPR);
            buf.append(others[i]);
        }
        return buf.toString();
    }

    /**
     * Invoked by Co.close
     */
    public void close() {

    }

}
