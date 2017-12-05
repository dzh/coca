/**
 * 
 */
package coca.jgroups.co.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.ins.CoIns.Ins;
import coca.co.ins.InsConst;
import coca.co.io.ChannelSelector;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.CoChannelException;
import coca.co.io.channel.GroupChannel;
import coca.co.io.channel.PacketFuture;
import coca.co.io.channel.PacketResult;
import coca.co.io.packet.InsPacket;
import coca.jgroups.JGroupsConst;

/**
 * @author dzh
 * @date Dec 5, 2017 4:33:33 PM
 * @since 1.0.0
 */
public class JGroupsGroupChannel extends GroupChannel implements JGroupsConst, Receiver {

    static final Logger LOG = LoggerFactory.getLogger(JGroupsGroupChannel.class);

    private JChannel jch;

    public JGroupsGroupChannel(String name) {
        super(name);
    }

    @Override
    public CoChannel init(ChannelSelector selector) throws CoChannelException {
        super.init(selector);
        try {
            jch = new JChannel(); // TODO config
            jch.setReceiver(this);
            jch.connect(name());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            try {
                close();
            } catch (IOException e1) {}
        }
        return this;
    }

    /*
     * (non-Javadoc)
     * @see coca.co.io.channel.GroupChannel#writeImpl(coca.co.io.channel.PacketFuture)
     */
    @Override
    protected void writeImpl(PacketFuture pf) throws Exception {
        InsPacket packet = pf.send();
        LOG.info("write packet-{}", packet);

        packet.type(InsPacket.Type.GROUP.ordinal());
        ByteBuffer bytes = codec(packet.version()).encode(packet);
        List<Co> toCo = packet.ins().toCo();
        if (toCo.isEmpty()) {
            send(new Message(null, bytes.array()), pf);
        } else {
            for (Co co : toCo) { // TODO optimize
                send(new Message(clusterAddrs.get(co.id()), bytes.array()), pf);
            }
        }
    }

    private void send(Message msg, PacketFuture pf) throws Exception {
        jch.send(msg);
        pf.result(new PacketResult(PacketResult.IOSt.SEND_SUCC));
    }

    @Override
    public void close() throws IOException {
        super.close();
        try {
            if (jch != null && !jch.isClosed()) jch.close();
        } catch (Exception e) {}
        clusterAddrs.clear();
        LOG.info("{} close!", name());
    }

    @Override
    public void receive(Message msg) {
        LOG.info("recv jgroups msg-{}", msg);

        ByteBuffer packet = ByteBuffer.wrap(msg.buffer());
        packet.position(4);
        int v = packet.getShort();
        packet.rewind();
        InsPacket ins = this.codec(v).decode(packet);
        LOG.info("read packet-{}", ins);

        clusterAddrs(msg, ins); // TODO

        if (!receive(ins)) {
            LOG.error("recv msg-{}", ins);
        }

    }

    // @Override
    // public void viewAccepted(View view) {
    // LOG.info("view {} {}", view.getViewId(), view.getMembers().get(0));
    // }

    @Override
    public void suspect(Address suspected_mbr) {
        String id = null;
        for (Entry<String, Address> e : clusterAddrs.entrySet()) {
            if (suspected_mbr.equals(e.getValue())) {
                id = e.getKey();
                break;
            }
        }
        if (id != null) clusterAddrs.remove(id);
    }

    private ConcurrentMap<String, Address> clusterAddrs = new ConcurrentHashMap<>();

    void clusterAddrs(Message msg, InsPacket insPacket) {
        Ins ins = insPacket.ins().ins();
        if (ins.equals(InsConst.JOIN)) {
            clusterAddrs.put(insPacket.ins().from().id(), msg.src());
        } else if (ins.equals(InsConst.QUIT)) {
            clusterAddrs.remove(insPacket.ins().from().id());
        }
    }

}
