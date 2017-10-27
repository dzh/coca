/**
 * 
 */
package coca.co.ins.actor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    public void submit(final CoIns<?> ins) {
        LOG.info("{} {} submit {}", io.co(), name(), ins);
        ES.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    Co co = io.co();
                    CoGroup g = co.group(ins.toGroup().name(), true);
                    LOG.info("join before {}", g);
                    if (g.join(ins.from()) && !co.equals(ins.from())) {
                        CoIns<?> hb = co.insFactory().newHeartbeat(g.name(), co.id()).from(co).to(g).to(ins.from());
                        co.pub(hb);
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
    }

}
