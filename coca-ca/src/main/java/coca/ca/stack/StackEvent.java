/**
 * 
 */
package coca.ca.stack;

import java.util.EventObject;

import coca.ca.Ca;
import coca.ca.Ca.CaType;
import coca.ca.CaValue;

/**
 * @author dzh
 * @date Oct 12, 2017 9:25:06 PM
 * @since 0.0.1
 */
public class StackEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = -4459138654934874675L;

    private Ca<?, ?> ca;
    private CaValue<?, ?> val;

    public StackEvent(CaStack<?, ?> stack, Ca<?, ?> ca, CaValue<?, ?> val) {
        super(stack);
    }

    public static final StackEvent newEvent(CaStack<?, ?> stack, Ca<?, ?> ca, CaValue<?, ?> val) {
        return new StackEvent(stack, ca, val);
    }

    public String getStackName() {
        return ((CaStack<?, ?>) super.getSource()).name();
    }

    public CaStack<?, ?> stack() {
        return (CaStack<?, ?>) this.getSource();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("[stack=").append(getStackName());
        sb.append("; ca=").append(ca().name());
        sb.append("; key=").append(key());
        // sb.append("; oldValue=").append(getOldValue());
        // sb.append("; newValue=").append(getNewValue());
        sb.append("; value=").append(value());
        return sb.append("]").toString();
    }

    public boolean isLocalChanged() {
        return ca.type() == CaType.Local;
    }

    public Ca<?, ?> ca() {
        return ca;
    }

    public Object key() {
        return val.key();
    }

    public Object value() {
        return val.value();
    }

    public CaValue<?, ?> val() {
        return val;
    }

}
