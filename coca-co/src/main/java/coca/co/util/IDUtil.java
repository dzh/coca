/**
 * 
 */
package coca.co.util;

import java.lang.management.ManagementFactory;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author dzh
 * @date Sep 11, 2017 4:47:19 PM
 * @since 0.0.1
 */
public class IDUtil {

    /**
     * TimestampRandomInt[3]_PID_Hostname|
     * 
     * @return
     */
    public static final String newCoID() {
        return newCoID("_");
    }

    public static final String newCoID(String split) {
        String[] name = ManagementFactory.getRuntimeMXBean().getName().split("@");
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1, 10) + ThreadLocalRandom.current().nextInt(1, 10)
                + ThreadLocalRandom.current().nextInt(1, 10) + split + name[0] + split + name[1];
    }

    public static final String[] splitCoID(String id) {
        return splitCoID("_", id);
    }

    public static final String[] splitCoID(String split, String id) {
        return id.split(split, 3);
    }

    // public static final String encode(String id) {
    // return new String(Base64.getEncoder().encode(id.getBytes(CoConst.UTF8)), CoConst.UTF8);
    // }

    // public static final String decode(String id) {
    // return new String(Base64.getDecoder().decode(id.getBytes(CoConst.UTF8)), CoConst.UTF8);
    // }

    public static final String getPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.substring(0, name.indexOf('@'));
    }

    public static final String uuid() {
        return uuid3();
    }

    public static final String uuid1() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    public static final String uuid2() {
        String ts = String.valueOf(System.currentTimeMillis());
        String hash = String.valueOf(Math.abs(ManagementFactory.getRuntimeMXBean().getName().hashCode()));
        int ranlen = 32 - ts.length() - hash.length();

        StringBuilder buf = new StringBuilder(ts.length() + ranlen + hash.length());
        buf.append(ts);
        buf.append(hash);
        for (int i = 0; i < ranlen; i++) {
            buf.append(ThreadLocalRandom.current().nextInt(1, 10));
        }
        return buf.toString();
    }

    public static final String uuid3() {
        String ts = Long.toHexString(System.currentTimeMillis());
        String hash = String.valueOf(Integer.toHexString(ManagementFactory.getRuntimeMXBean().getName().hashCode()));
        int ranlen = 32 - ts.length() - hash.length();

        StringBuilder buf = new StringBuilder(32);
        buf.append(ts);
        // buf.append("-");
        buf.append(hash);
        // buf.append("-");
        for (int i = 0; i < ranlen; i++) {
            buf.append(Integer.toHexString(ThreadLocalRandom.current().nextInt(0, 16)));
        }
        return buf.toString();
    }

}
