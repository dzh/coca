/**
 * 
 */
package coca.co.ins;

import java.util.List;

import coca.co.Co;
import coca.co.CoGroup;
import coca.co.ins.fmt.InsFormat;

/**
 * Co Instruction
 * 
 * @author dzh
 * @date Aug 9, 2017 7:07:52 PM
 * @since 0.0.1
 */
public interface CoIns<T> extends Cloneable {

    public static final int MAX_DATA_BYTE = 1024 * 1024;// 1M

    String id();

    /**
     * Instruction
     * 
     * <pre>
     * Ins's code:
     * {@link Ins} reserved:[0,1024]
     * user definition:[1025,+∞)
     * </pre>
     * 
     * @return {@link Ins}
     */
    Ins ins();

    CoIns<T> ins(Ins ins);

    /**
     * Instruction data
     * 
     * @return
     */
    T data();

    CoIns<T> data(T data);

    Co from();

    List<Co> toCo();

    CoGroup toGroup();

    CoIns<T> from(Co c);

    CoIns<T> to(CoGroup group);

    CoIns<T> to(Co... co);

    /**
     * InsCodec's name
     * 
     * @param codec
     * @return
     */
    CoIns<T> codec(String codec);

    String codec();

    <F> F format(InsFormat<T, F> formatter);

    /**
     * Ins structure: code data
     * 
     * MaxLength:
     */
    public static class Ins implements InsConst {
        private int code = 0;
        private String name;
        private String format;

        public static final String FMT_SEPR = " ";

        public static final int MAX_NAME_BYTE = 127;// byte
        public static final int MAX_FORMAT_BYTE = 127;// byte

        public Ins(int code, String name, String format) {
            this.code = code;
            this.name = name;
            this.format = format;
        }

        public String name() {
            return this.name;
        }

        public int code() {
            return this.code;
        }

        public String format() {
            return this.format;
        }

        @Override
        public String toString() {
            return "Ins_" + code + "_" + name + "_" + format;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Ins) return ((Ins) obj).code == this.code;
            return false;
        }

    }

}
