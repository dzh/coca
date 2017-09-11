Coca设计
===============================================

## 缓存的问题、解决

### coca应用
- 数据缓存，一些key需保证一致性
- 配置文件，动态修改
- 代码执行环境切换
- 数据流的流向控制
- 日志级别动态修改

### 缓存系统
- 现在已有很多系统解决存储，不缺缓存系统
- 缓存的定义是啥，本质是具有临时属性数据的搬运工问题，缓存数据的特性是啥
- 现在不缺缓存系统，缺少具备调度这类属性数据的应用网络协议和实现，所以我怀疑ehcache的集群是否忽视了redis等
- 我理解的缓存系统需要做什么，本质是传输特性数据的IO协议+调度存储系统的规则
    - io协议，处理不同属性数据的传输
        - io模型如何实现
        - 多语言实现，io协议的语义清楚
    - 各种存储系统的多级调用
    - Co是io协议，处理传输
    - Ca是调度器，代理组合各类存储系统
    - CoCa本质是一种数据传输模型概念，表象是一个系统
    
### 数据特性 分布式环境
- 时效，过期失效
- 同步 事务 一致

### ZAB

### Raft
- https://raft.github.io/
- 一致性和不丢数据

### ViewStamp

### Paxos
- ballot quorum decree vote
- desc
    - B1(ß): 中的每一轮表决，都有一个唯一的编号
    - B2(ß): 中任意两轮表决的法定人数集中，至少有一个公共的牧师成员
    - B3(ß): 对于中每一轮表决B，如果B的法定人数集中的任何一个牧师在 中一个更小轮的表决中投过(赞成)票，那么B的法令与所有这些更小轮表决中的最大的那次表决的法令相同


### 多级缓存
- 进程内 L1
    ehcache javax.cache
- 集中式缓存 L2
    memcached redis
- 分布式缓存
    ehcache
   
    
### 缓存控制
- 缓存操作
    - init 初始化、预热
    - reload 重新加载
    - get 获取
    - put 添加／修改
    - delete 删除
    - ...
- 分布式缓存
    - 协同 集群环境下多机的进程内缓存put/delete/expire后与如何保持一致 （ehcache分布式多机进程内缓存)
    
### 缓存与GC
- GC时，缓存可释放 (weak soft)
- 尽量避免由缓存引起的FGC (offheap)

### 最大化利用缓存
- 进程内(ehcache) <- 集中式缓存(redis), 效率最优同时防止redis等太多IO消耗
- 协同多机进程内缓存数据 zk

## coca_0.1
Cooperation-Cache

### 设计(以实现为准)
- 目标
    - 内存利用最大化
    - 网络资源利用最优
    - 保证数据在集群中的一致性
    - 支持多语言的异构系统
- coca是一个缓存代理,本身不做缓存的事情,它利用各类存储定义N级缓存(ehcache redis mysql...),并提供缓存之间的协同指令
    - coca名字=机器名+进程ID+2位随机数
    - coca包含多个缓存视图(view)
- N级缓存控制
    - 缓存视图,定义一种缓存优先级L1->L2->LN,包含读写指针。如ehcache作为L1缓存、redis作L2、mysql作L3
        - 写指针(pw),开始写的位置,如pw->L2,那就从L2开始写数据,(可选)默认2写数据时异步写L1
        - 读指针(pr),开始读的位置,如pr->L2,那就从L2开始读数据,(可选)默认L2无数据则读L3并更新L2
        - 自动优化缓存,hot data 
- 协同组特性
    - 协同组实现了相同组名内coca的共性协同操作，ins定义操作含义。一个coca可加入多个组
    - coca组通过coca-master管理(zk),组的变化组内会收到相应时间
        - join 加入组
        - quit 离开组
    - 同组内的coca具有指令一致性功能，ins的弱一致性(最终一致),强一致性(实时一致)
    - 组内支持广播,coap之间P2P单播
    - 组内最后修改(lm)必须保持一致，通过coap定期从coca-master获取每个成员的lm, 领先的coca向落后的发送指令
- 协作指令(ins)
    - evict

### 协同协议 coca_udp
以下clnt1、clnt2、clnt3表示一个集群组内的3个缓存示例

- 加入组 zk
    - clnt1.join(g1); # clnt1.get() from L2
    - clnt1.clearCache(); or clnt1.waitForAmend(10s); # clnt1.get() from L1
    - clnt2|clnt3 and so on
- 离开组 zk
    - clnt1.quit(g1); # clnt1 quit from g1
    - clnt2.|clnt3.deleteInsFailLog
- 发送指令
    - clnt1.send(evict,g1);log2File(clnt2.ins.evict,clnt3.ins.evict);
    - clnt2.recv(evict);clnt2.send(evictAck,clnt1), and clnt3 also does
    - clnt1.recv(clnt2.evictAck);logSuc(clnt2.ins.evict)
    - while(clnt1.recv(clnt3.evictAck).miss & maxRetry > 0){delay(3s);clnt1.send(evict,clnt3);--maxRetry;} # clnt3 left from g1 after maxRetry*3s
    - if(maxRetry==0 & clnt1.failEvictAck){logFail(clnt3.ins.evict);};
    - when(clnt3.join(g1)){clnt1.readLog;clnt1.send(evict)} # 1st step
    
### 指令日志
- ins.log 指令日志 记录发送消息状态(send|suc|fail)
    - 监听到有join时创建
    - 每个clnt一个log
    - 定长格式日志
- ins_fail.log 记录发送失败的ins在ins.log中的偏移量，顺序和消息发送一致
    - 在对应clnt退组后删除文件
    - 在Ack接收成功后删除对应条目
- 写日志原则
    - 不是所有操作需要协同，若不一致可容忍一段时间(缓存超时),则不走协同过程

## 参考
- http://www.ibm.com/developerworks/cn/java/j-lo-ehcache/
- http://git.oschina.net/ld/J2Cache