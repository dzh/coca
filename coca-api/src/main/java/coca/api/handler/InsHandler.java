/**
 * 
 */
package coca.api.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.api.Coca;
import coca.co.ins.CoIns;
import coca.co.ins.fmt.MapTextInsFormat;

/**
 * @author dzh
 * @date Oct 13, 2017 4:01:34 PM
 * @since 0.0.1
 */
public abstract class InsHandler<T> implements Runnable {

    static Logger LOG = LoggerFactory.getLogger(InsHandler.class);

    public static final MapTextInsFormat MapInsFormat = new MapTextInsFormat();

    private Coca coca;

    CoIns<T> ins;

    public InsHandler(CoIns<T> ins) {
        this.ins = ins;
    }

    public CoIns<?> ins() {
        return ins;
    }

    // public InsHandler<T> ins(CoIns<T> ins) {
    // this.ins = ins;
    // return this;
    // }

    public Coca coca() {
        return this.coca;
    }

    public InsHandler<T> coca(Coca coca) {
        this.coca = coca;
        return this;
    }

}
