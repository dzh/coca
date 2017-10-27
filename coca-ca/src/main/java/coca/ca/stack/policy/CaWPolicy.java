/**
 * 
 */
package coca.ca.stack.policy;

import coca.ca.CaValue;
import coca.ca.stack.pointer.CaPointer;

/**
 * @author dzh
 * @date Nov 14, 2016 12:36:00 PM
 */
interface CaWPolicy<K, V> {

    /**
     * All cache that wp pointed to be written
     */
    long WOP_ALL_WRITE = 1;

    /**
     * Stop writing if failure
     */
    long WOP_ABORT_ON_FAIL = 1 << 1;

    boolean isWritable();

    CaPointer<K, V> wp(CaValue<K, V> val);

    long wop(long op);

    long wop();

}
