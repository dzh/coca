/**
 * 
 */
package coca.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.api.ca.CocaListener;
import coca.api.ca.WithCo;
import coca.api.co.StackCoIns;
import coca.api.handler.EvictHandler;
import coca.api.handler.InsHandler;
import coca.api.handler.VoidHandler;
import coca.ca.Ca;
import coca.ca.stack.CaStack;
import coca.ca.stack.StackListener;
import coca.ca.stack.StackManager;
import coca.co.BasicCo;
import coca.co.Co;
import coca.co.CoConst;
import coca.co.CoException;
import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;

/**
 * @author dzh
 * @date Nov 12, 2016 10:44:46 PM
 * @since 0.0.1
 */
public class Coca implements Closeable {

    static final Logger LOG = LoggerFactory.getLogger(Coca.class);

    private Map<String, CaStack<String, ?>> stacks = new ConcurrentHashMap<>(8);

    private Co co;

    private volatile boolean closed = false;

    private Thread subT;

    private ExecutorService insT;

    private Coca() {}

    protected void init(Map<String, String> conf) {
        // Ins thread pool
        insT = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);// TODO config

        // create Co
        co = initCo(conf);
        if (co.isClosed()) throw new IllegalStateException("Co init failed!");
        startSubThread(co); // TODO CoActor is more convenient maybe
    }

    protected void startSubThread(final Co co) {
        subT = new Thread(() -> {
            CoIns<?> ins = null;
            for (;;) {
                if (closed) break;
                try {
                    ins = co.sub(10, TimeUnit.SECONDS);
                    if (ins == null) continue;
                    if (ins.from().equals(co)) {// ignore self ins TODO
                        LOG.info("sub self-ins {}", ins);
                        continue;
                    }

                    insT.submit(customHandler(ins).coca(this));
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }, "Coca-SubThread");
        subT.setUncaughtExceptionHandler((t, e) -> {
            LOG.error(e.getMessage(), e);
        });
        subT.start();
        LOG.info("{} start!", subT.getName());
    }

    public InsHandler<?> customHandler(CoIns<?> coIns) {
        Ins ins = coIns.ins();
        if (ins.equals(CocaConst.EVICT)) return new EvictHandler((StackCoIns) coIns);
        return VoidHandler.VOID;
    }

    /**
     * 
     * @param conf
     *            {@link CoConst} contains configuration keys
     * @return
     */
    public static final Coca newCoca(Map<String, String> conf) {
        Coca coca = new Coca();
        coca.init(conf);
        return coca;
    }

    protected Co initCo(Map<String, String> conf) {
        return BasicCo.newCo(conf);
    }

    /**
     * 
     * @param name
     *            stack's name to be used for ins's group name
     * @param ca
     *            small index is on the stack's top
     * @param listeners
     *            {@link CocaListener}
     * @return
     * @throws CoException
     */
    public <V> CaStack<String, V> withStack(String name, List<Ca<String, V>> ca, StackListener... listeners) throws CoException {
        if (stacks.containsKey(name)) {
            stacks.get(name).close();
        } else {
            co.join(name); // join
        }

        CaStack<String, V> stack = StackManager.newStack(name);
        for (int i = ca.size() - 1; i >= 0; i--)
            stack.push(ca.get(i));
        for (StackListener l : listeners) {
            if (l instanceof WithCo) {
                ((WithCo) l).co(this.co);
            }
            stack.addListener(l);
        }

        stacks.put(name, stack);
        return stack;
    }

    @SuppressWarnings("unchecked")
    public <V> CaStack<String, V> stack(String name) {
        if (name == null) return null;
        return ((CaStack<String, V>) stacks.get(name));
    }

    @Override
    public void close() throws IOException {
        try {
            if (co != null) co.close();
        } finally {
            closeSubT();
            closeInsT();
            stacks.clear();
        }
    }

    private void closeInsT() {
        if (insT != null) {
            try {
                insT.shutdown();
                insT.awaitTermination(30, TimeUnit.SECONDS);// TODO
            } catch (InterruptedException e) {}
        }
    }

    private void closeSubT() {
        closed = true;
        if (subT != null) subT.interrupt();
    }

}
