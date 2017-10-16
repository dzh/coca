/**
 * 
 */
package coca.api.handler;

import coca.co.ins.VoidCoIns;

/**
 * @author dzh
 * @date Oct 13, 2017 4:29:47 PM
 * @since 0.0.1
 */
public class VoidHandler extends InsHandler<Void> {

    public static final VoidHandler VOID = new VoidHandler();

    public VoidHandler() {
        super(VoidCoIns.VOID);
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
