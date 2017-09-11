/**
 * 
 */
package coca.co.ins.codec;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author dzh
 * @date Sep 6, 2017 5:51:03 PM
 * @since 0.0.1
 */
public class TextInsCodec implements InsCodec {

    private Charset charset = StandardCharsets.UTF_8;

    public static final String NAME = "co.TextInsCodec";

    @Override
    public byte[] encode(Object data) {
        if (data == null) return new byte[0];
        return data.toString().getBytes(charset);
    }

    @Override
    public String decode(byte[] data) {
        return new String(data, charset);
    }

    @Override
    public String name() {
        return NAME;
    }

    public TextInsCodec charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public Charset charset() {
        return this.charset;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof InsCodec) { return ((TextInsCodec) obj).name() == name(); }
        return false;
    }

}
