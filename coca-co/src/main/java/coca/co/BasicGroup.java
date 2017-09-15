/**
 * 
 */
package coca.co;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ThreadSafe
 * 
 * @author dzh
 * @date Sep 7, 2017 6:15:10 PM
 * @since 0.0.1
 */
public class BasicGroup implements CoGroup {

    private String name;

    Map<String, Co> members;

    public BasicGroup(String name) {
        this.name = name;
        members = new ConcurrentHashMap<>();
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
        return members.values();
    }

    /*
     * (non-Javadoc)
     * @see coca.co.CoGroup#join(coca.co.Co)
     */
    @Override
    public boolean join(Co co) {
        return members.putIfAbsent(co.id(), co) == null;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.CoGroup#quit(coca.co.Co)
     */
    @Override
    public boolean quit(Co co) {
        return members.remove(co.id()) != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof CoGroup) { return ((CoGroup) obj).name().equals(name()); }
        return false;
    }

    @Override
    public boolean contain(Co member) {
        return members.containsKey(member.id());
    }

    @Override
    public String toString() {
        return name + " " + members.toString();
    }

}
