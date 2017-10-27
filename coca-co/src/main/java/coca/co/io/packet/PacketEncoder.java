/**
 * 
 */
package coca.co.io.packet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 
 * @author dzh
 * @date Sep 8, 2017 5:45:31 PM
 * @since 0.0.1
 */
public interface PacketEncoder {

    short version();

    ByteBuffer encode(InsPacket ins);

    Charset charset();

}
