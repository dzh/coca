/**
 * 
 */
package coca.ca.stack.policy;

import coca.ca.stack.CaStack;

/**
 * @author dzh
 * @date Nov 14, 2016 12:34:29 PM
 */
public interface CaPolicy extends CaRPolicy, CaWPolicy {

    void stack(CaStack s);

    CaStack stack();

}
