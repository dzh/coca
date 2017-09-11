/**
 * 
 */
package coca.co.io.packet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import coca.co.CoConst;

/**
 * 
 * @author dzh
 * @date Sep 8, 2017 5:45:31 PM
 * @since 0.0.1
 */
public interface PacketEncoder {

    short version();

    ByteBuffer encode(InsPacket ins);

    /**
     * @return Magic[4] + Version[2] + CoInsSize[4] + CoIns[] + Hash[4]
     */
    // byte[] packet();

    /**
     * @return FromSize[1] + From[] + InsSize[2] + Ins[] + CodecSize[1] + Codec[] + DataSize[4] + Data[]
     */
    // byte[] coIns();

    /**
     * @return code[4] + nameSize[1] + name[] + formatSize[1] + format[]
     */
    // byte[] ins();

    /**
     * 
     * @return
     */
    default Charset charset() {
        return CoConst.UTF8;
    }

}
