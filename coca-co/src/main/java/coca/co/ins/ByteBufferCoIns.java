/**
 * 
 */
package coca.co.ins;

import java.nio.ByteBuffer;

/**
 * @author dzh
 * @date Sep 6, 2017 5:40:26 PM
 * @since 0.0.1
 */
public class ByteBufferCoIns extends FmtCoIns<ByteBuffer> {

    public ByteBufferCoIns(Ins ins) {
        super(ins);
    }

    public ByteBufferCoIns(CoIns<?> ins) {
        super(ins);
    }

}
