/**
 * 
 */
package coca.co.io.packet;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Sep 9, 2017 8:25:59 PM
 * @since 0.0.1
 */
public class TestInsPacket {

    static final Logger LOG = LoggerFactory.getLogger(TestInsPacket.class);

    @Test
    public void testMagic() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(0x636F6361);
        buf.flip();
        System.out.println(buf.get());
        System.out.println(buf.get());
        System.out.println(buf.get());
        System.out.println(buf.get());
    }
    
    @Test
    public void testVersion() {
        int v = 1;
        
    }

}
