/**
 * 
 */
package coca.co.ins.actor;

import java.util.Optional;
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
 * @date Sep 14, 2017 7:39:54 PM
 * @since 0.0.1
 */
public class QuitActor extends BasicActor {

    static final Logger LOG = LoggerFactory.getLogger(JoinActor.class);

    // TODO
    private ExecutorService ES = Executors.newSingleThreadExecutor();

    @Override
    public String name() {
        return "QuitActor";
    }

    @Override
    public boolean accept(CoIns<?> ins) {
        if (!isOpen()) return false;
        return ins.ins().equals(InsConst.QUIT);
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

    @Override
    public void submit(CoIns<?> ins) {
        LOG.info("{} {} submit {}", io.co(), name(), ins);
        ES.submit(new Runnable() {

            @Override
            public void run() {
                Co co = io.co();
                CoGroup g = co.group(ins.toGroup().name(), false);
                LOG.info("quit before {}", g);
                Optional.<CoGroup> ofNullable(g).ifPresent(group -> group.quit(ins.from()));
                LOG.info("quit after {}", g);
            }

        });
    }

}
