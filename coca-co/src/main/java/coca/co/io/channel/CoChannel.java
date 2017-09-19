/**
 * 
 */
package coca.co.io.channel;

import java.nio.channels.Channel;
import java.util.concurrent.TimeUnit;

import coca.co.io.ChannelSelector;
import coca.co.io.packet.InsPacket;
import coca.co.io.packet.InsPacketException;

/**
 * @author dzh
 * @date Sep 8, 2017 1:43:03 PM
 * @since 0.0.1
 */
public interface CoChannel extends Channel {

    String name();

    CoChannel init(ChannelSelector selector) throws CoChannelException;

    /**
     * 
     * @param packet
     * @return
     * @throws InsPacketException
     *             if InsPacket is null or invalid
     * @throws CoChannelException
     *             if channel has closed
     */
    PacketFuture write(InsPacket packet) throws CoChannelException;

    /**
     * 
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    InsPacket read(long timeout, TimeUnit unit) throws InterruptedException;

}
