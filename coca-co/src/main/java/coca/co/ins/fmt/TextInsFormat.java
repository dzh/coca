/**
 * 
 */
package coca.co.ins.fmt;

import coca.co.ins.CoIns.Ins;

/**
 * TODO format expression
 * 
 * @author dzh
 * @date Sep 6, 2017 3:29:44 PM
 * @since 0.0.1
 */
public abstract class TextInsFormat<F> implements InsFormat<String, F> {

    /**
     * Separator char of Ins.format
     */
    private String sepr;

    /**
     * 
     * 
     */
    // @Override
    // public F format(String fmt, String data) {
    // String[] fmtList = parseFmt(fmt);
    // return customFormat(fmtList, data);
    // }

    /**
     * 
     */
    protected String[] split(String text) {
        if (text == null) return new String[0];
        String sepr = sepr();
        return text.split(sepr == null ? Ins.FMT_SEPR : sepr);
    }

    public TextInsFormat<F> sepr(String sepr) {
        this.sepr = sepr;
        return this;
    }

    public String sepr() {
        return this.sepr;
    }

}
