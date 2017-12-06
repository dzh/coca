/**
 * 
 */
package coca.co;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import coca.co.ins.CoIns;
import coca.co.ins.CoInsFactory;
import coca.co.ins.InsResult;
import coca.co.io.CoIO;

/**
 * ThreadSafe
 * 
 * @author dzh
 * @date Sep 7, 2017 6:15:10 PM
 * @since 0.0.1
 */
public class BasicGroup implements CoGroup {

    private String name;

    ConcurrentMap<String, Co> members;

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
        try {
            return members.putIfAbsent(co.id(), of(co)) == null;
        } finally {
            Co p = members.get(co.id());
            if (p != null && p instanceof CoProxy) ((CoProxy) p).lastAccess(System.currentTimeMillis());
        }
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

    public static final CoProxy of(Co co) {
        return new CoProxy(co.id());
    }

    public static class CoProxy implements Co {

        private String id;
        private long lastAccess; // clear this if lastAccess + timeout < now

        public CoProxy(String id) {
            this.id = id;
        }

        public long lastAccess() {
            return lastAccess;
        }

        public CoProxy lastAccess(long lastAccess) {
            this.lastAccess = lastAccess;
            return this;
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public Co init(CoConf conf) {
            return null;
        }

        @Override
        public CoConf conf() {
            return null;
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public Collection<CoGroup> groups() {
            return null;
        }

        @Override
        public CoGroup group(String name, boolean addIfNil) {
            return null;
        }

        @Override
        public CoFuture<InsResult> join(String name) throws CoException {
            return null;
        }

        @Override
        public CoFuture<InsResult> quit(String name) throws CoException {
            return null;
        }

        @Override
        public CoFuture<InsResult> pub(CoIns<?> ins) throws CoException {
            return null;
        }

        @Override
        public CoIns<?> sub(long timeout, TimeUnit unit) throws CoException, InterruptedException {
            return null;
        }

        @Override
        public Co insFactory(CoInsFactory insFactory) {
            return null;
        }

        @Override
        public CoInsFactory insFactory() {
            return null;
        }

        @Override
        public Co io(CoIO io) {
            return null;
        }

        @Override
        public CoIO io() {
            return null;
        }

        @Override
        public Co withListener(String name, CoListener l) {
            return null;
        }

        @Override
        public CoListener removeListener(String name) {
            return null;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Co) return ((Co) obj).id().equals(id);
            return false;
        }

    }

    @Override
    public Co find(String id) {
        return members.get(id);
    }

}
