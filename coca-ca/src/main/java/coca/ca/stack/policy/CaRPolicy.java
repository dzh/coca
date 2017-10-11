/**
 * 
 */
package coca.ca.stack.policy;

import coca.ca.stack.pointer.CaPointer;

/**
 * @author dzh
 * @date Nov 14, 2016 12:35:37 PM
 */
interface CaRPolicy {

    /**
     * reverse to write cache
     */
    long ROP_BACK_WRITE = 1;

    default boolean isReadable() {
        return true;
    }

    /**
     * 
     * @param key
     * @return read pointer
     */
    CaPointer rp(String key);

    /**
     * 
     * @param op
     * 
     * @return rop
     */
    long rop(long op);

    long rop();

}
