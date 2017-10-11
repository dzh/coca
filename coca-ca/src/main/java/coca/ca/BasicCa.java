/**
 * 
 */
package coca.ca;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author dzh
 * @date Sep 29, 2017 3:37:23 PM
 * @since 0.0.1
 */
public class BasicCa<C extends Closeable> implements Ca<C> {

    private String name;

    private C cache;

    private volatile boolean closed = false;
    private CaType type;

    public BasicCa(String name, C ca, CaType type) {
        this.name = name;
        this.cache = ca;
        this.type = type;
    }

    @Override
    public void close() throws IOException {
        try {
            cache.close();
        } finally {
            closed = true;
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public C ca() {
        return cache;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Ca) { return ((Ca<?>) obj).name().equals(this.name); }
        return false;
    }

    @Override
    public <T> CaValue<T> read(String key) {
        if (isClosed()) throw new CaException(name + " has been closed! ");
        return null;
    }

    @Override
    public <T> boolean write(CaValue<T> val) {
        if (isClosed()) throw new CaException(name + " has been closed!");
        return false;
    }

    @Override
    public CaType type() {
        return type;
    }

    @Override
    public String toString() {
        return name();
    }

}
