/**
 * 
 */
package coca.co;

import java.util.EventObject;

/**
 * @author dzh
 * @date Nov 12, 2016 11:09:14 PM
 * @since 1.0
 */
public class CoEvent<T> extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = -623491554143492237L;

    private EventType type;

    private T content;

    public CoEvent(Object source) {
        super(source);
    }

    public CoEvent<T> type(EventType type) {
        this.type = type;
        return this;
    }

    public CoEvent<T> content(T content) {
        this.content = content;
        return this;
    }

    public EventType type() {
        return type;
    }

    public T content() {
        return content;
    }

    public enum EventType {
        PUB_INS, SUB_INS
    }

}
