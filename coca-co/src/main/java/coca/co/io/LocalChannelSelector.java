/**
 * 
 */
package coca.co.io;

import coca.co.io.channel.GroupChannel;
import coca.co.io.channel.PacketFuture;
import coca.co.io.channel.PacketResult;

/**
 * For Test
 * 
 * @author dzh
 * @date Oct 13, 2017 11:23:50 AM
 * @since 0.0.1
 */
public class LocalChannelSelector extends GroupChannelSelector {

    /*
     * (non-Javadoc)
     * @see coca.co.io.GroupChannelSelector#newGroupChannel(java.lang.String)
     */
    @Override
    protected GroupChannel newGroupChannel(String name) {
        return new LoopGroupChannel(name);
    }

    class LoopGroupChannel extends GroupChannel {

        public LoopGroupChannel(String name) {
            super(name);
        }

        @Override
        protected void writeImpl(PacketFuture pf) throws Exception {
            if (receive(pf.send())) pf.result(new PacketResult(PacketResult.IOSt.SEND_SUCC));
            else pf.result(new PacketResult(PacketResult.IOSt.SEND_FAIL));
        }

    }

}
