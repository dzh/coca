/**
 * 
 */
package coca.ca;

/**
 * @author dzh
 * @date Aug 25, 2017 12:41:05 PM
 * @since 0.0.1
 */
public class CaException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -4814777955302294846L;

    public CaException() {
        super();
    }

    public CaException(String s) {
        super(s);
    }

    public CaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaException(Throwable cause) {
        super(cause);
    }

}
