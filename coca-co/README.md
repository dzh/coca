coca-co
====================

## [配置定义](src/main/java/coca/co/CoConst.java) [=默认值]
- co.init = coca.co.init.MapInit
- co = coca.co.BasicCo
- co.heartbeat.tick = 3000
- co.ins.factory = coca.co.ins.CoInsFactory
- co.ins.codecs = coca.co.ins.codec.TextInsCodec
- co.io = coca.co.io.BasicIO
- co.io.selector = coca.co.io.LocalChannelSelector
- co.io.actors = coca.co.ins.actor.JoinActor coca.co.ins.actor.QuitActor coca.co.ins.actor.HeartbeatActor