/**
 * 
 */
package coca.co.io;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import coca.co.Co;
import coca.co.CoException;
import coca.co.CoFuture;
import coca.co.ins.CoIns;
import coca.co.ins.InsResult;
import coca.co.ins.actor.CoActor;
import coca.co.io.packet.InsPacket;

/**
 * 
 * @author dzh
 * @date Sep 2, 2017 9:19:08 PM
 * @since 0.0.1
 */
public interface CoIO extends Closeable {

    Co co();

    CoIO init(Co co);

    ChannelSelector selector();

    CoIO selector(ChannelSelector selector);

    /**
     * 
     * @param ins
     * @return
     * @throws CoException
     */
    CoFuture<InsResult> pub(CoIns<?> ins) throws CoException;

    CoIns<?> sub(long timeout, TimeUnit unit) throws CoException;

    InsPacket packet(CoIns<?> ins);

    List<CoActor> actors();

    CoIO withActor(CoActor actor);

    CoActor removeActor(String name);

}
