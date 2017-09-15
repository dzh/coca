/**
 * 
 */
package coca.co.ins;

/**
 * @author dzh
 * @date Sep 12, 2017 8:05:48 PM
 * @since 0.0.1
 */
public class InsResult {

    private AckCoIns ack;

    InsSt st = InsSt.PUB;

    public InsResult(InsSt st, AckCoIns ack) {
        this.st = st;
        this.ack = ack;
    }

    public InsResult(InsSt st) {
        this.st = st;
    }

    public AckCoIns ack() {
        return ack;
    }

    public InsSt st() {
        return st;
    }

    public boolean isAckSucc() {
        return st.equals(InsSt.ACK_SUCC);
    }

    public static enum InsSt {
        PUB, PUB_SUCC, PUB_FAIL, PUB_TIMEOUT, ACK_SUCC, ACK_FAIL, ACK_TIMEOUT
    }

}
