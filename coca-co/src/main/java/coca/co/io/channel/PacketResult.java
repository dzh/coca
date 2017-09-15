/**
 * 
 */
package coca.co.io.channel;

import coca.co.io.packet.InsPacket;

/**
 * Ins Send Status
 * 
 * @author dzh
 * @date Sep 8, 2017 4:49:29 PM
 * @since 0.0.1
 */
public class PacketResult {

    public PacketResult(IOSt st, InsPacket recv) {
        this.st = st;
        this.recv = recv;
    }

    public PacketResult(IOSt st) {
        this.st = st;
    }

    public PacketResult() {
        this(IOSt.SEND);
    }

    public InsPacket recv() {
        return this.recv;
    }

    public IOSt st() {
        return this.st;
    }

    InsPacket recv;

    /**
     * io status
     */
    IOSt st;

    public static enum IOSt {
        SEND, SEND_SUCC, SEND_FAIL, RECV_SUCC, RECV_FAIL, RECV_TIMEOUT
    }

}
