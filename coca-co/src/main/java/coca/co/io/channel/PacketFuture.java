/**
 * 
 */
package coca.co.io.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.BasicFuture;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 8, 2017 4:37:02 PM
 * @since 0.0.1
 */
public class PacketFuture extends BasicFuture<PacketResult> {

    static final Logger LOG = LoggerFactory.getLogger(PacketFuture.class);

    private final InsPacket send;

    public boolean hasAck() {
        return (send.cntl() & InsPacket.CNTL_ACK) == InsPacket.CNTL_ACK;
    }

    private PacketResult result;

    public PacketFuture(InsPacket send) {
        this.send = send;
    }

    public InsPacket send() {
        return send;
    }

    @Override
    public boolean isDone() {
        if (!super.isDone()) return false;
        boolean done = false;
        if (result != null) {
            if (hasAck()) {
                done = result.st == PacketResult.IOSt.RECV_SUCC || result.st == PacketResult.IOSt.RECV_FAIL
                        || result.st == PacketResult.IOSt.RECV_TIMEOUT;
            } else {
                done = result.st == PacketResult.IOSt.SEND_SUCC || result.st == PacketResult.IOSt.SEND_FAIL;
            }
        }
        return done;
    }

    @Override
    public PacketResult change(Object obj) {
        // TODO Auto-generated method stub
        return null;
    }

}
