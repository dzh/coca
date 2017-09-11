/**
 * 
 */
package coca.co;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import coca.co.ins.CoIns;
import coca.co.ins.CoInsFactory;
import coca.co.ins.codec.InsCodec;
import coca.co.io.CoIO;
import coca.co.io.PacketResult;

/**
 * <p>
 * Features:
 * <ul>
 * <li>Received {@link }</li>
 * </ul>
 * </p>
 * 
 * For example:
 * 
 * <pre>
 * 
 * </pre>
 * 
 * @author dzh
 * @date Nov 14, 2016 1:09:39 PM
 * @since 0.0.1
 */
public interface Co extends Closeable {

    String id();

    Co init();

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
    CoGroup group(String name);

    /**
     * 
     * @param name
     *            group name
     * @return {@link CoGroup} to join
     */
    CoFuture<CoGroup> join(String name) throws CoException;

    /**
     * 
     * @param name
     *            group name
     * @return {@link CoGroup} to quit
     */
    CoFuture<CoGroup> quit(String name) throws CoException;

    /**
     * publish ins
     * 
     * @param ins
     *            CoIns
     * @return Co
     * @throws CoException
     */
    CoFuture<PacketResult> pub(CoIns<?> ins) throws CoException;

    /**
     * For example:
     * 
     * <pre>
     * {@code
     *     CoIns<?> ins = null;
     *     for(;;){
     *         try{
     *           ins = sub(1000);
     *           if(ins==null) continue;
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
     * @param TimeUnit
     * @return CoIns or null if timeout
     * @throws CoException
     *             sub
     */
    CoIns<?> sub(long timeout, TimeUnit unit) throws CoException;

    Co withCodec(InsCodec codec);

    InsCodec codec(String name);

    Co insFactory(CoInsFactory insFactory);

    CoInsFactory insFactory();

    Co io(CoIO io);

    CoIO io();

    Co withListener(String name, CoListener l);

    CoListener removeListener(String name);

}
