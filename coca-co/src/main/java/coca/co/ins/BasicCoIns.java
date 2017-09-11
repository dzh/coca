/**
 * 
 */
package coca.co.ins;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import coca.co.Co;
import coca.co.CoGroup;
import coca.co.ins.fmt.InsFormat;

/**
 * @author dzh
 * @date Sep 2, 2017 5:01:00 PM
 * @since 0.0.1
 */
public class BasicCoIns<T> implements CoIns<T> {

    private String id;

    private Ins ins;

    private Co from;

    private List<Co> toCo;
    private CoGroup toGroup;
    private String codec;

    private T data;

    public BasicCoIns(Ins ins) {
        this();
        this.ins = ins;
    }

    public BasicCoIns() {
        this.id = genId();
    }

    private static final String genId() { // TODO
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
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
    public String id() {
        return id;
    }

    @Override
    public CoIns<T> ins(Ins ins) {
        this.ins = ins;
        return this;
    }

}
