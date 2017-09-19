/**
 * 
 */
package coca.co.redis.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import coca.co.Co;
import coca.co.io.ChannelSelector;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.CoChannelException;
import coca.co.io.channel.GroupChannel;
import coca.co.io.channel.PacketFuture;
import coca.co.io.channel.PacketResult;
import coca.co.io.packet.InsPacket;

/**
 * @author dzh
 * @date Sep 15, 2017 7:36:13 PM
 * @since 0.0.1
 */
public class RedisGroupChannel extends GroupChannel {

    static final Logger LOG = LoggerFactory.getLogger(RedisGroupChannel.class);

    private RedissonClient redisson;

    public static final String P_CO_REDIS_ADDR = "co.redis.addr";

    public RedisGroupChannel(String name) {
        super(name);
    }

    @Override
    public CoChannel init(ChannelSelector selector) throws CoChannelException {
        super.init(selector);
        try {
            Config config = newRedisConf(this);
            redisson = Redisson.create(config);

            startRedis(redisson);
        } catch (Exception e) {
            throw new CoChannelException("init channel:" + name() + ",error:" + e.getMessage(), e.getCause());
        }
        return this;
    }

    private String groupTopic() {
        return name();
    }

    private String selfTopic() {
        return String.valueOf(selector.io().co().hashCode());
    }

    protected void startRedis(RedissonClient redisson) {
        RTopic<byte[]> groupTopic = redisson.getTopic(groupTopic(), ByteArrayCodec.INSTANCE);
        groupTopic.addListener(new MessageListener<byte[]>() {
            @Override
            public void onMessage(String channel, byte[] message) {
                doRecvMessage(channel, message);
            }
        });

        RTopic<byte[]> coTopic = redisson.getTopic(selfTopic(), ByteArrayCodec.INSTANCE);
        coTopic.addListener(new MessageListener<byte[]>() {
            @Override
            public void onMessage(String channel, byte[] message) {
                doRecvMessage(channel, message);
            }
        });
    }

    protected void doRecvMessage(String channel, byte[] message) {
        ByteBuffer packet = ByteBuffer.wrap(message);
        packet.position(4);
        int v = packet.getShort();
        packet.rewind();
        InsPacket ins = codec(v).decode(packet);
        LOG.info("Receive from channel:{}, read packet {}", channel, ins);
        // TODO miss packet
        if (!rq.offer(ins)) LOG.info("discard {}", ins);
    }

    private String redisAddr() {
        return this.selector.io().co().conf().get(P_CO_REDIS_ADDR, "redis://127.0.0.1:6379");
    }

    protected Config newRedisConf(RedisGroupChannel ch) {
        Config config = new Config();
        // single
        config.useSingleServer().setAddress(redisAddr());

        // cluster
        // config.useClusterServers().setScanInterval(2000).addNodeAddress("127.0.0.1:7000", "127.0.0.1:7001");

        // sentinel
        // config.useSentinelServers().setMasterName("mymaster").addSentinelAddress("127.0.0.1:26389",
        // "127.0.0.1:26379");

        // master-slave
        // config.useMasterSlaveServers().setMasterAddress("127.0.0.1:6379").addSlaveAddress("127.0.0.1:6389",
        // "127.0.0.1:6332");
        return config;
    }

    @Override
    protected void writeImpl(PacketFuture pf) throws Exception {
        InsPacket packet = pf.send();
        packet.type(InsPacket.Type.GROUP.ordinal());
        ByteBuffer bytes = codec(packet.version()).encode(packet);

        String topic = packet.ins().toGroup().name();
        List<Co> toCo = packet.ins().toCo();
        if (toCo.isEmpty()) {
            RTopic<byte[]> groupTopic = redisson.getTopic(topic, ByteArrayCodec.INSTANCE);
            groupTopic.publish(bytes.array());
        } else {
            for (Co co : toCo) {
                RTopic<byte[]> coTopic = redisson.getTopic(String.valueOf(co.hashCode()), ByteArrayCodec.INSTANCE);
                coTopic.publish(bytes.array());
            }
        }
        pf.result(new PacketResult(PacketResult.IOSt.SEND_SUCC));
    }

    @Override
    public void close() throws IOException {
        super.close();
        // redisson.getTopic(groupTopic()).removeAllListeners();
        // redisson.getTopic(selfTopic()).removeAllListeners();
        if (redisson != null && !redisson.isShutdown()) redisson.shutdown(2, 15, TimeUnit.SECONDS);
    }

}
