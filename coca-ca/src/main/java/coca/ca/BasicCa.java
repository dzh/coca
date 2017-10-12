/**
 * 
 */
package coca.ca;

import java.io.IOException;

/**
 * @author dzh
 * @date Sep 29, 2017 3:37:23 PM
 * @since 0.0.1
 */
public abstract class BasicCa<K, V> implements Ca<K, V> {

    private String name;

    private volatile boolean closed = false;
    private CaType type;

    public BasicCa(String name, CaType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Ca) { return ((Ca<?, ?>) obj).name().equals(this.name); }
        return false;
    }

    @Override
    public CaValue<K, V> read(K key) {
        if (isClosed()) throw new CaException(name + " has been closed! ");
        return doRead(key);
    }

    protected abstract CaValue<K, V> doRead(K key);

    @Override
    public boolean write(CaValue<K, V> val) {
        if (isClosed()) throw new CaException(name + " has been closed!");
        return doWrite(val);
    }

    protected abstract boolean doWrite(CaValue<K, V> val);

    @Override
    public CaType type() {
        return type;
    }

    @Override
    public String toString() {
        return name();
    }

}
