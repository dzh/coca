/**
 * 
 */
package coca.api;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.api.ca.CocaListener;
import coca.api.ca.WithCo;
import coca.api.co.CocaInsFactory;
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
import coca.co.ins.VoidCoIns;

/**
 * <pre>
 * For example:
 * {@code
 * Coca coca = Coca.newCoca("coca",confMap);
 * coca.withStack("stack",caList,listener...);
 * coca.stack("stack").read(key);
 * ...
 * coca.stack("stack").write(val);
 * ...
 * coca.close();
 * }
 * </pre>
 * 
 * @author dzh
 * @date Nov 12, 2016 10:44:46 PM
 * @since 0.0.1
 */
public class Coca implements Closeable, CocaConst {

    static final Logger LOG = LoggerFactory.getLogger(Coca.class);

    protected Map<String, CaStack<String, ?>> stacks = new ConcurrentHashMap<>(8);

    protected Co co;

    private Thread subT;

    private ThreadPoolExecutor insT;

    private CountDownLatch closeLatch;

    private String name;

    public Coca() {
        this("coca");
    }

    public Coca(String name) {
        this.name = name;
    }

    /**
     * 
     * @return coca's name
     */
    public String name() {
        return name;
    }

    public void init(Map<String, String> conf) {
        int nThreads = Integer
                .parseInt(conf.getOrDefault(P_COCA_HANDLER_THREDNUM, String.valueOf(Runtime.getRuntime().availableProcessors() * 10)));
        // Ins thread pool
        insT = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        // create Co
        co = initCo(conf);
        if (co.isClosed()) throw new IllegalStateException("Co init failed!");

        closeLatch = new CountDownLatch(1);
        startSubThread(co); // TODO CoActor is more convenient maybe
        LOG.info("{} init", this.name);
    }

    protected void startSubThread(final Co co) {
        subT = new Thread(() -> {
            CoIns<?> ins = null;
            for (;;) {
                if (co.isClosed()) break;
                try {
                    ins = co.sub(10, TimeUnit.SECONDS);
                    if (ins == VoidCoIns.VOID || ins == null) continue;
                    if (co.equals(ins.from())) {// ignore self ins TODO
                        LOG.debug("{} ignore self-ins {}", Coca.this, ins);
                        continue;
                    }

                    // TODO
                    // if (insT.getQueue().size() > 100000) {
                    // }

                    insT.submit(customHandler(ins).coca(this));
                } catch (InterruptedException e) {
                    continue;
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            LOG.info("{} closed.", subT.getName());
            closeLatch.countDown();
        }, name + "-sub");
        subT.setUncaughtExceptionHandler((t, e) -> {
            LOG.error(e.getMessage(), e);
        });
        subT.start();
        LOG.info("{} start", subT.getName());
    }

    protected InsHandler<?> customHandler(CoIns<?> coIns) {
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
        return newCoca("coca", conf);
    }

    public static final Coca newCoca(String name, Map<String, String> conf) {
        Coca coca = new Coca(name);
        coca.init(conf);
        return coca;
    }

    protected Co initCo(Map<String, String> conf) {
        // default value
        conf.putIfAbsent(CocaConst.P_CO_INS_FACTORY, CocaInsFactory.class.getName());

        return BasicCo.newCo(conf);
    }

    /**
     * 
     * @param name
     *            stack's name to be used for ins's sync-group name
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
        }

        CaStack<String, V> stack = StackManager.newStack(name);// TODO config
        for (int i = ca.size() - 1; i >= 0; i--)
            stack.push(ca.get(i));
        for (StackListener l : listeners) {
            if (l instanceof WithCo) {
                ((WithCo) l).co(this.co);
            }
            stack.addListener(l);
        }

        stacks.put(name, stack);

        try {
            co.join(name).get();  // join
            LOG.info("{} withStack {} succ!", this.name, name);
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e.getMessage(), e);

            stacks.remove(name).close();
            LOG.info("{} withStack {} fail!", this.name, name);
        }

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
            try {
                closeLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {}
            for (Entry<String, CaStack<String, ?>> e : stacks.entrySet()) {
                e.getValue().close();
            }
            stacks.clear();
            LOG.info("{} closed.", this.name);
        }
    }

    private void closeInsT() {
        if (insT != null && !insT.isTerminated()) {
            try {
                insT.shutdown();
                insT.awaitTermination(30, TimeUnit.SECONDS);// TODO
            } catch (InterruptedException e) {}
        }
    }

    private void closeSubT() {
        if (subT != null) subT.interrupt();
    }

    @Override
    public String toString() {
        return this.name;
    }

}
