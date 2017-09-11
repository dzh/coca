/**
 * 
 */
package coca.co;

import java.util.Collection;

/**
 * ThreadSafe
 * 
 * @author dzh
 * @date Sep 7, 2017 6:15:10 PM
 * @since 0.0.1
 */
public class BasicCoGroup implements CoGroup {

    private String name;

    public BasicCoGroup(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.CoGroup#name()
     */
    @Override
    public String name() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.CoGroup#members()
     */
    @Override
    public Collection<Co> members() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.CoGroup#join(coca.co.Co)
     */
    @Override
    public boolean join(Co co) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.CoGroup#quit(coca.co.Co)
     */
    @Override
    public boolean quit(Co co) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof CoGroup) { return ((CoGroup) obj).name().equals(name()); }
        return false;
    }

}
