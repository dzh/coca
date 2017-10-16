package coca.ca.stack.pointer;

import coca.ca.Ca;
import coca.ca.stack.CaStack;

/**
 * 
 * @author dzh
 * @date Oct 11, 2017 4:07:30 PM
 * @since 0.0.1
 */
public class StackPointer<K, V> implements CaPointer<K, V> {

    private CaStack<K, V> stack;

    protected boolean down = true;

    private int index = 0;

    public StackPointer(CaStack<K, V> stack) {
        this(stack, 0, false);
    }

    public StackPointer(CaStack<K, V> stack, int index, boolean down) {
        if (stack == null) throw new NullPointerException("stack is null");

        this.stack = stack;
        this.index = index;
        this.down = down;
    }

    public static final <K, V> StackPointer<K, V> newReadPointer(CaStack<K, V> stack, int index) {
        return new StackPointer<K, V>(stack, index, true);
    }

    public static final <K, V> StackPointer<K, V> newWritePointer(CaStack<K, V> stack, int index) {
        return new StackPointer<K, V>(stack, stack.size() - 1, false);
    }

    @Override
    public CaStack<K, V> stack() {
        return stack;
    }

    @Override
    public boolean hasNext() {
        return down ? index < stack().size() : index > -1;
    }

    @Override
    public Ca<K, V> next() {
        return stack().ca(down ? index++ : index--);
    }

    @Override
    public CaPointer<K, V> reverse() {
        index = down ? index - 2 : index + 2;
        down = down ? false : true;
        return this;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public CaPointer<K, V> index(int index) {
        this.index = index;
        return this;
    }

}
