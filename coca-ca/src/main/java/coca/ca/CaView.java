/**
 * 
 */
package coca.ca;

import java.io.Closeable;

/**
 * @author dzh
 * @date Nov 12, 2016 11:02:58 PM
 */
public interface CaView extends Closeable {

    String name();

    void addCaPolicy(CaPolicy p);

    void delCaPolic(CaPolicy p);

    LCache<?> head();

    LCache<?> tail();

    LCache<?> prev();

    LCache<?> next();

    LCache<?> cache();

    LCache<?> pr(int level);

    LCache<?> pw(int level);

}
