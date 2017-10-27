/**
 * 
 */
package coca.co;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.ins.CoIns;
import coca.co.ins.CoIns.Ins;
import coca.co.ins.InsResult;
import coca.co.ins.VoidCoIns;

/**
 * @author dzh
 * @date Oct 20, 2017 10:46:48 AM
 * @since 0.0.1
 */
public class TestCoLocal {
    // @Rule
    // public TestRule benchmarkRun = new BenchmarkRule();

    static final Logger LOG = LoggerFactory.getLogger(TestCoLocal.class);

    static final Ins LocalIns = new Ins(1025, "local ins", "");

    static final String LocalGroupName = "local_group";

    static final int COUNT = 10000;

    static final boolean PUB_SYNC = true;

    /**
     * benchmark:(MacPro 2.9 GHz Intel Core i5)
     * 
     * <pre>
     * async: co.pub()
     * pub-10000        fail-0 time-361     sub-10000       time-362        27624/s
     * pub-100000       fail-0 time-1831    sub-100000      time-1831       54615/s
     * pub-1000000      fail-0 time-10315   sub-1000000     time-10316      96946/s
     * pub-10000000     fail-0 time-89624   sub-10000000    time-89625      111577/s
     * pub-20000000     fail-0 time-192132  sub-20000000    time-192133     104095/s
     * 
     * sync: co.pub().get()
     * pub-10000        fail-0 time-560     sub-10000       time-561        17825/s
     * pub-100000       fail-0 time-3119    sub-100000      time-3120       32051/s
     * pub-1000000      fail-0 time-24465   sub-1000000     time-24466      40875/s
     * pub-10000000     fail-0 time-248110  sub-10000000    time-248111     40395/s
     * </pre>
     * 
     * 
     * @param args
     * @throws Exception
     */
    public static final void main(String[] args) throws Exception {
        final Co co = BasicCo.newCo(Collections.<String, String> emptyMap());
        co.join(LocalGroupName).get();

        Thread subT = new Thread("subT-local") {
            public long count = 0L;

            public void run() {
                LOG.info("subT start");
                long s0 = System.currentTimeMillis();
                while (true) {
                    try {
                        if (co.isClosed()) break;

                        CoIns<?> subIns = co.sub(2, TimeUnit.SECONDS);
                        if (VoidCoIns.VOID.equals(subIns)) continue;
                        // LOG.info("{}", subIns);
                        count++;
                        if (count == COUNT) break;
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
                LOG.info("sub-{} time-{}", count, (System.currentTimeMillis() - s0));
            }
        };
        subT.start();

        Thread pubT = new Thread("pubT-local") {
            public long succCount = 0L;
            public long failCount = 0L;

            public void run() {
                LOG.info("pubT start");
                long s0 = System.currentTimeMillis();
                for (int i = 0; i < COUNT; i++) {
                    // CoIns<?> ins = new TextCoIns(LocalIns).id(IDUtil.uuid()).from(co).to(new
                    // BasicGroup(LocalGroupName));
                    CoIns<?> ins = co.insFactory().newIns(LocalIns).from(co).to(new BasicGroup(LocalGroupName));
                    try {
                        CoFuture<InsResult> f = co.pub(ins);
                        if (PUB_SYNC) {
                            InsResult r = f.get(1, TimeUnit.SECONDS);
                            if (r == null) {
                                failCount++;
                            } else {
                                succCount++;
                            }
                        } else {
                            succCount++;
                        }
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e.getCause());
                        failCount++;
                    }
                }
                LOG.info("pub-{} fail-{} time-{}", succCount + failCount, failCount, (System.currentTimeMillis() - s0));
            }

        };
        pubT.start();
        pubT.join();

        co.quit(LocalGroupName).get();
        Thread.sleep(2000);
        co.close();
    }

}
