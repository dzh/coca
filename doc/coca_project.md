Coca Project
================================
代码。。。

## 工程源码
```
- caca
    - coca-api                     缓存同步框架的主要功能
        - Coca                     封装Ca和Co,实现缓存同步,详见`配置说明`一节
        - CocaConst                指令和常量定义,如EVICT-删除本地缓存指令
        - ca.CocaListener          写Local缓存时触发,产生EVICT指令
        - co.CocaInsFactory        Coca的指令工厂,配置参数coca.co.CoConst.P_CO_INS_FACTORY
        - handler.EvictHandler     Evict指令处理
        
    - coca-ca               缓存调度的核心实现
        - Ca                缓存代理，具体实现如CaGuava、CaRedis等
        - stack.CaStack     Stack维护一组Ca的读写,从顶到底表示1级到N级缓存,通常1级缓存是Local类型(如CaGuava),2级缓存是Remote类型(如CaRedis)
        
    - coca-co                       指令de同步核心实现
        - Co                        Co表示一个同步组件,一个应用生成一个Co实例                  
        - ins.InsConst              Co内部指令定义,自定义指令参考它
        - ins.actor                 CoActor拦截并处理指令,除了Co.sub()之外的处理指令接收并处理方式
        - io.GroupChannelSelector   管理Channel
        - channel.GroupChannel      组Channel实现组内指令同步的主要功能
        
    - coca-demo         示例和测试
        - benchmark     coca性能压测
        - sample        简单的coca使用示例 
        
    - coca-ehcache      TODO 待实现
    
    - coca-guava        Guava实现Local缓存
        - ca.CaGuava    Guava-Cache的Ca实现
        
    - coca-redis                        使用redisson客户端,包括Ca和Co具体功能实现
        - ca.CaRedis                    Redis的Ca实现
        - co.io.RedisChannelSelector    返回RedisGroupChannel
        - co.io.RedisGroupChannel       通过Redis的pub/sub实现组消息同步
        
    - coca-rmq                          RocketMQ的Co实现
        - co.io.RMQChannelSelector      返回RMQGroupChannel
        - co.io.RMQGroupChannel         通过RMQ的消息广播实现组消息同步
        
    - doc
        - bak 备份内容
        - dev 开发资源
```

## 配置说明
- [Coca配置参数](../coca-api/README.md)
- [Co配置参数](../coca-co/README.md)
- [redis实现](../coca-redis/README.md)
- [RocketMQ实现](../coca-rmq/README.md)
   
## 版本依赖
- (0.0.1, 1.0.0)
    - jdk7 or higher 
- [1.0.0, 2.0.0)
    - jdk8
- [2.0.0, +∞) 待定
    - jdk9
    
## 贡献源码
- master稳定分支,develop开发分支。请Pull Request到develop
- Eclipse配置文件
    - [代码格式化文件](doc/dev/eclipse_format.xml)
    - [注释模版文件](doc/dev/eclipse_template.xml)
    
## 设计思考
- co的目标是为了实现多个应用之间的消息传输。
    - GroupChannel的消息同步概念和MQ的pub/sub类似，还可以实现其他消息传递方式
- ca的目标是希望把缓存之间的调度策略能用一个通用的方式组织。
    - 调度策略是相对明确的几种，如读缓存的一般策略是优先从1级缓存读，若从2级缓存或数据库获取，则再回写到1级缓存
- 最后，很自然的coca就是把co和ca的功能合在一起实现分布式应用系统之间的缓存同步。
    - coca=co+ca，分层设计希望系统更好的扩展和维护




