/**
 * 
 */
package coca.co.ins.actor;

import coca.co.io.CoIO;

/**
 * @author dzh
 * @date Sep 15, 2017 3:00:25 AM
 * @since 0.0.1
 */
public abstract class BasicActor implements CoActor {

    protected CoIO io;

    protected volatile boolean open;

    @Override
    public CoActor init(CoIO io) {
        this.io = io;
        open = true;
        return this;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#isOpen()
     */
    @Override
    public boolean isOpen() {
        return open;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#close()
     */
    @Override
    public void close() {
        open = false;
    }

}
