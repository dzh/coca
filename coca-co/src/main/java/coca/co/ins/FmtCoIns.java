/**
 * 
 */
package coca.co.ins;

/**
 * Custom {@link Ins}'s format
 * 
 * @author dzh
 * @date Sep 7, 2017 2:41:16 PM
 * @since 0.0.1
 */
public class FmtCoIns<T> extends BasicCoIns<T> {

    private String format;

    public FmtCoIns(Ins ins) {
        super(ins);
        this.format = ins.format();
    }

    public FmtCoIns(CoIns<?> ins) {
        super(ins);
        this.format = ins.ins().format();
    }

    public String format() {
        return this.format;
    }

}
