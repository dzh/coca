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
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

}
