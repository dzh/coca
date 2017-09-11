/**
 * 
 */
package coca.co.io.packet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Optional;

import coca.co.BasicCo;
import coca.co.ins.ByteBufferCoIns;
import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;
import coca.co.util.MD5Hash;

/**
 * @author dzh
 * @date Sep 10, 2017 8:31:42 PM
 * @since 0.0.1
 */
public class PacketCodec_v1 implements PacketCodec {

    private short _v = 1;

    /*
     * (non-Javadoc)
     * @see coca.co.io.packet.PacketDecoder#version()
     */
    @Override
    public short version() {
        return _v;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.io.packet.PacketDecoder#decode(java.nio.ByteBuffer)
     */
    @Override
    public InsPacket decode(ByteBuffer packet) {
        return new PacketDecoderImpl().decode(packet);
    }

    /*
     * (non-Javadoc)
     * @see coca.co.io.packet.PacketEncoder#encode(coca.co.io.packet.InsPacket)
     */
    @Override
    public ByteBuffer encode(InsPacket ins) {
        return new PacketEncoderImpl().encode(ins);
    }

    class PacketEncoderImpl implements PacketEncoder {
        private ByteBufferCoIns _ins;
        private int _m;

        public byte[] packet() {
            byte[] coIns = coIns();
            ByteBuffer buf = ByteBuffer.allocate(4 + 2 + 4 + coIns.length + 4);
            buf.putInt(_m);
            short v = version();
            if ((v & 0xffff) > 0) throw new InsPacketException("PacketEncoder invalid version:" + v);
            buf.putShort(v);
            buf.putInt(coIns.length);
            buf.put(coIns);
            buf.putInt(MD5Hash.md5hash(buf.array()));
            return buf.array();
        }

        public byte[] coIns() {
            Charset charset = charset();
            byte[] from = _ins.from().id().getBytes(charset);
            byte[] codec = _ins.codec().getBytes(charset);
            byte[] data = _ins.data().array();
            if (data.length > CoIns.MAX_DATA_BYTE) // ≤ 1M
                throw new InsPacketException(_ins.ins().toString() + " data's size overflow:" + data.length);
            byte[] ins = ins();
            ByteBuffer buf = ByteBuffer.allocate(2 + ins.length + 1 + from.length + 1 + codec.length + 4 + data.length);
            buf.put((byte) ins.length);
            buf.put(ins);
            buf.put((byte) from.length);
            buf.put(from);
            buf.put((byte) codec.length);
            buf.put(codec);
            buf.putInt(codec.length);
            buf.put(data);
            return buf.array();
        }

        public byte[] ins() {
            int code = _ins.ins().code();
            byte[] name = Optional.ofNullable(_ins.ins().name()).orElse("").getBytes(charset());
            if (name.length > Ins.MAX_NAME_BYTE)
                throw new InsPacketException(_ins.ins().toString() + " name's size overflow:" + name.length);
            byte[] format = Optional.ofNullable(_ins.format()).orElse("").getBytes(charset());
            if (format.length > Ins.MAX_FORMAT_BYTE)
                throw new InsPacketException(_ins.ins().toString() + " format's size overflow:" + format.length);
            ByteBuffer buf = ByteBuffer.allocate(4 + 1 + name.length + 1 + format.length);
            buf.putInt(code);
            buf.put((byte) name.length);
            buf.put(name);
            buf.put((byte) format.length);
            buf.put(format);
            return buf.array();
        }

        @Override
        public short version() {
            return _v;
        }

        @Override
        public ByteBuffer encode(InsPacket ins) {
            this._ins = ins.ins();
            this._m = ins.magic();
            return ByteBuffer.wrap(packet());
        }
    }

    class PacketDecoderImpl implements PacketDecoder {

        private ByteBuffer _packet;

        public InsPacket packet() {
            InsPacket _ins = new BasicInsPacket();
            _ins.packet(_packet);

            Charset charset = charset();

            // magic
            int magic = _packet.getInt();
            _ins.magic(magic);
            // version
            short v = _packet.getShort();
            if ((v & 0xffff) > 0) throw new InsPacketException("PacketEncoder invalid version:" + v);
            _ins.version(v);
            // CoIns
            int coInsSize = _packet.getInt();
            ByteBuffer coInsBuf = ByteBuffer.wrap(new byte[coInsSize]);
            _packet.get(coInsBuf.array());
            _packet.mark();
            // hash
            int hash = _packet.getInt();
            // check hash
            _packet.reset();
            _packet.putInt(0);
            if (MD5Hash.md5hash(_packet.array()) != hash) throw new InsPacketException("PacketDecoder invalid hash:" + hash);
            _ins.hash(hash);
            // Ins
            int insSize = coInsBuf.getChar();
            ByteBuffer insBuf = ByteBuffer.wrap(new byte[insSize]);
            coInsBuf.get(insBuf.array());
            ByteBufferCoIns coIns = new ByteBufferCoIns(ins(insBuf));
            // from
            int fromSize = coInsBuf.get();
            byte[] from = new byte[fromSize];
            coInsBuf.get(from);
            coIns.from(new BasicCo(new String(from, charset)));
            // codec
            int codecSize = coInsBuf.get();
            byte[] codec = new byte[codecSize];
            coInsBuf.get(codec);
            coIns.codec(new String(codec, charset));
            // data
            int dataSize = coInsBuf.getInt();
            byte[] data = new byte[dataSize];
            coInsBuf.get(data);
            coIns.data(ByteBuffer.wrap(data));

            _ins.ins(coIns);
            return _ins;
        }

        public Ins ins(ByteBuffer bytes) {
            int code = bytes.getInt();
            int nameSize = bytes.get();
            byte[] name = new byte[nameSize];
            int fmtSize = bytes.get();
            byte[] format = new byte[fmtSize];
            return new Ins(code, new String(name, charset()), new String(format, charset()));
        }

        @Override
        public short version() {
            return _v;
        }

        @Override
        public InsPacket decode(ByteBuffer packet) {
            this._packet = packet;
            return packet();
        }
    }

}
