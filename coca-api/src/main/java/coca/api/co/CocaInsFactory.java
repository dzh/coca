/**
 * 
 */
package coca.api.co;

import coca.api.CocaConst;
import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;
import coca.co.ins.CoInsFactory;

/**
 * 
 * @author dzh
 * @date Oct 13, 2017 11:12:56 AM
 * @since 0.0.1
 */
public class CocaInsFactory extends CoInsFactory {

    protected CoIns<?> customIns(Ins ins) {
        if (CocaConst.EVICT.equals(ins)) { return new StackCoIns(ins); }
        return null;
    }

}
