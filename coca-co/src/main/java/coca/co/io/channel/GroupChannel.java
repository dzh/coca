/**
 * 
 */
package coca.co.io.channel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import coca.co.io.ChannelSelector;
import coca.co.io.packet.InsPacket;
import coca.co.io.packet.PacketCodec;
import coca.co.io.packet.PacketCodec_v1;

/**
 * @author dzh
 * @date Sep 8, 2017 1:45:11 PM
 * @since 0.0.1
 */
public class GroupChannel implements CoChannel {

    private String name;

    private List<PacketCodec> codec;

    protected ChannelSelector selector;

    public GroupChannel(String name) {
        this.name = name;
        codec = new LinkedList<>();
    }

    @Override
    public String name() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.channels.Channel#isOpen()
     */
    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.channels.Channel#close()
     */
    @Override
    public void close() throws IOException {

    }

    @Override
    public ChannelFuture write(InsPacket packet) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InsPacket read(long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof GroupChannel) { return ((GroupChannel) obj).name().equals(name); }
        return false;
    }

    @Override
    public CoChannel init(ChannelSelector selector) {
        this.selector = selector;
        codec.add(new PacketCodec_v1());
        return this;
    }

    /**
     *
     * @param v
     * @return
     * @throws NullPointerException
     *             if the element selected is null
     */
    protected PacketCodec codec(int v) {
        return codec.stream().filter(c -> {
            return c.version() == v;
        }).findFirst().get();
    }

    @Override
    public String toString() {
        return this.name;
    }

}
