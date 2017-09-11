/**
 * 
 */
package coca.co.io;

import java.util.concurrent.TimeUnit;

import coca.co.Co;
import coca.co.CoFuture;
import coca.co.ins.CoIns;
import coca.co.io.packet.InsPacket;

/**
 * 
 * @author dzh
 * @date Sep 2, 2017 9:19:08 PM
 * @since 0.0.1
 */
public interface CoIO {

    Co co();

    ChannelSelector selector();

    CoIO selector(ChannelSelector selector);

    CoFuture<PacketResult> pub(CoIns<?> ins);

    CoIns<?> sub(long timeout, TimeUnit unit);

    InsPacket packet(CoIns<?> ins);

}
