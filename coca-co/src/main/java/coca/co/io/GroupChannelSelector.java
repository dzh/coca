/**
 * 
 */
package coca.co.io;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.CoGroup;
import coca.co.ins.CoIns;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.GroupChannel;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 10, 2017 3:26:47 PM
 * @since 0.0.1
 */
public class GroupChannelSelector extends BasicChannelSelector {

    static final Logger LOG = LoggerFactory.getLogger(GroupChannelSelector.class);

    Map<String, CoChannel> channels;

    private volatile boolean closed = false;

    private BlockingQueue<InsPacket> _queue;

    protected CoIO io;

    @Override
    public ChannelSelector init(CoIO io) {
        this.io = io;
        channels = new ConcurrentHashMap<String, CoChannel>();
        _queue = new LinkedBlockingQueue<InsPacket>(10000);// TODO
        return this;
    }

    /**
     * @throws
     */
    @Override
    public CoChannel select(CoIns<?> ins) {
        CoGroup g = ins.toGroup();
        if (g == null) return null;
        String name = g.name();
        CoChannel ch = channels.get(name);
        if (ch == null) {
            ch = newChannel(ins);
        }
        return ch;
    }

    public CoChannel newChannel(CoIns<?> ins) {
        String name = ins.toGroup().name();
        if (closed) return null;
        GroupChannel ch = new GroupChannel(name);
        if (channels.putIfAbsent(name, ch) == null) {
            listen(ch.init(this));
        }
        return channels.get(name);
    }

    protected void listen(CoChannel ch) {
        if (ch == null) return;
        // TODO acquire a thread from pool
        ChannelThread t = new ChannelThread(ch);
        t.start();
    }

    @Override
    public void close() throws IOException {
        closed = true;
        channels.forEach((name, ch) -> {
            try {
                ch.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        });
        channels.clear();
    }

    @Override
    public InsPacket poll(long timeout, TimeUnit unit) {
        try {
            return _queue.poll(timeout, unit);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    class ChannelThread extends Thread {

        CoChannel ch;

        public ChannelThread(CoChannel ch) {
            super("ChannelThread-" + ch.name());
            this.ch = ch;
        }

        public void run() {
            for (;;) {
                try {
                    InsPacket packet = ch.read(10, TimeUnit.SECONDS);
                    if (packet == null) {
                        if (!ch.isOpen()) {
                            break;
                        }
                    }
                    // TODO limit QPS
                    if (!_queue.offer(packet, 2, TimeUnit.SECONDS)) {
                        LOG.error("discard {}", packet.toString());// TODO save
                    }
                } catch (InterruptedException e) {
                    LOG.error(e.getMessage(), e);
                    break;
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            LOG.info("{} exit!{} closed.", getName(), ch.name());
        }
    }

    @Override
    public CoIO io() {
        return io;
    }
}
