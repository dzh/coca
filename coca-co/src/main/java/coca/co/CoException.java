/**
 * 
 */
package coca.co;

/**
 * @author dzh
 * @date Aug 25, 2017 12:41:05 PM
 * @since 0.0.1
 */
public class CoException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */

    public CoException() {
        super();
    }

    public CoException(String s) {
        super(s);
    }

    public CoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoException(Throwable cause) {
        super(cause);
    }

}
