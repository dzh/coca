/**
 * 
 */
package coca.ca.stack;

import java.util.Optional;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.ca.Ca;
import coca.ca.CaException;
import coca.ca.CaValue;
import coca.ca.stack.pointer.CaPointer;
import coca.ca.stack.policy.CaPolicy;

/**
 * @author dzh
 * @date Sep 29, 2017 3:48:26 PM
 * @since 0.0.1
 */
public class BasicStack implements CaStack {

    static Logger LOG = LoggerFactory.getLogger(BasicStack.class);

    private Stack<Ca<?>> stack = new Stack<>();

    private CaPolicy policy;

    private String name;

    public BasicStack(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#push(coca.ca.Ca)
     */
    @Override
    public boolean push(Ca<?> ca) {
        stack.push(ca);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#pop()
     */
    @Override
    public Ca<?> pop() {
        return stack.pop();
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#read(java.lang.String)
     */
    @Override
    public <T> CaValue<T> read(String key) {
        if (!policy.isReadable()) { throw new CaException(name + " is not readable for key:" + key); }

        if (stack.isEmpty()) return null;

        CaValue<T> val = null;
        CaPointer rp = policy.rp(key);
        while (rp.hasNext()) {
            val = rp.next().read(key);
            if (val != null) break;
        }
        // TODO async
        Optional.<CaValue<T>> ofNullable(val).ifPresent(v -> {
            if ((policy.rop() & CaPolicy.ROP_BACK_WRITE) > 0) {
                writeInner(rp.reverse(), v);
            }
        });
        return val;
    }

    protected CaStack writeInner(CaPointer wp, CaValue<?> val) {
        while (wp.hasNext()) {
            Ca<?> ca = wp.next();
            if (ca.write(val)) {
                if (!hasWop(CaPolicy.WOP_ALL_WRITE)) break;
            } else {
                LOG.error("{} write {} failed!", ca, val); // TODO to handle
                if (hasWop(CaPolicy.WOP_ABORT_ON_FAIL)) break;
            }
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#write(coca.ca.CaValue)
     */
    @Override
    public <T> CaStack write(CaValue<T> val) {
        if (!policy.isWritable()) { throw new CaException(name + " is not writable for key:" + val); }

        CaPointer wp = policy.wp(val);
        return writeInner(wp, val);
    }

    protected boolean hasWop(long op) {
        return (policy.wop() & op) > 0;
    }

    protected boolean hasRop(long op) {
        return (policy.rop() & op) > 0;
    }

    @Override
    public CaStack withPolicy(CaPolicy p) {
        this.policy = p;
        p.stack(this);
        return this;
    }

    @Override
    public void close() {
        stack.clear();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public Ca<?> cache(int index) {
        int size = size() - 1;
        if (index < 0 || index > size) return null;

        return stack.elementAt(size - 1 - index);
    }

}
