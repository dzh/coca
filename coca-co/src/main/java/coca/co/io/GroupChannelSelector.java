/**
 * 
 */
package coca.co.io;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.BasicGroup;
import coca.co.CoException;
import coca.co.ins.CoIns;
import coca.co.ins.actor.CoActor;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.CoChannelException;
import coca.co.io.channel.GroupChannel;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 10, 2017 3:26:47 PM
 * @since 0.0.1
 */
public abstract class GroupChannelSelector extends BasicChannelSelector {

    static final Logger LOG = LoggerFactory.getLogger(GroupChannelSelector.class);

    Map<String, CoChannel> channels; // <group name,channel>

    private volatile boolean closed = false;

    private BlockingQueue<InsPacket> _queue;

    public static final String P_CO_IO_SEL_QUEUE_SIZE = "co.io.sel.queue.size";

    @Override
    public ChannelSelector init(CoIO io) {
        super.init(io);
        channels = new ConcurrentHashMap<String, CoChannel>();
        _queue = new LinkedBlockingQueue<InsPacket>(queueSize());
        LOG.info("{} init", getClass().getName());
        return this;
    }

    private int queueSize() {
        return io.co().conf().getInt(P_CO_IO_SEL_QUEUE_SIZE, "10000");
    }

    /**
     * @throws
     */
    @Override
    public CoChannel select(CoIns<?> ins) throws CoException {
        if (closed) throw new CoException("selector closed! " + ins);
        if (isInvalidIns(ins)) throw new CoException("Invalid " + ins);

        String name = ins.toGroup().name();
        CoChannel ch = channels.get(name);
        if (ch == null) {
            ch = newCh(ins);
        }
        if (ch != null && !ch.isOpen()) throw new CoException(name + " channel closed!");
        return ch;
    }

    protected boolean isInvalidIns(CoIns<?> ins) {
        if (ins.toGroup() == null) return true;
        return false;
    }

    @Override
    public CoChannel newCh(CoIns<?> ins) throws CoChannelException {
        String name = ins.toGroup().name();
        if (closed) return null;
        GroupChannel ch = newGroupChannel(name);
        if (channels.putIfAbsent(name, ch) == null) {
            listen(ch.init(this));
        }
        return channels.get(name);
    }

    abstract protected GroupChannel newGroupChannel(String name);

    protected void listen(CoChannel ch) {
        if (ch == null || !ch.isOpen()) return;
        // TODO acquire a thread from pool
        ChannelThread t = new ChannelThread(ch);
        t.start();
        channelThreads.put(ch, t);
    }

    // used for close
    private Map<CoChannel, ChannelThread> channelThreads = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        channels.forEach((name, ch) -> {
            try {
                ch.close();
                channelThreads.get(ch).interrupt();
                channelThreads.get(ch).awaitExit(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
        channels.clear();
        channelThreads.clear();
    }

    @Override
    public InsPacket poll(long timeout, TimeUnit unit) {
        try {
            return _queue.poll(timeout, unit);
        } catch (InterruptedException e) {
            // LOG.warn("interrupted");
        }
        return null;
    }

    // TODO All channels read InsPack by single selector thread
    class ChannelThread extends Thread {

        CoChannel ch;
        private CountDownLatch closeLatch = new CountDownLatch(1);

        public ChannelThread(CoChannel ch) {
            super("ChannelThread-" + ch.name());
            this.ch = ch;
        }

        public void run() {
            READ_PACKET:
            for (;;) {
                try {
                    InsPacket packet = ch.read(30, TimeUnit.SECONDS);// TODO
                    if (packet == null) {
                        if (!ch.isOpen()) {
                            break;
                        }
                        continue;
                    }
                    // set Group
                    if (ch instanceof GroupChannel) packet.ins().to(new BasicGroup(ch.name()));
                    // actor
                    for (CoActor actor : io().actors()) {
                        if (actor.accept(packet.ins())) {
                            actor.submit(packet.ins());
                            continue READ_PACKET;
                        }
                    }

                    // TODO limit QPS
                    if (!_queue.offer(packet, 2, TimeUnit.SECONDS)) {
                        LOG.error("discard {}", packet.toString());// TODO save
                    }
                } catch (InterruptedException e) {
                    LOG.warn("{} Interrupted", getName());
                    break;
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            LOG.info("{} exit!", getName());
            closeLatch.countDown();
        }

        public boolean awaitExit(long timeout, TimeUnit unit) {
            try {
                return closeLatch.await(timeout, unit);
            } catch (InterruptedException e) {}
            return false;
        }
    }

    @Override
    public String toString() {
        return "GroupChannelSelector";
    }
}
