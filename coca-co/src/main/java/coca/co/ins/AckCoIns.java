/**
 * 
 */
package coca.co.ins;

import coca.co.ins.AckCoIns.Ack;
import coca.co.ins.fmt.TextInsFormat;

/**
 * @author dzh
 * @date Sep 12, 2017 8:49:58 PM
 * @since 0.0.1
 */
public class AckCoIns extends BasicCoIns<Ack> {

    public AckCoIns(Ins ins) {
        super(ins);
    }

    public boolean isSucc() {
        return data().code == Ack.SUCC;
    }

    public AckCoIns data(int code, String msg) {
        Ack data = new Ack().code(code).msg(msg);
        data(data);
        return this;
    }

    public static class Ack {
        public static final int SUCC = 0;
        private int code = SUCC;
        private String msg;

        public int code() {
            return code;
        }

        public Ack code(int code) {
            this.code = code;
            return this;
        }

        public String msg() {
            return msg;
        }

        public Ack msg(String msg) {
            this.msg = msg;
            return this;
        }
    }

    public static class AckFormat extends TextInsFormat<Ack> {

        @Override
        public Ack format(String fmt, String data) {
            String[] datas = data.split(" ", 2);
            Ack d = new Ack();

            d.code(Integer.parseInt(datas[0]));
            if (datas.length > 1) {
                d.msg(datas[1]);
            }
            return d;
        }

    }

}
