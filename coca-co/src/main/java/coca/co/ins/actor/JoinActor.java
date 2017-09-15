/**
 * 
 */
package coca.co.ins.actor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.CoGroup;
import coca.co.ins.CoIns;
import coca.co.ins.InsConst;

/**
 * @author dzh
 * @date Sep 14, 2017 2:41:27 PM
 * @since 0.0.1
 */
public class JoinActor extends BasicActor {

    static final Logger LOG = LoggerFactory.getLogger(JoinActor.class);

    // TODO
    private ExecutorService ES = Executors.newSingleThreadExecutor();

    @Override
    public String name() {
        return "JoinActor";
    }

    @Override
    public boolean accept(CoIns<?> ins) {
        if (!isOpen()) return false;
        return ins.ins().equals(InsConst.JOIN);
    }

    @Override
    public void submit(CoIns<?> ins) {
        LOG.info("{} {} submit {}", io.co(), name(), ins);
        ES.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    Co co = io.co();
                    CoGroup g = co.group(ins.toGroup().name(), true);
                    LOG.info("join before {}", g);
                    g.join(ins.from());
                    if (!co.equals(ins.from())) {
                        CoIns<?> join = co.insFactory().newJoin(g.name(), co.id()).from(co).to(g).to(ins.from());
                        co.pub(join);
                    }
                    LOG.info("join after {}", g);
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }

        });
    }

    @Override
    public void close() {
        super.close();
        ES.shutdown();
        try {
            ES.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn(e.getMessage(), e);
        }
        LOG.info("{} closed.", name());
    }

}
