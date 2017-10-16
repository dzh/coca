/**
 * 
 */
package coca.co;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import coca.co.ins.CoInsFactory;
import coca.co.ins.actor.CoActor;
import coca.co.ins.codec.InsCodec;
import coca.co.ins.codec.TextInsCodec;
import coca.co.io.BasicIO;
import coca.co.io.ChannelSelector;
import coca.co.io.CoIO;
import coca.co.io.LocalChannelSelector;
import coca.co.util.IDUtil;

/**
 * @author dzh
 * @date Sep 19, 2017 3:11:15 PM
 * @since 0.0.1
 */
public class CoConf implements CoConst {

    private Map<String, String> conf = Collections.synchronizedMap(new HashMap<>());

    public CoConf init(Map<String, String> conf) {
        if (conf == null) conf = Collections.emptyMap();
        this.conf.putAll(conf);
        return this;
    }

    public String get(String key) {
        return conf.getOrDefault(key, System.getProperty(key));
    }

    public String get(String key, String defval) {
        return Optional.ofNullable(get(key)).orElse(defval);
    }

    public int getInt(String key, String defval) {
        return Integer.parseInt(get(key, defval));
    }

    public String put(String key, String val) {
        return conf.put(key, val);
    }

    public String remove(String key) {
        return conf.remove(key);
    }

    public void clear() {
        conf.clear();
    }

    public CoInsFactory newInsFactory() throws Exception {
        Class<?> clazz = getClass().getClassLoader().loadClass(conf.getOrDefault(P_CO_INS_FACTORY, CoInsFactory.class.getName()));
        return (CoInsFactory) clazz.newInstance();
    }

    public void withActors(CoIO io) throws Exception {
        String[] actors = conf
                .getOrDefault(P_CO_IO_ACTORS, "coca.co.ins.actor.JoinActor coca.co.ins.actor.QuitActor coca.co.ins.actor.HeartbeatActor")
                .split(" ");
        for (String actor : actors) {
            io.withActor(newActor(actor));
        }
    }

    public CoActor newActor(String actor) throws Exception {
        Class<?> clazz = getClass().getClassLoader().loadClass(actor);
        return (CoActor) clazz.newInstance();
    }

    public Co newCo() throws Exception {
        Class<?> clazz = getClass().getClassLoader().loadClass(conf.getOrDefault(P_CO, BasicCo.class.getName()));
        return (Co) clazz.getConstructor(String.class).newInstance(IDUtil.newCoID());
    }

    public CoIO newIO() throws Exception {
        Class<?> clazz = getClass().getClassLoader().loadClass(conf.getOrDefault(P_CO_IO, BasicIO.class.getName()));
        return (CoIO) clazz.newInstance();
    }

    public ChannelSelector newSelector() throws Exception {
        Class<?> clazz = getClass().getClassLoader().loadClass(conf.getOrDefault(P_CO_IO_SELECTOR, LocalChannelSelector.class.getName()));
        return (ChannelSelector) clazz.newInstance();
    }

    public void withCodecs(CoIO io) throws Exception {
        String[] codecs = conf.getOrDefault(P_CO_INS_CODECS, TextInsCodec.class.getName()).split(" ");
        for (String codec : codecs) {
            io.withCodec(newCodec(codec));
        }
    }

    public InsCodec newCodec(String codec) throws Exception {
        Class<?> clazz = getClass().getClassLoader().loadClass(codec);
        return (InsCodec) clazz.newInstance();
    }

}
