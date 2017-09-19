/**
 * 
 */
package coca.co.init;

/**
 * @author dzh
 * @date Sep 19, 2017 3:04:02 PM
 * @since 0.0.1
 */
public class CoInitException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 8103668390498944365L;

    public CoInitException() {
        super();
    }

    public CoInitException(String s) {
        super(s);
    }

    public CoInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoInitException(Throwable cause) {
        super(cause);
    }

}
