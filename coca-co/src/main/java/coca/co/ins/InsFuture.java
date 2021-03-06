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

    private CoIns<?> pub;

    public InsFuture() {
        super();
    }

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
        if (super.isDone()) return true;
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
    public void change(Object result) {
        if (result instanceof PacketResult) {
            result(new InsResult(
                    ((PacketResult) result).st() == PacketResult.IOSt.SEND_SUCC ? InsResult.InsSt.PUB_SUCC : InsResult.InsSt.PUB_FAIL));
        }
    }

}
