/**
 * 
 */
package coca.co.io.channel;

import coca.co.CoException;

/**
 * @author dzh
 * @date Sep 13, 2017 8:00:14 PM
 * @since 0.0.1
 */
public class CoChannelException extends CoException {

    /**
     * 
     */
    private static final long serialVersionUID = 6259248598194491912L;

    public CoChannelException() {
        super();
    }

    public CoChannelException(String s) {
        super(s);
    }

    public CoChannelException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoChannelException(Throwable cause) {
        super(cause);
    }

}
