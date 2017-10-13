/**
 * 
 */
package coca.co.init;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.CoConf;
import coca.co.ins.CoInsFactory;
import coca.co.io.CoIO;

/**
 * @author dzh
 * @date Sep 19, 2017 2:50:30 PM
 * @since 0.0.1
 */
public class MapInit implements CoInit<Map<String, String>> {

    static Logger LOG = LoggerFactory.getLogger(MapInit.class);

    /*
     * (non-Javadoc)
     * @see coca.co.init.CoInit#init(java.util.Map)
     */
    @Override
    public Co init(Map<String, String> _conf) {
        // TODO check conf content
        try {
            CoConf conf = new CoConf().init(_conf);

            // ins factory
            CoInsFactory insFactory = conf.newInsFactory();

            // io
            CoIO io = conf.newIO();
            io.selector(conf.newSelector());
            conf.withActors(io);
            conf.withCodecs(io);

            Co co = conf.newCo().insFactory(insFactory).io(io).init(conf);
            return co;
        } catch (Exception e) {
            throw new CoInitException(e);
        }
    }

}
