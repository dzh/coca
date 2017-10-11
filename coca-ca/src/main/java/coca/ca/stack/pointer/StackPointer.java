package coca.ca.stack.pointer;

import coca.ca.Ca;
import coca.ca.stack.CaStack;

/**
 * 
 * @author dzh
 * @date Oct 11, 2017 4:07:30 PM
 * @since 0.0.1
 */
public class StackPointer implements CaPointer {

    private CaStack stack;

    protected boolean up = false;

    private int index = 0;

    public StackPointer(CaStack stack) {
        this(stack, 0, false);
    }

    public StackPointer(CaStack stack, int index, boolean up) {
        if (stack == null) throw new NullPointerException("stack is null");

        this.stack = stack;
        this.index = index;
        this.up = up;
    }

    @Override
    public CaStack stack() {
        return stack;
    }

    @Override
    public boolean hasNext() {
        return up ? index > -1 : index < stack().size();
    }

    @Override
    public Ca<?> next() {
        return stack().cache(up ? index-- : index++);
    }

    @Override
    public CaPointer reverse() {
        index = up ? index + 1 : index - 1;
        up = up ? false : true;
        return this;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public CaPointer index(int index) {
        this.index = index;
        return this;
    }

}
