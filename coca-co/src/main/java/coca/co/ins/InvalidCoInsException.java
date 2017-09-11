/**
 * 
 */
package coca.co.ins;

/**
 * @author dzh
 * @date Sep 7, 2017 2:37:08 PM
 * @since 0.0.1
 */
public class InvalidCoInsException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 8887538829404358771L;

    public InvalidCoInsException() {
        super();
    }

    public InvalidCoInsException(String s) {
        super(s);
    }

    public InvalidCoInsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCoInsException(Throwable cause) {
        super(cause);
    }

}
