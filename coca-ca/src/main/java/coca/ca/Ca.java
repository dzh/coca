/**
 * 
 */
package coca.ca;

/**
 * @author dzh
 * @date Nov 14, 2016 12:46:26 PM
 */
public interface Ca {

    CaView addView(String name, LCache<?>... ca);

    void delView(String name);

    CaView view(String name);

}
