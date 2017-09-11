/**
 * 
 */
package coca.co.io.packet;

import java.nio.charset.Charset;

import coca.co.CoConst;

/**
 * @author dzh
 * @date Sep 10, 2017 8:30:50 PM
 * @since 0.0.1
 */
public interface PacketCodec extends PacketDecoder, PacketEncoder {

    @Override
    default Charset charset() {
        return CoConst.UTF8;
    }

}
