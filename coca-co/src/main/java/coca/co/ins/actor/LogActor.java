/**
 * 
 */
package coca.co.ins.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.ins.CoIns;

/**
 * @author dzh
 * @date Sep 15, 2017 3:03:40 AM
 * @since 0.0.1
 */
public class LogActor extends BasicActor {

    static final Logger LOG = LoggerFactory.getLogger(LogActor.class);

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#name()
     */
    @Override
    public String name() {
        return "LogActor";
    }

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#accept(coca.co.ins.CoIns)
     */
    @Override
    public boolean accept(CoIns<?> ins) {
        return ins.ins().code() > 1024;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.ins.actor.CoActor#submit(coca.co.ins.CoIns)
     */
    @Override
    public void submit(CoIns<?> ins) {
        LOG.info("{} {} submit {}", io.co(), name(), ins);
    }

    @Override
    public void close() {
        super.close();
    }

}
