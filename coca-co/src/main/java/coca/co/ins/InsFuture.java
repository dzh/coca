/**
 * 
 */
package coca.co.ins;

import coca.co.BasicFuture;
import coca.co.io.channel.PacketResult;

/**
 * @author dzh
 * @date Sep 12, 2017 7:59:44 PM
 * @since 0.0.1
 */
public class InsFuture extends BasicFuture<InsResult> {

    private final CoIns<?> pub;

    private InsResult result;

    public InsFuture(CoIns<?> pub) {
        this.pub = pub;
    }

    public boolean hasAck() {
        return (pub.cntl() & CoIns.CNTL_ACK) == CoIns.CNTL_ACK;
    }

    public CoIns<?> pub() {
        return pub;
    }

    @Override
    public boolean isDone() {
        if (isCancelled()) return true;
        boolean done = false;
        if (hasAck()) {
            done = result.st == InsResult.InsSt.ACK_SUCC || result.st == InsResult.InsSt.ACK_FAIL
                    || result.st == InsResult.InsSt.ACK_TIMEOUT;
        } else {
            done = result.st == InsResult.InsSt.PUB_SUCC || result.st == InsResult.InsSt.PUB_FAIL
                    || result.st == InsResult.InsSt.PUB_TIMEOUT;
        }
        return done;
    }

    @Override
    protected void doDone() {

    }

    @Override
    public InsResult change(Object result) {
        if (result instanceof PacketResult) {
            result(new InsResult(
                    ((PacketResult) result).st() == PacketResult.IOSt.SEND_SUCC ? InsResult.InsSt.PUB_SUCC : InsResult.InsSt.PUB_FAIL));
        }
        return null;
    }

}
