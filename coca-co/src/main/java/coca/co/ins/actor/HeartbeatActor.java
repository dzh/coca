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
 * @date Oct 16, 2017 4:36:27 PM
 * @since 0.0.1
 */
public class HeartbeatActor extends BasicActor {

    // TODO
    private ExecutorService ES = Executors.newSingleThreadExecutor();

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#name()
     */
    @Override
    public String name() {
        return "HeartbeatActor";
    }

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#accept(coca.co.ins.CoIns)
     */
    @Override
    public boolean accept(CoIns<?> ins) {
        if (!isOpen()) return false;
        return ins.ins().equals(InsConst.HEARTBEAT);
    }

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#submit(coca.co.ins.CoIns)
     */
    @Override
    public void submit(CoIns<?> ins) {
        LOG.debug("{} {} submit {}", io.co(), name(), ins);
        ES.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    Co co = io.co();
                    if (co.equals(ins.from())) return; // ignore self-HEARTBEAT
                    CoGroup g = co.group(ins.toGroup().name(), true);
                    LOG.info("join before {}", g);
                    g.join(ins.from());
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
