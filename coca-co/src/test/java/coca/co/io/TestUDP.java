/**
 * 
 */
package coca.co.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://docs.oracle.com/javase/tutorial/networking/datagrams/clientServer.html
 * 
 * @author dzh
 * @date Jul 26, 2017 6:32:33 PM
 */
public class TestUDP {

    static Logger LOG = LoggerFactory.getLogger(TestUDP.class);

    @Before
    public void listenServer() throws IOException, InterruptedException {
        final DatagramSocket datagramSocket = new DatagramSocket(8080);
        new Thread() {
            public void run() {
                try {
                    byte[] buffer = new byte[3];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    datagramSocket.receive(packet);

                    LOG.info("recv ip-{} port-{} data-{}", packet.getAddress().getHostAddress(), packet.getPort(),
                            new String(packet.getData(), "utf-8"));

                } catch (Exception e) {
                    LOG.error(e.getMessage(), e.fillInStackTrace());
                } finally {
                    datagramSocket.close();
                }
            }
        }.start();
    }

    @Test
    public void sendPacket() throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(null);
        datagramSocket.bind(new InetSocketAddress("127.0.0.1", 8081));

        byte[] buffer = "123".getBytes("utf-8");
        InetAddress receiverAddress = InetAddress.getLocalHost();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 8080);
        packet.setAddress(InetAddress.getByName("127.0.0.1"));
        datagramSocket.send(packet);
        datagramSocket.close();

        LOG.info("send ip-{} port-{} data-{}", packet.getAddress().getHostAddress(), packet.getPort(),
                new String(packet.getData(), "utf-8"));
    }

}
