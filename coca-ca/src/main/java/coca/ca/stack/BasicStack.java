/**
 * 
 */
package coca.ca.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.ca.Ca;
import coca.ca.Ca.CaType;
import coca.ca.CaException;
import coca.ca.CaValue;
import coca.ca.VoidCa;
import coca.ca.stack.pointer.CaPointer;
import coca.ca.stack.policy.CaPolicy;

/**
 * @author dzh
 * @date Sep 29, 2017 3:48:26 PM
 * @since 0.0.1
 */
public class BasicStack<K, V> implements CaStack<K, V> {

    static Logger LOG = LoggerFactory.getLogger(BasicStack.class);

    private Stack<Ca<K, V>> stack = new Stack<>();

    private CaPolicy<K, V> policy;

    private String name;

    public BasicStack(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#push(coca.ca.Ca)
     */
    @Override
    public boolean push(Ca<K, V> ca) {
        stack.push(ca);
        return true;
    }

    @Override
    public Ca<K, V> peek() {
        return stack.peek();
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#pop()
     */
    @Override
    public Ca<K, V> pop() {
        return stack.pop();
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#read(java.lang.String)
     */
    @Override
    public CaValue<K, V> read(K key) {
        if (stack.isEmpty()) return null;
        if (!policy.isReadable()) { throw new CaException(name + " is not readable for key:" + key); }

        Ca<K, V> ca = null;
        CaValue<K, V> val = null;
        CaPointer<K, V> rp = policy.rp(key);
        while (rp.hasNext()) {
            ca = rp.next();
            val = ca.read(key).sync(false);

            if (val != null && val.value() != null) {
                // TODO async
                if (ca.type() == CaType.Remote && (policy.rop() & CaPolicy.ROP_BACK_WRITE) > 0) {
                    writeInner(rp.reverse(), val);
                }
                break;
            }
        }
        return val;
    }

    protected CaStack<K, V> writeInner(CaPointer<K, V> wp, CaValue<K, V> val) {
        if (enableWop(CaPolicy.WOP_ALL_WRITE)) {
            while (wp.hasNext()) {
                if (!writeNext(wp, val)) {
                    if (enableWop(CaPolicy.WOP_ABORT_ON_FAIL)) break;
                }
            }
        } else { // write once
            if (wp.hasNext()) writeNext(wp, val);
        }
        return this;
    }

    private boolean writeNext(CaPointer<K, V> wp, CaValue<K, V> val) {
        Ca<K, V> ca = wp.next();
        if (ca.write(val)) {
            // TODO fire before local write
            fireStackChange(StackEvent.newEvent(this, ca, val));
            return true;
        }

        LOG.error("{} write {} failed!", ca, val); // TODO to handle
        return false;
    }

    protected void fireStackChange(StackEvent evt) {
        for (StackListener l : listeners) {
            l.stackChange(evt);
        }
    }

    /*
     * (non-Javadoc)
     * @see coca.ca.CaStack#write(coca.ca.CaValue)
     */
    @Override
    public CaStack<K, V> write(CaValue<K, V> val) {
        if (stack.isEmpty()) return this;
        if (!policy.isWritable()) { throw new CaException(name + " is not writable for key:" + val); }

        CaPointer<K, V> wp = policy.wp(val);
        return writeInner(wp, val);
    }

    protected boolean enableWop(long op) {
        return (policy.wop() & op) > 0;
    }

    protected boolean enableRop(long op) {
        return (policy.rop() & op) > 0;
    }

    @Override
    public CaStack<K, V> withPolicy(CaPolicy<K, V> p) {
        this.policy = p;
        p.stack(this);
        return this;
    }

    @Override
    public void close() {
        stack.clear();
        // closeStack(); // ca maybe shared among multiple stack
    }

    // protected void closeStack() {
    // List<Ca<K, V>> clone = new ArrayList<>(stack);
    // stack.clear();
    // for (Ca<K, V> ca : clone) {
    // try {
    // ca.close();
    // } catch (IOException e) {
    // LOG.error(e.getMessage(), e);
    // }
    // }
    // }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public Ca<K, V> ca(int index) {
        int size = size();
        if (index < 0 || index >= size) return null;

        return stack.elementAt(size - 1 - index);
    }

    private List<StackListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void addListener(StackListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListener(StackListener l) {
        listeners.remove(l);
    }

    @Override
    public Ca<K, V> ca(String name) {
        int idx = stack.lastIndexOf(new VoidCa<K, V>(name));
        if (idx < 0) return null;
        return stack.get(idx);
    }

    @Override
    public List<Ca<K, V>> asList() {
        return new ArrayList<>(stack);
    }

    @Override
    public String toString() {
        return name;
    }

}
