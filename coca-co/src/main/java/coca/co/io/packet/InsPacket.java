/**
 * 
 */
package coca.co.io.packet;

import java.nio.ByteBuffer;

import coca.co.ins.ByteBufferCoIns;

/**
 * <pre>
 * Packet Binary Format([byte size]):
 * Packet -> Magic[4] + Version[2] + CNTL[8] + Type[4] + IDSize[1] + ID[] + CoInsSize[4] + CoIns[] + Hash[4]
 *                                                                                           |
 * CoIns  -> InsSize[2] + Ins[] + IDSize[1] + ID[] + CNTL[8] + TTL[8] + FromSize[1] + From[] + CodecSize[1] + Codec[] + DataSize[4] + Data[]
 *                         |
 * Ins    -> code[4] + nameSize[1] + name[] + formatSize[1] + format[]
 * </pre>
 * 
 * <pre>
 * Encode Example:
 * {@code 
 * }
 * </pre>
 * 
 * <pre>
 * Decode Example:
 * {@code
 * }
 * </pre>
 * 
 * @author dzh
 * @date Sep 6, 2017 7:31:14 PM
 * @since 0.0.1
 */
public interface InsPacket {

    /**
     * Magic Number: coca
     */
    int M = 0x636F6361;

    /**
     * 
     */
    int CNTL_ACK = 1;

    int magic();

    InsPacket magic(int magic);

    short version();

    InsPacket version(short v);

    long cntl();

    InsPacket cntl(long cntl);

    int type();

    InsPacket type(int type);

    String id();

    InsPacket id(String id);

    ByteBufferCoIns ins();

    InsPacket ins(ByteBufferCoIns ins);

    int hash();

    InsPacket hash(int hash);

    ByteBuffer packet();

    InsPacket packet(ByteBuffer packet);

    public enum Type {
        ACK, GROUP
    }

}
