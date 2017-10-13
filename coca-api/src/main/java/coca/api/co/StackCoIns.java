/**
 * 
 */
package coca.api.co;

import coca.api.CocaConst;
import coca.ca.stack.CaStack;
import coca.co.ins.TextCoIns;
import coca.co.util.IDUtil;

/**
 * @author dzh
 * @date Oct 13, 2017 2:13:17 PM
 * @since 0.0.1
 */
public class StackCoIns extends TextCoIns {

    private CaStack<?, ?> stack;

    public StackCoIns() {
        super(CocaConst.EVICT);
        id(stack.name() + "_" + IDUtil.uuid());
    }

    public static final StackCoIns newStackIns(CaStack<?, ?> stack, Ins ins) {
        StackCoIns coIns = new StackCoIns(ins);
        coIns.stack(stack);
        return coIns;
    }

    public StackCoIns(Ins ins) {
        super(ins);
    }

    public CaStack<?, ?> stack() {
        return stack;
    }

    public StackCoIns stack(CaStack<?, ?> stack) {
        this.stack = stack;
        return this;
    }

}
