/**
 * 
 */
package coca.ca;

/**
 * @author dzh
 * @date Sep 29, 2017 2:04:53 PM
 * @since 0.0.1
 */
public class CaValue<T> {

    public static final CaValue<Void> EMPTY = new CaValue<>();

    private String key;
    private T value;

    public CaValue<T> key(String key) {
        this.key = key;
        return this;
    }

    public CaValue<T> value(T value) {
        this.value = value;
        return this;
    }

    public String key() {
        return this.key;
    }

    public T value() {
        return value;
    }

    @Override
    public String toString() {
        return key + ":" + value;
    }

}
