/**
 * 
 */
package coca.redis.co.io;

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
import coca.co.CoConf;
import coca.co.io.ChannelSelector;
import coca.co.io.channel.CoChannel;
import coca.co.io.channel.CoChannelException;
import coca.co.io.channel.GroupChannel;
import coca.co.io.channel.PacketFuture;
import coca.co.io.channel.PacketResult;
import coca.co.io.packet.InsPacket;
import coca.redis.RedisConst;

/**
 * @author dzh
 * @date Sep 15, 2017 7:36:13 PM
 * @since 0.0.1
 */
public class RedisGroupChannel extends GroupChannel implements RedisConst {

    static final Logger LOG = LoggerFactory.getLogger(RedisGroupChannel.class);

    private RedissonClient redisson;

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
        LOG.info("groupTopic-{}", name());
        return name();
    }

    private String selfTopic() {
        LOG.info("selfTopic-{}", selector.io().co().hashCode());
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
        ByteBuffer bytes = ByteBuffer.wrap(message);
        bytes.position(4);
        int v = bytes.getShort();
        bytes.rewind();
        InsPacket packet = codec(v).decode(bytes);

        LOG.info("Recv from channel:{}, read packet {} {}", channel, packet, packet.ins());
        // TODO miss packet
        if (!receive(packet)) LOG.info("discard {}", packet);
    }

    private boolean isSingle() {
        return this.selector.io().co().conf().get(P_CO_REDIS_ADDR) != null;
    }

    private boolean isCluster() {
        return this.selector.io().co().conf().get(P_CO_REDIS_CLUSTER_ADDR) != null;
    }

    private boolean isSentinel() {
        return this.selector.io().co().conf().get(P_CO_REDIS_SENTINEL_ADDR) != null;
    }

    private boolean isMasterSlave() {
        return this.selector.io().co().conf().get(P_CO_REDIS_MASTER_ADDR) != null;
    }

    protected Config newRedisConf(RedisGroupChannel ch) {
        CoConf coConf = selector.io().co().conf();
        Config config = new Config();
        if (isSingle()) {
            config.useSingleServer().setAddress(coConf.get(P_CO_REDIS_ADDR, "redis://127.0.0.1:6379"));
        }
        // cluster
        else if (isCluster()) {
            String[] addrList = coConf.get(P_CO_REDIS_CLUSTER_ADDR, "redis://127.0.0.1:7000").split("\\s+");
            config.useClusterServers().setScanInterval(selector.io().co().conf().getInt(P_CO_REDIS_CLUSTER_SCAN_INTERVAL, "2000"))
                    .addNodeAddress(addrList);
        }
        // sentinel
        else if (isSentinel()) {
            String masterName = coConf.get(P_CO_REDIS_SENTINEL_MASTER_NAME, "mymaster");
            String[] addrList = coConf.get(P_CO_REDIS_SENTINEL_ADDR, "redis://127.0.0.1:26389").split("\\s+");
            config.useSentinelServers().setMasterName(masterName).addSentinelAddress(addrList);
        }
        // master-slave
        else if (isMasterSlave()) {
            String addrMaster = coConf.get(P_CO_REDIS_MASTER_ADDR, "redis://127.0.0.1:6379");
            String[] addrSlave = coConf.get(P_CO_REDIS_SLAVE_ADDR, "redis://127.0.0.1:6389").split("\\s+");
            config.useMasterSlaveServers().setMasterAddress(addrMaster).addSlaveAddress(addrSlave);
        } else {
            config.useSingleServer().setAddress(coConf.get(P_CO_REDIS_ADDR, "redis://127.0.0.1:6379"));
        }
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
        if (redisson != null) redisson.shutdown(2, 15, TimeUnit.SECONDS);
    }

}
