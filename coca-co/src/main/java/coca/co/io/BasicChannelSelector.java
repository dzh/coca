/**
 * 
 */
package coca.co.io;

/**
 * @author dzh
 * @date Sep 10, 2017 3:34:58 PM
 * @since 0.0.1
 */
public abstract class BasicChannelSelector implements ChannelSelector {

    protected CoIO io;

    @Override
    public ChannelSelector init(CoIO io) {
        this.io = io;
        return this;
    }

    @Override
    public CoIO io() {
        return io;
    }

}
