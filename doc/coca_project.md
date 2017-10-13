Coca Project
================================
代码。。。

## 工程源码
```
- caca
    - coca-api                              缓存同步框架的主要功能
        - coca.api.Coca                     封装Ca和Co,实现缓存同步,详见`配置说明`一节
        - coca.api.CocaConst                指令和常量定义,如EVICT-删除本地缓存指令
        - coca.api.ca.CocaListener          写Local缓存时触发,产生EVICT指令
        - coca.api.co.CocaInsFactory        Coca的指令工厂,配置参数coca.co.CoConst.P_CO_INS_FACTORY
        - coca.api.handler.EvictHandler     Evict指令处理
        
    - coca-ca                       缓存调度的核心实现
        - coca.ca.Ca                缓存代理，具体实现如CaGuava、CaRedis等
        - coca.ca.stack.CaStack     Stack维护一组Ca的读写,从顶到底表示1级到N级缓存,通常1级缓存是Local类型(如CaGuava),2级缓存是Remote类型(如CaRedis)
        
    - coca-co                               指令de组同步核心实现
        - coca.co.Co                        Co表示一个同步组件,一个应用生成一个Co实例                  
        - coca.co.ins.InsConst              Co内部指令定义
        - coca.co.ins.actor                 CoActor拦截并处理指令,除了Co.sub()之外的处理指令方式
        - coca.co.io.GroupChannelSelector   管理Channel
        - coca.co.channel.GroupChannel      组Channel实现组内指令同步的主要功能
        
    - coca-ehcache TODO 待实现
    
    - coca-guava                Guava实现Local缓存
        - coca.guava.ca.CaGuava Guava-Cache的Ca实现
        
    - coca-redis                                使用redisson客户端,包括Ca和Co具体功能实现
        - coca.redis.ca.CaRedis                 Redis的Ca实现
        - coca.redis.co.io.RedisChannelSelector 返回RedisGroupChannel
        - coca.redis.co.io.RedisGroupChannel    通过Redis的pub/sub实现组消息同步
        
    - coca-rmq                              RocketMQ的Co实现
        - coca.rmq.co.io.RMQChannelSelector 返回RMQGroupChannel
        - coca.rmq.co.io.RMQGroupChannel    通过RMQ的消息广播实现组消息同步
        
    - doc
        - bak 备份资源
        - dev 开发资源
```

## 配置说明
- Coca配置参数
- Ca配置参数
- Co配置参数


   
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





