/**
 * 
 */
package coca.co.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.CoException;
import coca.co.CoFuture;
import coca.co.ins.ByteBufferCoIns;
import coca.co.ins.CoIns;
import coca.co.ins.InsFuture;
import coca.co.ins.InsResult;
import coca.co.ins.VoidCoIns;
import coca.co.ins.actor.CoActor;
import coca.co.ins.codec.InsCodec;
import coca.co.ins.codec.TextInsCodec;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.CoChannelException;
import coca.co.io.packet.BasicInsPacket;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 10, 2017 12:32:06 PM
 * @since 0.0.1
 */
public class BasicIO implements CoIO {

    static final Logger LOG = LoggerFactory.getLogger(BasicIO.class);

    private Co co;

    private ChannelSelector selector;

    private List<CoActor> actors = Collections.synchronizedList(new LinkedList<CoActor>());

    private Map<String, InsCodec> codecs = Collections.synchronizedMap(new HashMap<String, InsCodec>());;

    private static final TextInsCodec TEXT_CODEC = new TextInsCodec();

    @Override
    public CoIO init(Co co) {
        if (co == null) throw new NullPointerException("BasicIO.co is nil");
        this.co = co;

        if (selector == null) throw new NullPointerException("BasicIO.selector is nil");
        selector.init(this);

        for (CoActor actor : actors)
            actor.init(this);

        LOG.info("{} init", getClass().getName());
        return this;
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
    public CoFuture<InsResult> pub(CoIns<?> ins) throws CoException {
        if (ins == null) throw new CoException("ins is nil");
        LOG.info("pub {}", ins);

        if (selector == null) throw new CoException("pub but selector is nil");
        CoChannel ch = selector.select(ins);
        if (ch == null) throw new CoException("None channel selected " + ins);
        if (ch.isOpen()) {
            // TODO ins_ack
            try {
                return (InsFuture) ch.write(packet(ins)).next();
            } catch (CoChannelException e) {
                LOG.error(e.getMessage(), e);
                throw new CoException(e);
            }
        }
        throw new CoException("pub error!" + ch.name() + " closed " + ins);
    }

    @Override
    public CoIns<?> sub(long timeout, TimeUnit unit) {
        if (selector == null) {
            LOG.error("sub but selector is nil");
            return VoidCoIns.VOID;
        }
        InsPacket packet = selector.poll(timeout, unit);
        if (packet == null) return VoidCoIns.VOID;
        return decodeCoIns(packet.ins());
    }

    @Override
    public CoIO selector(ChannelSelector selector) {
        this.selector = selector;
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
        CoIns<Object> copy = (CoIns<Object>) co.insFactory().newIns(ins.ins(), false);
        copy.id(ins.id());
        copy.cntl(ins.cntl());
        copy.from(ins.from());
        copy.codec(ins.codec());
        if (ins.data() != null) copy.data(codec(ins.codec()).decode(ins.data().array()));
        LOG.info("sub {}", copy);
        return copy;
    }

    // TODO clone
    private ByteBufferCoIns encodeCoIns(CoIns<?> ins) {
        Objects.requireNonNull(ins);
        ByteBufferCoIns copy = new ByteBufferCoIns(ins);

        if (ins.data() != null) {
            InsCodec codec = codec(ins.codec());
            copy.data(ByteBuffer.wrap(codec.encode(ins.data())));
        }
        return copy;
    }

    @Override
    public void close() throws IOException {
        if (selector != null) selector.close();
        if (actors != null) {
            for (CoActor a : actors) {
                a.close();
            }
            actors.clear();
        }
    }

    @Override
    public List<CoActor> actors() {
        return actors;
    }

    @Override
    public CoIO withActor(CoActor actor) {
        if (!actors.add(actor)) {
            LOG.error("withActor {} failed!", actor);
        }
        return this;
    }

    @Override
    public CoActor removeActor(String name) {
        Optional<CoActor> op = actors.stream().filter(actor -> {
            return actor.name().equals(name);
        }).findFirst();

        if (op.isPresent()) {
            actors.remove(op.get());
            return op.get();
        }
        return null;
    }

    @Override
    public CoIO withCodec(InsCodec codec) {
        codecs.put(codec.name(), codec);
        return this;
    }

    @Override
    public InsCodec codec(String name) {
        if (name == null) return TEXT_CODEC;
        return codecs.getOrDefault(name, TEXT_CODEC);
    }

}
