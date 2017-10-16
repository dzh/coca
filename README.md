coca
===================================
Co-Cache 缓存的协同框架

## 快速开始
- 示例说明
    - 通过2个coca实例实现本地缓存地同步改动
    - 1级缓存通过guava-cache实现, 需要coca-guava组件
    - 2级缓存由redis实现, 需安装启动redis-server
    - 缓存同步通过redis的pub/sub实现, 需要coca-redis组件
    
- 示例源码 coca-demo/coca.demo.sample/
    - SampleMain启动2个coca实例,分别起名为`app1、app2`, 他们的同步组名为`syncGroup`
    - 缓存key分类
        - coca.app1.*   app1的私有缓存，不需要同步
        - coca.app2.*   app2的私有缓存，不需要同步
        - coca.share.*  app1和app2的共享缓存，需要同步
    - 缓存读策略(默认): 1级缓存 if(nil)-> 2级缓存
        - 支持回写, 2级缓存读到的消息写到1级缓存
    - 缓存写策略(默认): 2级缓存 -> 1级缓存 -> 消息同步

- 在IDE中启动SampleMain,执行testShareWrite, (和代码一起看)打印日志分析如下:

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
- (0.0.1, 1.0.0)    //coca版本
    - jdk7 or higher
- [1.0.0, 2.0.0)
    - jdk8
- [2.0.0, +∞)
    - jdk9

## 性能测试


## 文档链接
- [工程源码](doc/coca_project.md)




