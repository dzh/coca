coca-redis
===============================


## [配置定义](src/main/java/coca/redis/RedisConst.java)[=默认值]
- co.io.selector = coca.redis.co.io.RedisChannelSelector
- co.redis.addr = redis://127.0.0.1:6379
- co.redis.cluster.scan.interval = 2000
- co.redis.cluster.addr = redis://127.0.0.1:7000
- co.redis.sentinel.addr = redis://127.0.0.1:26389
- co.redis.sentinel.master.name = mymaster
- co.redis.master.addr = redis://127.0.0.1:6379
- co.redis.slave.addr = redis://127.0.0.1:6389

    // cluster

    // sentinel
    String P_CO_REDIS_SENTINEL_ADDR = "co.redis.sentinel.addr";
    String P_CO_REDIS_SENTINEL_MASTER_NAME = "co.redis.sentinel.master.name";
    // master-slave
    String P_CO_REDIS_MASTER_ADDR = "co.redis.master.addr";
    String P_CO_REDIS_SLAVE_ADDR = "co.redis.slave.addr";

