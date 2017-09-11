
package coca.co.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.CoConst;

/**
 * TODO
 * 
 * @date Sep 10, 2017 12:39:40 AM
 * @since 0.0.1
 */
public class MD5Hash {

    static final Logger LOG = LoggerFactory.getLogger(MD5Hash.class);

    private static String byteArrayToHexString(byte b[]) {
        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String md5hex(String content, Charset charset) {
        String md5 = content;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charset == null) charset = CoConst.UTF8;
            md5 = byteArrayToHexString(md.digest(content.getBytes(charset)));
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return md5;
    }

    public static int md5hash(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(bytes)).hashCode();
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return 0;
    }

    public static String md5hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(bytes));
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return "";
    }

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}