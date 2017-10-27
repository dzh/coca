/**
 * 
 */
package coca.co.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

import org.junit.Test;

/**
 * @author dzh
 * @date Sep 11, 2017 8:48:48 PM
 * @since 0.0.1
 */
public class TestIDUtil {

    @Test
    public void testCoID() {
        String id = IDUtil.newCoID();
        System.out.println(id);
        System.out.println(id.length());
    }

    @Test
    public void testSplitCoID() {
        String id = "1505188694732222_99714_dzh-laptop.local";
        String[] split = IDUtil.splitCoID(id);
        System.out.println(Arrays.asList(split));
    }

    public void testGetIP() throws SocketException, Exception {
        Enumeration<NetworkInterface> iter = NetworkInterface.getNetworkInterfaces();
        while (iter.hasMoreElements()) {
            NetworkInterface ni = iter.nextElement();
            if (ni.isLoopback()) continue;
            System.out.println(ni.getName());
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                System.out.println(addrs.nextElement().getHostAddress());
            }
        }

        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        System.out.println(ManagementFactory.getRuntimeMXBean().getStartTime());
        System.out.println(ManagementFactory.getRuntimeMXBean().getUptime());
        Thread.sleep(100);
        System.out.println(ManagementFactory.getRuntimeMXBean().getStartTime());
        System.out.println(ManagementFactory.getRuntimeMXBean().getUptime());
        System.out.println(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime());

        // byte[] src = "1234567890".getBytes("utf-8");
        // String str = Base64.getEncoder().encodeToString(src);
        // System.out.println(str);
        //
        // src = "1".getBytes("utf-8");
        // str = Base64.getEncoder().encodeToString(src);
        // System.out.println(str);
    }

}
