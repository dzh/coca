/**
 * 
 */
package coca.demo.benchmark.rmq;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.api.CocaConst;
import coca.demo.sample.CocaSample;
import coca.rmq.co.io.RMQChannelSelector;

/**
 * @author dzh
 * @date Oct 24, 2017 5:04:17 PM
 * @since 0.0.1
 */
public class CocaRMQSub {

    static final Logger LOG = LoggerFactory.getLogger(CocaRMQSub.class);

    static Map<String, String> CONF = new HashMap<>(8);
    static {
        CONF.put(CocaConst.P_CO_IO_SELECTOR, RMQChannelSelector.class.getName());
    }

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        Configurator.initialize("CocaRMQBenchmark", Thread.currentThread().getContextClassLoader(), "rmq-log4j2.xml");

        // init coca
        CocaSample sub = new CocaSample("appSub", "syncGroup");
        sub.initCoca(CONF);

        int warmCount = CocaRMQBenchmark.warmCount;
        int writeCount = CocaRMQBenchmark.writeCount;
        int total = warmCount + writeCount;

        long s0 = System.currentTimeMillis();
        while (true) {
            if (sub.countSub() >= total) {
                break;
            }
            LOG.info("total-{} sub-{}", total, sub.countSub());
            Thread.sleep(5000L);
        }
        long s1 = System.currentTimeMillis();

        sub.close();
        LOG.info("sub-{} time-{}", sub.countSub() - warmCount, s1 - s0);
    }

    // public static boolean pubExit(CocaSample sub) {
    // return !new File(CocaRMQBenchmark.PubRunFlag).exists();
    // }

}
