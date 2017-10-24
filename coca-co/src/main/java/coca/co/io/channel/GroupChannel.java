/**
 * 
 */
package coca.co.io.channel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.ins.InsFuture;
import coca.co.io.ChannelSelector;
import coca.co.io.packet.InsPacket;
import coca.co.io.packet.InsPacketException;
import coca.co.io.packet.PacketCodec;
import coca.co.io.packet.PacketCodec_v1;

/**
 * @author dzh
 * @date Sep 8, 2017 1:45:11 PM
 * @since 0.0.1
 */
public abstract class GroupChannel implements CoChannel {

    static final Logger LOG = LoggerFactory.getLogger(GroupChannel.class);

    private String name;

    private List<PacketCodec> codec;

    protected ChannelSelector selector;

    private volatile boolean open = true;

    protected BlockingQueue<PacketFuture> wq;

    protected BlockingQueue<InsPacket> rq;
    // TODO thread pool?
    private WriterThread wt;

    public GroupChannel(String name) {
        this.name = name;
        codec = new LinkedList<>();
    }

    @Override
    public CoChannel init(ChannelSelector selector) throws CoChannelException {
        this.selector = selector;
        codec.add(new PacketCodec_v1());

        wq = createWriteQueue();
        rq = createReadQueue();

        wt = new WriterThread("WriterThread-" + name);
        wt.start();

        open = true;
        LOG.info("{} init", getClass().getName());
        return this;
    }

    // TODO config
    protected BlockingQueue<PacketFuture> createWriteQueue() {
        return new LinkedBlockingQueue<PacketFuture>(10000);
    }

    // TODO config
    protected BlockingQueue<InsPacket> createReadQueue() {
        return new LinkedBlockingQueue<InsPacket>(10000);
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
        return open;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.channels.Channel#close()
     */
    @Override
    public void close() throws IOException {
        open = false;
        if (!wq.isEmpty()) try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        wt.interrupt();
        selector = null;
    }

    @Override
    public PacketFuture write(InsPacket packet) throws CoChannelException {
        if (!isOpen()) throw new CoChannelException(name() + " closed");
        if (packet == null) throw new InsPacketException("packet is nil");
        if (!isValidPacket(packet)) throw new InsPacketException("packet is invalid");

        PacketFuture pf = new PacketFuture(packet);
        pf.next(new InsFuture(packet.ins())); // TODO
        try {
            if (wq.offer(pf, 2, TimeUnit.SECONDS)) {
                // TODO ack
            } else {
                pf.cancel(true);
            }
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
            pf.cancel(true);
        }
        return pf;
    }

    class WriterThread extends Thread {

        public WriterThread(String name) {
            super(name);
        }

        public void run() {
            LOG.info("{} start!", getName());
            for (;;) {
                if (!GroupChannel.this.isOpen() && wq.isEmpty()) break;
                PacketFuture pf = null;
                try {
                    pf = wq.take();
                    writeImpl(pf);
                } catch (InterruptedException e) {
                    LOG.warn("WriterThread {} interrupted", getName());
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    if (pf != null) pf.result(new PacketResult(PacketResult.IOSt.SEND_FAIL));
                }
            }
            LOG.info("{} exit!", getName());
        }
    }

    protected abstract void writeImpl(PacketFuture pf) throws Exception;

    protected boolean receive(InsPacket ins) {
        return rq.offer(ins);
    }

    protected boolean isValidPacket(InsPacket packet) {
        // TODO
        return true;
    }

    @Override
    public InsPacket read(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            return rq.poll(timeout, unit);
        } catch (InterruptedException e) {
            throw e;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof GroupChannel) { return ((GroupChannel) obj).name().equals(name); }
        return false;
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
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
