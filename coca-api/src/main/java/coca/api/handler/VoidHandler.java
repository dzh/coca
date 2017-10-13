/**
 * 
 */
package coca.api.handler;

/**
 * @author dzh
 * @date Oct 13, 2017 4:29:47 PM
 * @since 0.0.1
 */
public class VoidHandler extends InsHandler<Void> {

    public static final VoidHandler VOID = new VoidHandler();

    public VoidHandler() {
        super(null);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOG.info("Hi! I'm void.");
    }

}
