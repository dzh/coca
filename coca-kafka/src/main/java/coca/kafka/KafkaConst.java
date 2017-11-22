/**
 * 
 */
package coca.kafka;

/**
 * @author dzh
 * @date Nov 8, 2017 4:18:15 PM
 * @since 1.0.0
 */
public interface KafkaConst {

    // commons
    String P_CO_KAFKA_BOOTSTRAP_SERVERS = "co.kafka.bootstrap.servers"; // localhost:9092,localhost2:9092
    String P_CO_KAFKA_SSL_KEY_PASSWORD = "co.kafka.ssl.key.password";
    String P_CO_KAFKA_SSL_KEYSTORE_LOCATION = "co.kafka.ssl.keystore.location";
    String P_CO_KAFKA_SSL_KEYSTORE_PASSWORD = "co.kafka.ssl.keystore.password";
    String P_CO_KAFKA_SSL_TRUSTSTORE_LOCATION = "co.kafka.ssl.truststore.location";
    String P_CO_KAFKA_SSL_TRUSTSTORE_PASSWORD = "co.kafka.ssl.truststore.password";

    // consumer http://kafka.apache.org/documentation.html#newconsumerconfigs
    String P_CO_KAFKA_C_KEY_DESERIALIZER = "co.kafka.c.key.deserializer";
    String P_CO_KAFKA_C_VALUE_DESERIALIZER = "co.kafka.c.value.deserializer";
    String P_CO_KAFKA_C_FETCH_MIN_BYTES = "co.kafka.c.fetch.min.bytes";
    String P_CO_KAFKA_C_GROUP_ID = "co.kafka.c.group.id";
    String P_CO_KAFKA_C_HEARTBEAT_INTERVAL_MS = "co.kafka.c.heartbeat.interval.ms";
    String P_CO_KAFKA_C_MAX_PARTITION_FETCH_BYTES = "co.kafka.c.max.partition.fetch.bytes";
    String P_CO_KAFKA_C_SESSION_TIMEOUT_MS = "co.kafka.c.session.timeout.ms";

    String P_CO_KAFKA_C_AUTO_OFFSET_RESET = "co.kafka.c.auto.offset.reset"; // [latest, earliest, none]
    String P_CO_KAFKA_C_ENABLE_AUTO_COMMIT = "co.kafka.c.enable.auto.commit"; // true
    String P_CO_KAFKA_C_AUTO_COMMIT_INTERVAL_MS = "co.kafka.c.auto.commit.interval.ms";
    String P_CO_KAFKA_C_ISOLATION_LEVEL = "co.kafka.c.isolation.level"; // [read_committed, read_uncommitted]
    String P_CO_KAFKA_C_MAX_POLL_RECORDS = "co.kafka.c.max.poll.records";

    String P_CO_KAFKA_C_MESSAGE_IGNORE_TIMEOUT = "co.kafka.c.message.ignore.timeout"; // second
    // producer http://kafka.apache.org/documentation.html#producerconfigs
    String P_CO_KAFKA_P_CLIENT_ID = "co.kafka.p.client.id";
    // String P_CO_KAFKA_P_PRODUCER_TYPE = "co.kafka.p.producer.type"; // sync or async
    String P_CO_KAFKA_P_ACKS = "co.kafka.p.acks"; // all
    String P_CO_KAFKA_P_RETRIES = "co.kafka.p.retries";
    String P_CO_KAFKA_P_LINGER_MS = "co.kafka.p.linger.ms";
    String P_CO_KAFKA_P_KEY_SERIALIZER = "co.kafka.p.key.serializer";
    String P_CO_KAFKA_P_VALUE_SERIALIZER = "co.kafka.p.value.serializer";
    String P_CO_KAFKA_P_BATCH_SIZE = "co.kafka.p.batch.size";
    String P_CO_KAFKA_P_BUFFER_MEMORY = "co.kafka.p.buffer.memory";
    String P_CO_KAFKA_P_COMPRESSION_TYPE = "co.kafka.p.compression.type";

    String P_CO_KAFKA_P_CONNECTIONS_MAX_IDLE_MS = "co.kafka.p.connections.max.idle.ms";

}
