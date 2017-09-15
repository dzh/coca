/**
 * 
 */
package coca.co.ins.actor;

import coca.co.ins.CoIns;
import coca.co.io.CoIO;

/**
 * @author dzh
 * @date Sep 8, 2017 2:02:22 PM
 * @since 0.0.1
 */
public interface CoActor {

    String name();

    CoActor init(CoIO io);

    boolean accept(CoIns<?> ins);

    void submit(CoIns<?> ins);

    boolean isOpen();

    void close();
}
