/**
 * 
 */
package coca.api.handler;

import java.util.Map;

import coca.api.CocaConst;
import coca.api.co.StackCoIns;
import coca.ca.Ca;
import coca.ca.CaValue;
import coca.ca.stack.CaStack;

/**
 * @author dzh
 * @date Oct 13, 2017 4:27:09 PM
 * @since 0.0.1
 */
public class EvictHandler extends InsHandler<String> {

    public EvictHandler(StackCoIns ins) {
        super(ins);
    }

    @Override
    public void run() {
        StackCoIns ins = (StackCoIns) ins();
        try {
            Map<String, String> data = ins.format(MapInsFormat);
            CaStack<String, Object> stack = coca().stack(data.get(CocaConst.F_stack));
            Ca<String, Object> ca = stack.ca(data.get(CocaConst.F_ca));
            CaValue<String, Object> val = CaValue.newVal(data.get(CocaConst.F_key), null);
            ca.write(val);

            LOG.info("{} {} evict {}", coca(), ca, ins);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
