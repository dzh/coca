/**
 * 
 */
package coca.co.ins;

import java.io.Closeable;
import java.io.IOException;

import coca.co.ins.CoIns.Ins;

/**
 * @author dzh
 * @date Sep 2, 2017 5:32:04 PM
 * @since 0.0.1
 */
public class CoInsFactory implements Closeable {

    public CoInsFactory() {}

    public CoIns<?> newIns(CoIns.Ins ins) {
        if (ins == null) return VoidCoIns.VOID;

        CoIns<?> coIns = isInnerIns(ins) ? innerIns(ins) : customIns(ins);
        // if (coIns == null && isCoIns) {
        // if (code) if (code == Ins.JOIN.code() || code == Ins.QUIT.code()) return new
        // TextCoIns(ins).data(String.valueOf(data));
        // }

        return coIns == null ? VoidCoIns.VOID : coIns;
    }

    protected CoIns<?> innerIns(Ins ins) {
        if (Ins.JOIN.equals(ins) || Ins.QUIT.equals(ins)) { return new TextCoIns(ins); }
        return null;
    }

    protected CoIns<?> customIns(Ins ins) {
        return null;
    }

    /**
     * 
     * @param ins
     *            {@link Ins}
     * @return true if code in [0,1024]
     */
    protected boolean isInnerIns(Ins ins) {
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
        return new TextCoIns(Ins.JOIN).data(toTextIns(name, ids));
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

    @Override
    public void close() throws IOException {}

}
