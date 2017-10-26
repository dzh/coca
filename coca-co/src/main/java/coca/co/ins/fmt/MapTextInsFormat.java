/**
 * 
 */
package coca.co.ins.fmt;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * TextIns formatted to Map
 * 
 * @author dzh
 * @date Sep 6, 2017 2:27:57 PM
 * @since 0.0.1
 */
public class MapTextInsFormat extends TextInsFormat<Map<String, String>> {

    @Override
    public Map<String, String> format(String fmt, String data) {
        String[] fmtList = split(fmt);
        String[] dataList = split(data);
        // TODO
        Map<String, String> r = new HashMap<String, String>(fmtList.length, 1);
        for (int i = 0; i < fmtList.length; i++) {
            if (i < dataList.length) r.put(fmtList[i], dataList[i]);
            else r.put(fmtList[i], "");
        }
        return r;
    }

}
