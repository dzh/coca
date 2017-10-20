/**
 * 
 */
package coca.redis;

/**
 * @author dzh
 * @date Oct 20, 2017 3:07:06 PM
 * @since 0.0.1
 */
public interface RedisConst {

    String P_CO_REDIS_ADDR = "co.redis.addr";

    // cluster
    String P_CO_REDIS_CLUSTER_SCAN_INTERVAL = "co.redis.cluster.scan.interval";
    String P_CO_REDIS_CLUSTER_ADDR = "co.redis.cluster.addr";

    // sentinel
    String P_CO_REDIS_SENTINEL_ADDR = "co.redis.sentinel.addr";
    String P_CO_REDIS_SENTINEL_MASTER_NAME = "co.redis.sentinel.master.name";
    // master-slave
    String P_CO_REDIS_MASTER_ADDR = "co.redis.master.addr";
    String P_CO_REDIS_SLAVE_ADDR = "co.redis.slave.addr";
}
