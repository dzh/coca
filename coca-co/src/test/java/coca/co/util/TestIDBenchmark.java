/**
 * 
 */
package coca.co.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dzh
 * @date Oct 30, 2017 9:37:03 PM
 * @since 0.0.1
 */
public class TestIDBenchmark {

    static Logger LOG = LoggerFactory.getLogger(TestIDBenchmark.class);

    @Test
    public void testUuid() {
        LOG.info("uuid1 {}", IDUtil.uuid1());
        int warm = 100000;
        for (int i = 0; i < warm; i++) {
            IDUtil.uuid();
        }

        int loop = 100000;
        long s0 = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            IDUtil.uuid1();
        }
        long s1 = System.currentTimeMillis();
        LOG.info("uuid1 time-{}", s1 - s0);
    }

    @Test
    public void testUuid2() {
        LOG.info("uuid2 {}", IDUtil.uuid2());
        int warm = 100000;
        for (int i = 0; i < warm; i++) {
            IDUtil.uuid2();
        }

        int loop = 100000;
        long s0 = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            IDUtil.uuid2();
        }
        long s1 = System.currentTimeMillis();
        LOG.info("uuid2 time-{}", s1 - s0);
    }

    @Test
    public void testUuid3() {
        // long st = System.currentTimeMillis();
        // LOG.info("{}", st); // 97-122 48-57
        // for (char ch : String.valueOf(st).toCharArray()) {
        // LOG.info("{}", (char) (ch + 49));
        // }
        // LOG.info("{}", String.valueOf(Integer.MAX_VALUE).length());
        // LOG.info("{}", String.valueOf(System.currentTimeMillis()).length());
        LOG.info("uuid3 {}", IDUtil.uuid3());
        int warm = 100000;
        for (int i = 0; i < warm; i++) {
            IDUtil.uuid3();
        }

        int loop = 100000;
        long s0 = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            IDUtil.uuid3();
        }
        long s1 = System.currentTimeMillis();
        LOG.info("uuid3 time-{}", s1 - s0);
    }

}
