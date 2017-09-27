/**
 * 
 */
package coca.rmq.co.io;

import coca.co.io.GroupChannelSelector;
import coca.co.io.channel.GroupChannel;

/**
 * @author dzh
 * @date Sep 14, 2017 1:45:38 AM
 * @since 0.0.1
 */
public class RMQChannelSelector extends GroupChannelSelector {


    public RMQChannelSelector() {
        super();
    }

    @Override
    protected GroupChannel newGroupChannel(String name) {
        return new RMQGroupChannel(name);
    }

}
