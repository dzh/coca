/**
 * 
 */
package coca.ca.stack.policy;

import coca.ca.CaValue;
import coca.ca.stack.CaStack;
import coca.ca.stack.pointer.CaPointer;
import coca.ca.stack.pointer.StackPointer;

/**
 * @author dzh
 * @date Sep 29, 2017 5:54:05 PM
 * @since 0.0.1
 */
public class StackPolicy<K, V> implements CaPolicy<K, V> {

    private CaStack<K, V> stack;

    private long rop = CaRPolicy.ROP_BACK_WRITE;

    private long wop = CaWPolicy.WOP_ALL_WRITE & CaWPolicy.WOP_ABORT_ON_FAIL;

    public StackPolicy() {}

    @Override
    public CaStack<K, V> stack() {
        return stack;
    }

    @Override
    public void stack(CaStack<K, V> s) {
        this.stack = s;
    }

    @Override
    public CaPointer<K, V> rp(K key) {
        return StackPointer.newReadPointer(stack, 0);
    }

    @Override
    public long rop(long op) {
        rop = op;
        return rop;
    }

    @Override
    public CaPointer<K, V> wp(CaValue<K, V> val) {
        return StackPointer.newWritePointer(stack, 0);
    }

    @Override
    public long wop(long op) {
        wop = op;
        return wop;
    }

    @Override
    public long rop() {
        return rop;
    }

    @Override
    public long wop() {
        return wop;
    }

}
