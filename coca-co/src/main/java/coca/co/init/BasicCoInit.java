/**
 * 
 */
package coca.co.init;

import java.util.Map;

import coca.co.BasicCo;
import coca.co.Co;
import coca.co.CoConf;
import coca.co.io.BasicCoIO;
import coca.co.io.ChannelSelector;
import coca.co.io.CoIO;
import coca.co.util.IDUtil;

/**
 * @author dzh
 * @date Sep 19, 2017 2:50:30 PM
 * @since 0.0.1
 */
public class BasicCoInit implements CoInit {

    /*
     * (non-Javadoc)
     * @see coca.co.init.CoInit#init(java.util.Map)
     */
    @Override
    public Co init(Map<String, String> conf) {
        // TODO check conf content
        try {
            // new co
            Class<?> clazz = getClass().getClassLoader().loadClass(conf.getOrDefault(P_CO, BasicCo.class.getName()));
            Co co = (Co) clazz.getConstructor(String.class).newInstance(IDUtil.newCoID());
            // new coio
            clazz = getClass().getClassLoader().loadClass(conf.getOrDefault(P_CO_IO, BasicCoIO.class.getName()));
            CoIO io = (CoIO) clazz.newInstance();
            // new selector
            clazz = getClass().getClassLoader().loadClass(conf.get(P_CO_IO_SELECTOR));
            ChannelSelector sel = (ChannelSelector) clazz.newInstance();
            // init co
            co.io(io.selector(sel)).init(new CoConf().init(co, conf));
            return co;
        } catch (Exception e) {
            throw new CoInitException(e);
        }
    }

}
