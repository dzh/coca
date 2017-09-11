/**
 * 
 */
package coca.co.io.packet;

import java.nio.ByteBuffer;

import coca.co.ins.ByteBufferCoIns;

/**
 * 
 * @author dzh
 * @date Sep 9, 2017 7:52:56 PM
 * @since 0.0.1
 */
public class BasicInsPacket implements InsPacket {

    private ByteBufferCoIns ins;
    private ByteBuffer packet;

    private short version = 1;
    private int magic;
    private int hash;

    @Override
    public int magic() {
        return magic;
    }

    @Override
    public short version() {
        return version;
    }

    @Override
    public ByteBufferCoIns ins() {
        return ins;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.io.packet.InsPacket#packet()
     */
    @Override
    public ByteBuffer packet() {
        return packet;
    }

    @Override
    public InsPacket magic(int magic) {
        this.magic = magic;
        return this;
    }

    @Override
    public InsPacket version(short v) {
        this.version = v;
        return this;
    }

    @Override
    public InsPacket ins(ByteBufferCoIns ins) {
        this.ins = ins;
        return this;
    }

    @Override
    public int hash() {
        return hash;
    }

    @Override
    public InsPacket hash(int hash) {
        this.hash = hash;
        return this;
    }

    @Override
    public InsPacket packet(ByteBuffer packet) {
        this.packet = packet;
        return this;
    }

}
