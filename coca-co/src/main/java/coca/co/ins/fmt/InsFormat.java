/**
 * 
 */
package coca.co.ins.fmt;

/**
 * format CoIns.data to F with fmt
 * 
 * @author dzh
 * @date Sep 6, 2017 10:55:17 AM
 * @since 0.0.1
 */
public interface InsFormat<T, F> {
    /**
     * 
     * @param fmt
     *            format expression
     * @param data
     *            CoIns.data
     * @return
     * @throws InsFormatException
     */
    F format(String fmt, T data);

}
