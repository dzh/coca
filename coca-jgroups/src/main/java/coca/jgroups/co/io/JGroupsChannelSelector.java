/**
 * 
 */
package coca.jgroups.co.io;

import coca.co.io.GroupChannelSelector;
import coca.co.io.channel.GroupChannel;

/**
 * @author dzh
 * @date Dec 5, 2017 4:28:57 PM
 * @since 1.0.0
 */
public class JGroupsChannelSelector extends GroupChannelSelector {

    @Override
    protected GroupChannel newGroupChannel(String name) {
        return new JGroupsGroupChannel(name);
    }

}
