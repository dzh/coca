coco-rmq
======================



## [配置定义](src/main/java/coca/co/CoConst.java)[=默认值]
- co.io.selector = coca.rmq.co.io.RMQChannelSelector
- co.rmq.namesrv = 127.0.0.1:9876
- co.rmq.topic.queuenum = 8
- co.rmq.topic.key = DefaultCluster
- co.rmq.p.retry.times = 3
- co.rmq.c.message.batch.maxsize = 100
- co.rmq.c.thread.max = 64
- co.rmq.c.thread.min = 20
- co.rmq.c.message.ignore.timeout = 30 //second
- co.rmq.c.pull.threshold = 10000
- co.rmq.c.timeout = 5 //minute
- co.rmq.c.max.reconsume.times = 3 
- co.rmq.c.max.span = 10000


