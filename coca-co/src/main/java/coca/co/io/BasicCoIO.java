/**
 * 
 */
package coca.co.io;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.CoFuture;
import coca.co.ins.ByteBufferCoIns;
import coca.co.ins.CoIns;
import coca.co.ins.FmtCoIns;
import coca.co.io.channel.CoChannel;
import coca.co.io.packet.BasicInsPacket;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 10, 2017 12:32:06 PM
 * @since 0.0.1
 */
public class BasicCoIO implements CoIO {

    static final Logger LOG = LoggerFactory.getLogger(BasicCoIO.class);

    private Co co;

    private ChannelSelector selector;

    /**
     * 
     * @param co
     * @throws NullPointerException
     */
    public BasicCoIO(Co co) {
        this.co = co;
        if (co == null) throw new NullPointerException("BasicCoIO.co is nil");
    }

    @Override
    public Co co() {
        return this.co;
    }

    @Override
    public ChannelSelector selector() {
        return this.selector;
    }

    @Override
    public CoFuture<PacketResult> pub(CoIns<?> ins) {
        if (ins == null) return null;
        if (selector == null) {
            LOG.error("pub but selector is nil");
            return null;
        }
        CoChannel ch = selector.select(ins);
        return ch.write(packet(ins));
    }

    @Override
    public CoIns<?> sub(long timeout, TimeUnit unit) {
        if (selector == null) {
            LOG.error("sub but selector is nil");
            return null;
        }
        InsPacket packet = selector.poll(timeout, unit);
        if (packet == null) return null;
        return decodeCoIns(packet.ins());
    }

    @Override
    public CoIO selector(ChannelSelector selector) {
        this.selector = selector;
        selector.init(this);// TODO
        return this;
    }

    @Override
    public InsPacket packet(CoIns<?> ins) {
        Objects.requireNonNull(ins);
        InsPacket packet = new BasicInsPacket();
        packet.version((short) 1);// TODO
        packet.magic(InsPacket.M);
        packet.ins(encodeCoIns(ins));
        return packet;
    }

    private CoIns<?> decodeCoIns(ByteBufferCoIns ins) {
        Objects.requireNonNull(ins);
        @SuppressWarnings("unchecked")
        CoIns<Object> copy = (CoIns<Object>) co.insFactory().newIns(ins.ins());
        copy.from(ins.from());
        copy.codec(ins.codec());
        copy.data(co.codec(ins.codec()).decode(ins.data().array()));
        return copy;
    }

    // TODO clone
    private ByteBufferCoIns encodeCoIns(CoIns<?> ins) {
        Objects.requireNonNull(ins);
        ByteBufferCoIns copy = new ByteBufferCoIns(ins.ins());
        copy.from(ins.from());
        copy.codec(ins.codec());
        if (ins instanceof FmtCoIns) copy.format(((FmtCoIns<?>) ins).format());
        copy.to(ins.toGroup());
        copy.to(ins.toCo().toArray(new Co[ins.toCo().size()]));
        copy.data(ByteBuffer.wrap(co.codec(ins.codec()).encode(ins.data())));
        return copy;
    }

}
