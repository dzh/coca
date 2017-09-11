/**
 * 
 */
package coca.co.ins;

/**
 * What?
 * 
 * @author dzh
 * @date Sep 2, 2017 5:44:31 PM
 * @since 0.0.1
 */
public class VoidCoIns extends BasicCoIns<Void> {

    public static final VoidCoIns VOID = new VoidCoIns();

    public VoidCoIns() {
        super(Ins.VOID);
    }

}
