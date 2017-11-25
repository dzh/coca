/**
 *
 */
package coca.api.ca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.api.CocaConst;
import coca.api.co.StackCoIns;
import coca.ca.stack.StackEvent;
import coca.ca.stack.StackListener;
import coca.co.BasicGroup;
import coca.co.Co;
import coca.co.CoException;

/**
 * @author dzh
 * @date Oct 13, 2017 10:51:27 AM
 * @since 0.0.1
 */
public class CocaListener implements StackListener, WithCo {

    static Logger LOG = LoggerFactory.getLogger(CocaListener.class);

    private Co co;

    /*
     * (non-Javadoc)
     * @see coca.ca.stack.StackListener#stackChange(coca.ca.stack.StackEvent)
     */
    @Override
    public void stackChange(StackEvent evnt) {
        if (evnt.isLocalChanged() && evnt.val().isSync()) {
            pubEvict(evnt);
        }
    }

    protected void pubEvict(StackEvent evnt) {
        StackCoIns ins = StackCoIns.newStackIns(evnt.stack(), CocaConst.EVICT);
        ins.from(co).to(new BasicGroup(evnt.getStackName())).data(evictData(evnt));
        try {
            co.pub(ins);
        } catch (CoException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public String evictData(StackEvent evnt) {
        return evnt.getStackName() + " " + evnt.ca().name() + " " + evnt.key().toString();
    }

    @Override
    public void co(Co co) {
        this.co = co;
    }

}
