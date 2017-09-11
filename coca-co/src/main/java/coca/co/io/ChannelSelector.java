/**
 * 
 */
package coca.co.io;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import coca.co.ins.CoIns;
import coca.co.io.channel.CoChannel;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 8, 2017 1:43:59 PM
 * @since 0.0.1
 */
public interface ChannelSelector extends Closeable {

    ChannelSelector init(CoIO io);

    CoIO io();

    CoChannel select(CoIns<?> ins);

    InsPacket poll(long timeout, TimeUnit unit);

    CoChannel newChannel(CoIns<?> ins);

}
