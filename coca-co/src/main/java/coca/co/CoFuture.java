/**
 * 
 */
package coca.co;

import java.util.concurrent.Future;

/**
 * @author dzh
 * @date Nov 14, 2016 2:09:06 PM
 * @since 1.0
 */
public interface CoFuture<V> extends Future<V> {

    void result(V result);

    /**
     * @param result
     *            parent future's result
     */
    void change(Object result);

    /**
     * 
     * @return
     */
    CoFuture<?> next();

    /**
     * 
     * @return
     */
    CoFuture<?> next(CoFuture<?> f);

}
