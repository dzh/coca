/**
 * 
 */
package coca.redis.co.io;

import coca.co.io.GroupChannelSelector;
import coca.co.io.channel.GroupChannel;

/**
 * @author dzh
 * @date Sep 15, 2017 7:35:51 PM
 * @since 0.0.1
 */
public class RedisChannelSelector extends GroupChannelSelector {

    @Override
    protected GroupChannel newGroupChannel(String name) {
        return new RedisGroupChannel(name);
    }

}
