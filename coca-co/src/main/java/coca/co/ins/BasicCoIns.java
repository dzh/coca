/**
 * 
 */
package coca.co.ins;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import coca.co.Co;
import coca.co.CoGroup;
import coca.co.ins.fmt.InsFormat;
import coca.co.util.IDUtil;

/**
 * @author dzh
 * @date Sep 2, 2017 5:01:00 PM
 * @since 0.0.1
 */
public class BasicCoIns<T> implements CoIns<T> {

    private Ins ins;

    private Co from;

    private List<Co> toCo;
    private CoGroup toGroup;
    private String codec;

    private T data;

    private String id;

    private long cntl = 0;

    private long ttl = TTL_NEVER_TIMEOUT;

    public BasicCoIns(Ins ins) {
        this(IDUtil.uuid());
        this.ins = ins;
    }

    public BasicCoIns(String id) {
        this.id = id;
    }

    @Override
    public Ins ins() {
        return this.ins;
    }

    @Override
    public Co from() {
        return from;
    }

    @Override
    public List<Co> toCo() {
        return toCo == null ? Collections.emptyList() : toCo;
    }

    @Override
    public CoGroup toGroup() {
        return toGroup;
    }

    @Override
    public CoIns<T> from(Co c) {
        from = c;
        return this;
    }

    @Override
    public CoIns<T> to(CoGroup group) {
        this.toGroup = group;
        return this;
    }

    @Override
    public CoIns<T> to(Co... co) {
        if (co != null) this.toCo = Arrays.asList(co);
        return this;
    }

    @Override
    public CoIns<T> codec(String codec) {
        this.codec = codec;
        return this;
    }

    @Override
    public String codec() {
        return this.codec;
    }

    @Override
    public T data() {
        return data;
    }

    @Override
    public CoIns<T> data(T data) {
        this.data = data;
        return this;
    }

    @Override
    public <F> F format(InsFormat<T, F> formatter) {
        return formatter.format(ins.format(), data());
    }

    @Override
    public CoIns<T> ins(Ins ins) {
        this.ins = ins;
        return this;
    }

    @Override
    public int hashCode() {
        if (id == null) throw new NullPointerException("CoIns id is nil. " + toString());
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof CoIns) return ((CoIns<?>) obj).id().equals(this.id);
        return false;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public CoIns<T> id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public long cntl() {
        return cntl;
    }

    @Override
    public CoIns<T> cntl(long cntl) {
        this.cntl = cntl;
        return this;
    }

    @Override
    public long ttl() {
        return ttl;
    }

    @Override
    public CoIns<T> ttl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    @Override
    public String toString() {
        return "id_" + id() + " cntl_" + cntl() + " ttl_" + ttl() + " " + ins + " data_" + data();
    }

}
