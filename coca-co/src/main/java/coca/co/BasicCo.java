/**
 * 
 */
package coca.co;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.init.BasicCoInit;
import coca.co.init.CoInit;
import coca.co.init.CoInitException;
import coca.co.ins.CoIns;
import coca.co.ins.CoInsFactory;
import coca.co.ins.InsResult;
import coca.co.ins.actor.JoinActor;
import coca.co.ins.actor.QuitActor;
import coca.co.ins.codec.InsCodec;
import coca.co.ins.codec.TextInsCodec;
import coca.co.io.CoIO;
import coca.co.util.IDUtil;

/**
 * 
 * <pre>
 * For Example:
 * {@code
 *      Co co = new BasicCo().init(); // create Co
 *      co.io(new BasicCoIO()); // add CoIO
 *      co.insFactory(new CoInsFactory()); // add ins factory
 *      CoIns ins = co.insFactory.newIns(InsConst.JOIN);  // create CoIns
 *      ins.from(co).toGroup(group("name")).data("name id"); // fill ins's data
 *      co.pub(ins); // publish ins
 * }
 * </pre>
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

    private volatile boolean closed = false;

    private CoIO io;

    private static final TextInsCodec TEXT_CODEC = new TextInsCodec();

    private CoInsFactory insFactory;

    private CoConf conf;

    /**
     * @param id
     * @throws NullPointerException
     */
    public BasicCo(String id) {
        this.id = id;
    }

    public static final Co create(Map<String, String> conf) {
        CoInit init = null;
        if (conf.containsKey(CoInit.P_CO_INIT)) {
            try {
                init = (CoInit) Thread.currentThread().getContextClassLoader().loadClass(conf.get(CoInit.P_CO_INIT)).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new CoInitException(e);
            }
        } else {
            init = new BasicCoInit();
        }
        return init.init(conf);
    }

    @Override
    public CoConf conf() {
        return conf;
    }

    /**
     * @throws NullPointerException
     */
    @Override
    public Co init(CoConf conf) {
        if (conf == null) throw new NullPointerException("conf is nil");
        this.conf = conf;

        groups = initGroup();
        if (groups == null) throw new NullPointerException("groups is nil");

        listeners = initListener();
        if (listeners == null) throw new NullPointerException("listeners is nil");

        codecs = initInsCodec();
        if (codecs == null) throw new NullPointerException("codecs is nil");
        withCodec(TEXT_CODEC);

        if (insFactory == null) insFactory = new CoInsFactory();

        if (io == null) throw new NullPointerException("io is nil");
        // TODO
        io.withActor(new JoinActor()).withActor(new QuitActor());

        this.closed = false;
        LOG.info("{} init", this);
        return this;
    }

    @Override
    public String toString() {
        return id;
    }

    protected Map<String, InsCodec> initInsCodec() {
        return Collections.synchronizedMap(new HashMap<String, InsCodec>());
    }

    protected Map<String, CoGroup> initGroup() {
        return new ConcurrentHashMap<String, CoGroup>();
    }

    public BasicCo() {
        this(IDUtil.newCoID());
    }

    protected Map<String, CoListener> initListener() {
        return Collections.synchronizedMap(new HashMap<String, CoListener>());
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
    public CoGroup group(String name, boolean addIfNil) {
        CoGroup g = groups.get(name);
        if (g == null && addIfNil) {
            groups.putIfAbsent(name, new BasicGroup(name));
            return groups.get(name);
        }
        return g;
    }

    @Override
    public CoFuture<InsResult> join(String name) throws CoException {
        if (isClosed()) throw new CoException("Co closed!");
        CoIns<String> ins = insFactory.newJoin(name, id()).from(this).to(new BasicGroup(name));
        return pub(ins);
    }

    @Override
    public CoFuture<InsResult> quit(String name) throws CoException {
        if (isClosed()) throw new CoException("Co closed!");
        CoGroup g = group(name, false);
        if (g == null) throw new CoException("Quit failed from group:" + name);
        CoIns<String> ins = insFactory.newQuit(name, id()).from(this).to(g);
        return pub(ins);
    }

    @Override
    public CoFuture<InsResult> pub(CoIns<?> ins) throws CoException {
        if (isClosed()) throw new CoException("Co closed!");
        return io.pub(ins);
    }

    @Override
    public CoIns<?> sub(long timeout, TimeUnit unit) throws CoException {
        if (isClosed()) throw new CoException("Co closed!");
        return io.sub(timeout, unit);
    }

    @Override
    public Co withListener(String name, CoListener l) {
        if (name == null || l == null) return this;
        listeners.put(name, l);
        return this;
    }

    @Override
    public CoListener removeListener(String name) {
        if (name == null) return null;
        return listeners.remove(name);
    }

    @Override
    public void close() throws IOException {
        // quit group //TODO abnormal exit how to quit
        for (CoGroup g : groups.values()) {
            if (g.contain(this)) try {
                quit(g.name());
            } catch (CoException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        closed = true; // indicate that co closed
        closeIO();
        if (insFactory != null) insFactory.close();

        listeners.clear();
        codecs.clear();
        groups.clear();
        conf.clear();
        LOG.info("Co-{} closed!", this);
    }

    @Override
    public Co withCodec(InsCodec codec) {
        codecs.put(codec.name(), codec);
        return this;
    }

    @Override
    public InsCodec codec(String name) {
        if (name == null) return TEXT_CODEC;
        return codecs.getOrDefault(name, TEXT_CODEC);
    }

    @Override
    public Co insFactory(CoInsFactory insFactory) {
        if (this.insFactory != null) this.insFactory.close();
        this.insFactory = insFactory;
        return this;
    }

    @Override
    public CoInsFactory insFactory() {
        return insFactory;
    }

    @Override
    public Co io(CoIO io) {
        if (this.io != null) closeIO();
        this.io = io;
        io.init(this);
        return this;
    }

    private void closeIO() {
        if (io != null) {
            try {
                this.io.close();
            } catch (IOException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public CoIO io() {
        return io;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Co) return ((Co) obj).id().equals(id);
        return false;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
