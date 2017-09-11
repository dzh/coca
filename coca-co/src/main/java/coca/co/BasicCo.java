/**
 * 
 */
package coca.co;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.ins.CoIns;
import coca.co.ins.CoInsFactory;
import coca.co.ins.codec.InsCodec;
import coca.co.ins.codec.TextInsCodec;
import coca.co.io.BasicCoIO;
import coca.co.io.CoIO;
import coca.co.io.PacketResult;

/**
 * 
 * ThreadSafe
 * 
 * @author dzh
 * @date Aug 25, 2017 7:16:42 PM
 * @since 0.0.1
 */
public class BasicCo implements Co {

    static final Logger LOG = LoggerFactory.getLogger(BasicCo.class);

    protected Map<String, CoListener> listeners;

    private String id;

    private Map<String, CoGroup> groups;

    private Map<String, InsCodec> codecs;

    private final Object lock = new Object();

    private CoIO io;

    // TODO to config
    private CoInsFactory insFactory = new CoInsFactory();

    /**
     * 
     * @param id
     * @throws NullPointerException
     */
    public BasicCo(String id) {
        this.id = id;
        groups = initGroup();
        listeners = initListener();
        codecs = initInsCodec();
        withCodec(new TextInsCodec());

        if (groups == null) throw new NullPointerException("groups is nil");
        if (listeners == null) throw new NullPointerException("listeners is nil");
        if (codecs == null) throw new NullPointerException("codecs is nil");
    }

    protected Map<String, InsCodec> initInsCodec() {
        return new HashMap<String, InsCodec>();
    }

    protected Map<String, CoGroup> initGroup() {
        return new ConcurrentHashMap<String, CoGroup>();
    }

    public BasicCo() {
        this(genId());
    }

    protected Map<String, CoListener> initListener() {
        return new HashMap<String, CoListener>();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Collection<CoGroup> groups() {
        return groups.values();
    }

    @Override
    public CoGroup group(String name) {
        return groups.get(name);
    }

    private CoGroup group(String name, CoGroup def) {
        CoGroup g = group(name);
        return g == null ? def : g;
    }

    public static final String genId() { // TODO
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    /**
     * @param name
     *            group name
     */
    @Override
    public CoFuture<CoGroup> join(String name) throws CoException {
        CoIns<String> ins = insFactory.newJoin(name, id()).from(this).to(group(name, new BasicCoGroup(name)));
        pub(ins);// TODO
        return null;
    }

    /**
     * @param name
     *            group name
     */
    @Override
    public CoFuture<CoGroup> quit(String name) throws CoException {
        CoIns<String> ins = insFactory.newQuit(name, id()).from(this).to(group(name, new BasicCoGroup(name)));
        pub(ins);// TODO
        return null;
    }

    @Override
    public CoFuture<PacketResult> pub(CoIns<?> ins) throws CoException {
        return io.pub(ins);
    }

    @Override
    public CoIns<?> sub(long timeout, TimeUnit unit) throws CoException {

        return null;
    }

    @Override
    public Co withListener(String name, CoListener l) {
        if (name == null || l == null) return this;
        synchronized (lock) {
            listeners.put(name, l);
        }
        return this;
    }

    @Override
    public CoListener removeListener(String name) {
        if (name == null) return null;
        synchronized (lock) {
            return listeners.remove(name);
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            listeners.clear();
            codecs.clear();
        }
        groups.clear();
        insFactory.close();
    }

    @Override
    public Co withCodec(InsCodec codec) {
        synchronized (lock) {
            codecs.put(codec.name(), codec);
        }
        return this;
    }

    private static final TextInsCodec TEXT_CODEC = new TextInsCodec();

    @Override
    public InsCodec codec(String name) {
        if (name == null) return TEXT_CODEC;
        synchronized (lock) {
            return codecs.getOrDefault(name, TEXT_CODEC);
        }
    }

    @Override
    public Co insFactory(CoInsFactory insFactory) {
        this.insFactory = insFactory;
        return this;
    }

    @Override
    public CoInsFactory insFactory() {
        return insFactory;
    }

    @Override
    public Co io(CoIO io) {
        this.io = io;
        return this;
    }

    @Override
    public int hashCode() {
        return id().hashCode();
    }

    @Override
    public CoIO io() {
        return io;
    }

    @Override
    public Co init() {
        io = new BasicCoIO(this);
        return this;
    }

}
