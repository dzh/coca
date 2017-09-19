/**
 * 
 */
package coca.co;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dzh
 * @date Sep 19, 2017 3:11:15 PM
 * @since 0.0.1
 */
public class CoConf {

    private Co co;

    private Map<String, String> conf = Collections.synchronizedMap(new HashMap<>());

    public CoConf init(Co co, Map<String, String> conf) {
        if (conf == null) conf = Collections.emptyMap();
        this.conf.putAll(conf);
        return this;
    }

    public String get(String key, String defval) {
        return conf.getOrDefault(key, defval);
    }

    public String put(String key, String val) {
        return conf.put(key, val);
    }

    public String remove(String key) {
        return conf.remove(key);
    }

    public Co co() {
        return this.co;
    }

    public void clear() {
        conf.clear();
    }

}
