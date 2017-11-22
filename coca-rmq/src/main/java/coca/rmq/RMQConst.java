/**
 * 
 */
package coca.rmq;

/**
 * @author dzh
 * @date Oct 19, 2017 8:49:15 PM
 * @since 0.0.1
 */
public interface RMQConst {

    String P_CO_RMQ_NAMESRV = "co.rmq.namesrv";
    String P_CO_RMQ_TOPIC_KEY = "co.rmq.topic.key";
    String P_CO_RMQ_TOPIC_QUEUENUM = "co.rmq.topic.queuenum";

    // consume
    String P_CO_RMQ_C_MESSAGE_BATCH_MAXSIZE = "co.rmq.c.message.batch.maxsize";
    String P_CO_RMQ_C_TIMEOUT = "co.rmq.c.timeout";
    String P_CO_RMQ_C_THREAD_MAX = "co.rmq.c.thread.max";
    String P_CO_RMQ_C_THREAD_MIN = "co.rmq.c.thread.min";
    String P_CO_RMQ_C_MESSAGE_IGNORE_TIMEOUT = "co.rmq.c.message.ignore.timeout"; // second
    String P_CO_RMQ_C_PULL_THRESHOLD = "co.rmq.c.pull.threshold";
    String P_CO_RMQ_C_MAX_RECONSUME_TIMES = "co.rmq.c.max.reconsume.times";
    String P_CO_RMQ_C_MAX_SPAN = "co.rmq.c.max.span";
    String P_CO_RMQ_C_PERSIST_OFFSET_INTERVAL = "co.rmq.c.persist.offset.interval";

    // produce
    String P_CO_RMQ_P_RETRY_TIMES = "co.rmq.p.retry.times";

}
