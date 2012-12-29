BlackHole
=========

####1. 简介

BlackHole是一个迷你型的DNS服务器。它的主要特色是可以通过简单配置，将DNS请求导向某些特定IP。

####2. 用途

BlackHole最简单直接的用途是在开发和测试环境中将域名指向某个IP。与修改hosts文件相比，BlackHole配置更方便，并且支持通配符。

例如：

	127.0.0.1	*.codecraft.us
	
表示将所有以.codecraft.us形式结尾的域名全部指向127.0.0.1。

目前BlackHole已经有一个单机版本hostd，用于解决上述的问题。

BlackHole的另一个愿景是：用于机构的内部DNS拦截。如果内部DNS希望将某些域名导向特定IP，从而达到封禁的目的，则可以通过简单配置BlackHole完成。

####3. 原理：

BlackHole存在两种工作模式："拦截"和"转发"。

* #####拦截


	当DNS客户端的请求在BlackHole中有对应配置时，则进入拦截模式。拦截模式使用正则表达式匹配域名并拦截。同时支持PTR反解。

* #####转发

	当DNS客户端的请求在BlackHole中没有对应zones配置时，则进入转发模式。转发模式下，BlackHole会将UDP请求转发给另一台DNS服务器，并将该DNS服务器的响应转发回客户端。	
#####基准测试

在基准测试中，拦截模式下不开启cache，qps为BIND的50%，为17000，如果开启cache，对于有缓存的数据达到40000qps，优于BIND，已经能满足企业内网需要。

基准测试结果见：
[BlackHole vs BIND benchmark](https://github.com/flashsword20/blackhole/blob/master/benchmark)

####4. 稳定性

目前BlackHole的稳定性未得到广泛证明，但是作者在不断更新中，欢迎使用并及时反馈。

####5. 协议

BlackHole的连接部分参考了EagleDNS的代码，遵守LGPLv3协议。

作者邮箱：
yihua.huang#dianping.com