coca = co + ca
===================================
Co-Cache 协同缓存框架

## 核心概述
- coca-ca 实现多级缓存调度策略,使用方式如下:
    - 继承Ca,实现不同的缓存操作，可参考CaGuava、CaRedis. Ca实例放入CaStack中实现调度
    - CaStack和CaPolicy实现了对多级缓存的读写操作控制, 具体参考下文示例
    - 一个Ca可放入不同的CaStack实现不同的读写处理
    - 注意,Ca不用时记得释放资源Ca.close, 一般的关闭次序为CaStack.close -> Ca.close
- coca-co 实现多机Co实例之间的组消息同步,使用时主要的配置如下:
    - CoIns定义消息的格式, CoIns = Ins(指令定义,说明含义) + data(数据内容)
    - 继承CoInsFactory实现自定义CoIns创建工厂
    - 继承GroupChannelSelector实现自定义的组通道创建,可参考RedisChannelSelector、RMQChannelSelector
    - 通过conf配置自定义实现,详见BasicCo.newCo(Map<String,String> conf)
    - 通过Co.pub发送指令、Co.sub接收指令，可看考Coca里的实现方式
- coca-api 组合ca和co功能
    - 可以看作是co和ca如何使用的参考实现,可用于多级缓存同步更新、配置信息修改实时通知等场景
    - 如多个jvm堆缓存(1级)+redis(2级)场景,常见需求是任一jvm堆更新后同步修改其他jvm和redis
- 其他工程包含ca或co的具体实现

## 快速开始
- 加pom依赖 参考[coca-demo](coca-demo/pom.xml)

```xml
        <dependency>                                <!-- 必要依赖 -->
            <groupId>${project.groupId}</groupId>   <!-- io.github.dzh -->
            <artifactId>coca-api</artifactId>
            <version>${project.version}</version>   <!-- 1.0.0-SNAPSHOT -->
        </dependency>
        <dependency>                                <!-- 可选依赖 redis实现-->
            <groupId>${project.groupId}</groupId>
            <artifactId>coca-redis</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>                                <!-- 可选依赖 rocketmq实现-->
            <groupId>${project.groupId}</groupId>
            <artifactId>coca-rmq</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>                                <!-- 可选依赖 guava-cache实现-->
            <groupId>${project.groupId}</groupId>
            <artifactId>coca-guava</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>                                <!-- 可选依赖 ehcache实现-->
            <groupId>${project.groupId}</groupId>
            <artifactId>coca-ehcache</artifactId>
            <version>${project.version}</version>
        </dependency>
```

- Coca使用

```java
// 配置及初始化
Map<String, String> conf = new HashMap<>();
// 更多配置参考对应工程下的README.md, 如coca-redis/README.md. 可选其他的组播实现如`RMQChannelSelector`等
conf.put(CocaConst.P_CO_IO_SELECTOR, RedisChannelSelector.class.getName()); 
Coca coca = new Coca();
coca.init(conf);

// 配置多级缓存栈
Ca<String, Integer> guava = new CaGuava<>("CocaSample-Guava"); // 1级缓存guava-cache
Ca<String, Integer> redis = new CaRedis<>("CocaSample-Redis"); // 2级缓存redis
String stackName = "guava-redis"; // 栈名作为同步组的名称, 类似redis里的topic
coca.withStack(stackName, Arrays.asList(guava, redis), new CocaListener()); // 定义多级缓存调用栈

// 写缓存
String key = "cacheKey";
Integer value = 1;
boolean sync = true; // true-在1级缓存修改时产生组播通知，如guava-cache改动了 false-修改无需组播通知
long ttl = 3000; // 缓存过期时间3s, 这里可能只对redis有效, 注:一些Ca的实现再配置时决定,如CaGuava
CaValue<String, Integer> val = CaValue.newVal(key, value).sync(sync).ttl(ttl); // 创建缓存值key-value
// 调用栈写入缓存, 如这里的次序是`redis -> guava`, 同时产生栈事件由`CocaListener`处理, 若sync=true发送`CocaConst.EVICT`指令到stackName组
coca.<Integer> stack(stackName).write(val);

// 读缓存
// 调用栈读取缓存, 如这里的次序是`guava -> redis`, 默认读策略: guava缓存不存在时再读redis, 回写redis的缓存到guava
CaValue<String, Integer> val = coca.<Integer> stack(stackName).read(key);
```

- 示例说明
    - 通过2个coca演示本地缓存地同步改动
    - 1级缓存通过guava-cache实现, 需要coca-guava工程
    - 2级缓存由redis实现, 需要coca-redis工程
    - 缓存同步通过redis的pub/sub实现, 需要coca-redis工程, 需预先启动redis-server
    
- 示例源码 coca-demo/coca.demo.sample/
    - SampleMain启动2个coca实例,分别起名为`app1、app2`, 他们的同步组(stackName)名为`syncGroup`
    - 缓存key分类
        - coca.app1.*   app1的私有缓存，不需要同步
        - coca.app2.*   app2的私有缓存，不需要同步
        - coca.share.*  app1和app2的共享缓存，需要同步
    - 缓存读策略(默认): 1级缓存 if(nil)-> 2级缓存
        - 支持回写, 2级缓存读到的消息写到1级缓存
    - 缓存写策略(默认): 2级缓存 -> 1级缓存 -> 消息同步

- 在IDE中启动[SampleMain](coca-demo/src/main/java/coca/demo/sample/SampleMain.java),执行testShareWrite, (和代码一起看)打印日志分析如下:

```log
2017-10-16 18:19:34.918 [INFO ]  [main] Coca - app1-sub start           
2017-10-16 18:19:34.920 [INFO ]  [main] Coca - app1 init                //启动app1
2017-10-16 18:19:35.701 [INFO ]  [main] Coca - app1 newStack syncGroup  //app1创建`syncGroup`的缓存栈(栈名为同步组的名称)
2017-10-16 18:19:35.702 [INFO ]  [main] Coca - app2-sub start
2017-10-16 18:19:35.702 [INFO ]  [main] Coca - app2 init                //启动app1
2017-10-16 18:19:35.738 [INFO ]  [main] Coca - app2 newStack syncGroup  //app2创建`syncGroup`的缓存栈
//app1删除缓存coca.share.6,发送evict指令到`syncGroup`,这里的目的是在测试前情况缓存
2017-10-16 18:19:35.756 [INFO ]  [main] CocaSample - writeKey app1 coca.share.6, coca.share.6:null
//app1忽略自己的evict指令
2017-10-16 18:19:35.762 [INFO ]  [app1-sub] Coca - app1 ignore self-ins id_664266eeb33840f5ba5f2b08d17750b8 cntl_0 ttl_0  Ins[1025_evict_stack ca key] data_syncGroup CocaSample-Guava coca.share.6
//app2收到evict指令,删除缓存coca.share.6
2017-10-16 18:19:35.767 [INFO ]  [pool-10-thread-1] InsHandler - app2 CocaSample-Guava evict id_664266eeb33840f5ba5f2b08d17750b8 cntl_0 ttl_0 Ins[1025_evict_stack ca key] data_syncGroup CocaSample-Guava coca.share.6 
2017-10-16 18:19:36.759 [INFO ]  [main] CocaSample - readKey app1 coca.share.6, coca.share.6:null   //app1读不到数据
2017-10-16 18:19:36.760 [INFO ]  [main] CocaSample - readKey app2 coca.share.6, coca.share.6:null   //app2读不到数据 
//app2写缓存coca.share.6=746,发送evict指令到`syncGroup`(redis和app2的guava现在都有值,redis缓存超时为3s,guava-cache的缓存超时为10s)
2017-10-16 18:19:36.773 [INFO ]  [main] CocaSample - writeKey app2 coca.share.6, coca.share.6:746
//app1读到app2刚写的值(从redis获取,并回写到guava)
2017-10-16 18:19:36.777 [INFO ]  [main] CocaSample - readKey app1 coca.share.6, coca.share.6:746
2017-10-16 18:19:36.778 [INFO ]  [main] CocaSample - readKey app2 coca.share.6, coca.share.6:746
//app2忽略自己的evict指令
2017-10-16 18:19:36.780 [INFO ]  [app2-sub] Coca - app2 ignore self-ins id_ab66c024ddae4af4b72edcc14cc5d35d cntl_0 ttl_0 Ins[1025_evict_stack ca key] data_syncGroup CocaSample-Guava coca.share.6
//app1收到evict指令,删除缓存coca.share.6
2017-10-16 18:19:36.785 [INFO ]  [pool-2-thread-1] InsHandler - app1 CocaSample-Guava evict id_ab66c024ddae4af4b72edcc14cc5d35d cntl_0 ttl_0 Ins[1025_evict_stack ca key] data_syncGroup CocaSample-Guava coca.share.6
//线程Sleep(5s)后,redis缓存时效, app1读不到数据
2017-10-16 18:19:41.780 [INFO ]  [main] CocaSample - readKey app1 coca.share.6, coca.share.6:null
//app2可以读到缓存数据(从本地guava获取)
2017-10-16 18:19:41.781 [INFO ]  [main] CocaSample - readKey app2 coca.share.6, coca.share.6:746
//线程又Sleep(6s)后,guava缓存时效,app2最后也读不到数据
2017-10-16 18:19:47.783 [INFO ]  [main] CocaSample - readKey app2 coca.share.6, coca.share.6:null
2017-10-16 18:19:50.018 [INFO ]  [app1-sub] Coca - app1-sub closed.
2017-10-16 18:19:50.018 [INFO ]  [main] Coca - app1 closed.     //停止app1
2017-10-16 18:19:54.458 [INFO ]  [app2-sub] Coca - app2-sub closed.
2017-10-16 18:19:54.459 [INFO ]  [main] Coca - app2 closed.     //停止app2
```

## 运行要求
- (0.0.1, 1.0.0)    // coca版本 维护
    - jdk7 or higher
- [1.0.0, 2.0.0)    // 开发
    - jdk8
- [2.0.0, +∞)
    - jdk9

## 性能测试
- [Co稳定性测试](coca-co/src/test/java/coca/co/TestCoLocal.java)
- [Redis测试](coca-demo/src/main/java/coca/demo/benchmark/redis/CocaRedisBenchmark.java)
- [RMQ测试](coca-demo/src/main/java/coca/demo/benchmark/rmq/CocaRMQBenchmark.java)

## 文档链接
- [工程源码](doc/coca_project.md)

## TODO
- redis保证组消息可靠到达，使用日志+group实现重发机制
- 组消息丢失，手工补救方式，如web界面
- Co监控(发送指令数/s，接收指令数/s) -> 日志
- 实现PUB_TIMEOUT,ACK_TIMEOUT,ACK_SUCC,ACK_FAIL更新InsFuture
- ca写相同值时的优化处理
- co的发送限制规则：是否只允许在已join的组内发消息
- join的组存在后，再发送其他消息，完善协同语义





