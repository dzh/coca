/**
 * 
 */
package coca.co.ins.codec;

/**
 * InsCodec for encoding and decoding Coins.data
 * 
 * ThreadSafe
 * 
 * @author dzh
 * @date Nov 14, 2016 1:31:31 PM
 * @since 0.0.1
 */
public interface InsCodec {

    /**
     * The unique name of implemented InsCodec.
     * 
     * @return
     */
    String name();

    byte[] encode(Object object);

    Object decode(byte[] ins);

}
