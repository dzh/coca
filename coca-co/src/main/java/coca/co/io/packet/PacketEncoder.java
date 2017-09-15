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
     * 
     * @return
     */
    default Charset charset() {
        return CoConst.UTF8;
    }

}
