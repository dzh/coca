/**
 * 
 */
package coca.co;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import coca.co.ins.CoIns;
import coca.co.ins.CoInsFactory;
import coca.co.ins.InsResult;
import coca.co.ins.VoidCoIns;
import coca.co.io.CoIO;

/**
 * Features:
 * <ul>
 * <li></li>
 * </ul>
 * 
 * For example:
 * 
 * 
 * @author dzh
 * @date Nov 14, 2016 1:09:39 PM
 * @since 0.0.1
 */
public interface Co extends Closeable {

    String id();

    /**
     * initialize Co with CoConf
     * 
     * @return
     * @throws NullPointerException
     */
    Co init(CoConf conf);

    CoConf conf();

    boolean isClosed();

    /**
     * {@link CoGroup} list which {@link Co} has joined
     * 
     * @return
     */
    Collection<CoGroup> groups();

    /**
     * find {@link CoGroup} matched the name
     * 
     * @return CoGroup
     */
    CoGroup group(String name, boolean addIfNil);

    /**
     * 
     * @param name
     *            CoGroup's name
     * @return
     */
    CoFuture<InsResult> join(String name) throws CoException;

    /**
     * 
     * @param name
     *            CoGroup's name
     * @return
     */
    CoFuture<InsResult> quit(String name) throws CoException;

    /**
     * publish ins
     * 
     * @param ins
     *            CoIns
     * @return Co
     * @throws CoException
     */
    CoFuture<InsResult> pub(CoIns<?> ins) throws CoException;

    /**
     * For example:
     * 
     * <pre>
     * {@code
     *     CoIns<?> ins = null;
     *     for(;;){
     *         try{
     *           ins = sub(1,TimeUnit.SECONDS);
     *           if(ins == VoidCoIns.VOID) continue;
     *         }catch(CoException e){
     *           LOG.error(...)
     *           break;
     *         }
     *         //handle ins
     *         ...
     *     }
     * }
     * </pre>
     * 
     * @param timeout
     *            time to wait for a CoIns. if wait_msec=-1 then to wait
     *            forever until a CoIns received or throw CoException
     * @param unit
     *            TimeUnit
     * @return CoIns or {@link VoidCoIns#VOID} if timeout
     * @throws CoException
     * @throws InterruptedException
     */
    CoIns<?> sub(long timeout, TimeUnit unit) throws CoException, InterruptedException;

    Co insFactory(CoInsFactory insFactory);

    CoInsFactory insFactory();

    Co io(CoIO io);

    CoIO io();

    Co withListener(String name, CoListener l);

    CoListener removeListener(String name);

}
