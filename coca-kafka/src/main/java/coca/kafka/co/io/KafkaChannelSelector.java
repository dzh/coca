/**
 * 
 */
package coca.kafka.co.io;

import coca.co.io.GroupChannelSelector;
import coca.co.io.channel.GroupChannel;

/**
 * @author dzh
 * @date Nov 8, 2017 4:16:11 PM
 * @since 1.0.0
 */
public class KafkaChannelSelector extends GroupChannelSelector {

    @Override
    protected GroupChannel newGroupChannel(String name) {
        return new KafkaGroupChannel(name);
    }

}
