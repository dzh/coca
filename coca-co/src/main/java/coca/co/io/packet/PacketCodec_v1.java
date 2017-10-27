/**
 * 
 */
package coca.co.io.packet;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import coca.co.BasicCo;
import coca.co.CoConst;
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

    @Override
    public Charset charset() {
        return CoConst.UTF8;
    }

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
        private InsPacket _packet;
        private ByteBufferCoIns _ins;

        public byte[] packet() {
            byte[] coIns = coIns();
            byte[] id = id();
            ByteBuffer buf = ByteBuffer.allocate(4 + 2 + 8 + 4 + 1 + id.length + 4 + coIns.length + 4);
            buf.putInt(_packet.magic());
            short v = version();
            if ((v & 0xffff) < 1) throw new InsPacketException("PacketEncoder invalid version:" + v);
            buf.putShort(v);
            buf.putLong(_packet.cntl());
            buf.putInt(_packet.type());
            buf.put((byte) id.length);
            buf.put(id);
            buf.putInt(coIns.length);
            buf.put(coIns);
            buf.putInt(MD5Hash.md5hash(buf.array()));
            return buf.array();
        }

        public byte[] id() {
            Charset charset = charset();
            return _packet.id().getBytes(charset);
        }

        public byte[] coIns() {
            // debug
            // if (_ins.ins().equals(new Ins(1025, "", ""))) {
            // System.out.println("debug");
            // }
            Charset charset = charset();
            byte[] ins = ins();
            byte[] from = _ins.from().id().getBytes(charset);
            byte[] codec = _ins.codec() == null ? new byte[0] : _ins.codec().getBytes(charset);
            byte[] data = _ins.data() == null ? new byte[0] : _ins.data().array();
            byte[] id = _ins.id().getBytes(charset);
            if (data.length > CoIns.MAX_DATA_BYTE) // ≤ 1M
                throw new InsPacketException(_ins.ins().toString() + " data's size overflow:" + data.length);
            ByteBuffer buf =
                    ByteBuffer.allocate(2 + ins.length + 1 + id.length + 8 + 8 + 1 + from.length + 1 + codec.length + 4 + data.length);
            buf.putShort((short) ins.length);
            buf.put(ins);
            buf.put((byte) id.length);
            buf.put(id);
            buf.putLong(_ins.cntl());
            buf.putLong(_ins.ttl());
            buf.put((byte) from.length);
            buf.put(from);
            buf.put((byte) codec.length);
            buf.put(codec);
            buf.putInt(data.length);
            buf.put(data);
            return buf.array();
        }

        public byte[] ins() {
            int code = _ins.ins().code();
            byte[] name = (_ins.ins().name() == null ? "" : _ins.ins().name()).getBytes(charset());
            if (name.length > Ins.MAX_NAME_BYTE)
                throw new InsPacketException(_ins.ins().toString() + " name's size overflow:" + name.length);
            byte[] format = (_ins.format() == null ? "" : _ins.format()).getBytes(charset());
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
        public ByteBuffer encode(InsPacket packet) {
            this._packet = packet;
            this._ins = packet.ins();
            return ByteBuffer.wrap(packet());
        }

        @Override
        public Charset charset() {
            return CoConst.UTF8;
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
            if ((v & 0xffff) < 1) throw new InsPacketException("PacketEncoder invalid version:" + v);
            _ins.version(v);
            // cntl
            long cntl = _packet.getLong();
            _ins.cntl(cntl);
            // type
            int type = _packet.getInt();
            _ins.type(type);
            // id
            int idSize = _packet.get();
            byte[] id = new byte[idSize];
            _packet.get(id);
            _ins.id(new String(id, charset));
            // CoIns data
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
            // coIns
            _ins.ins(coIns(coInsBuf));
            return _ins;
        }

        public ByteBufferCoIns coIns(ByteBuffer bytes) {
            Charset charset = charset();
            // Ins
            int insSize = bytes.getShort();
            ByteBuffer insBuf = ByteBuffer.wrap(new byte[insSize]);
            bytes.get(insBuf.array());
            ByteBufferCoIns coIns = new ByteBufferCoIns(ins(insBuf));
            // debug
            // if (coIns.ins().equals(new Ins(1025, "", ""))) {
            // System.out.println("debug");
            // }
            // id
            int idSize = bytes.get();
            ByteBuffer idBuf = ByteBuffer.wrap(new byte[idSize]);
            bytes.get(idBuf.array());
            coIns.id(new String(idBuf.array(), charset));
            // cntl
            coIns.cntl(bytes.getLong());
            // ttl
            coIns.ttl(bytes.getLong());
            // from
            int fromSize = bytes.get();
            byte[] from = new byte[fromSize];
            bytes.get(from);
            coIns.from(new BasicCo(new String(from, charset)));
            // codec
            int codecSize = bytes.get();
            if (codecSize > 0) {
                byte[] codec = new byte[codecSize];
                bytes.get(codec);
                coIns.codec(new String(codec, charset));
            }
            // data
            int dataSize = bytes.getInt();
            if (dataSize > 0) {
                byte[] data = new byte[dataSize];
                bytes.get(data);
                coIns.data(ByteBuffer.wrap(data));
            }
            return coIns;
        }

        public Ins ins(ByteBuffer bytes) {
            int code = bytes.getInt();
            int nameSize = bytes.get();
            byte[] name = new byte[nameSize];
            bytes.get(name);
            int fmtSize = bytes.get();
            byte[] format = new byte[fmtSize];
            bytes.get(format);
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

        @Override
        public Charset charset() {
            return CoConst.UTF8;
        }
    }

}
