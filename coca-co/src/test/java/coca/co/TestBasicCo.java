/**
 *
 */
package coca.co;

import coca.co.ins.AckCoIns;
import coca.co.ins.InsConst;
import coca.co.util.IDUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author dzh
 * @date Sep 2, 2017 8:39:11 PM
 * @since 0.0.1
 */
public class TestBasicCo {

    static Logger LOG = LoggerFactory.getLogger(TestBasicCo.class);

    public void testCoId() throws UnsupportedEncodingException {
        // BasicCo co = new BasicCo();
        // System.out.println(co.id());
        // System.out.println(co.id().length());

        String a = "";
        System.out.println(a.getBytes("utf-8").length);

        System.out.println("a".getBytes().length);
        System.out.println("z".getBytes().length);
        System.out.println((byte) 127);

    }

    @Test
    public void testIns() {
        LOG.info(InsConst.JOIN.toString());

        LOG.info(new AckCoIns(InsConst.ACK).id(IDUtil.uuid()).toString());
    }

    public void testMD5() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5 = md.digest("coca".getBytes());
        for (byte b : md5)
            System.out.println(b);
        System.out.println(md5.length);
    }

    public void testMagic() {
        int magic = 0x43834361;// CoCa
        System.out.println(magic);
        System.out.println((char) (magic >>> 24));
        magic = 0x63836361;// coca
        System.out.println(magic);
        System.out.println((char) (magic >>> 24));

    }

}
