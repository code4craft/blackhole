BlackHole
=========

####1. 简介

BlackHole是一个迷你型的DNS服务器。它的主要特色是可以通过简单配置，将DNS请求导向某些特定IP。

####2. 用途

BlackHole最简单直接的用途是在开发和测试环境中将域名指向某个IP。与修改hosts文件相比，BlackHole配置更方便，并且支持通配符。

例如：

	127.0.0.1	*.codecraft.us
	
表示将所有以.codecraft.us形式结尾的域名全部指向127.0.0.1。

BlackHole的另一个愿景是：用于机构的内部DNS拦截。如果内部DNS希望将某些域名导向特定IP，从而达到封禁的目的，则可以通过简单配置BlackHole完成。


####3. 安装BlackHole:

将[https://github.com/flashsword20/blackhole/tree/master/bin](https://github.com/flashsword20/blackhole/tree/master/bin)下的文件全部拷贝到/usr/local/blackhole目录下即可。

####4. 配置BlackHole:

如果你没有将BlackHole安装到/usr/local/blackhole，则需要修改blackhole.sh，将HOME_DIR更改为你的安装目录。

BlackHole目前有两个配置文件，分别是config/blackhole.conf和config/zones。

#####blackhole.conf

是blackhole的主要配置文件。

配置格式如下：

	key=value

例如：

	DNS=192.168.0.1
	
目前可配置项如下：

* **DNS**
	
	BlackHole并没有递归查找DNS的功能，如果遇到未在本地配置的域名请求，它会做一个UDP代理的工作，将请求发向一个已有的DNS服务器，并返回这个DNS服务器的结果。**如果你希望用BlackHole做某些域名的拦截，同时不影响其他域名的访问，需要对这个DNS服务器的地址进行配置。**
	
	支持多个DNS服务器配置，BlackHole会优先选择访问速度最快的服务器作为外部DNS。

* **DNS_TIMEOUT**
	
	请求外部DNS的超时时间，单位为毫秒。
	
* **CACHE**
	
	转发模式下，是否对DNS结果进行缓存。默认是true。缓存的失效时间由DNS结果的TLL值决定。缓存的保存时间不会大于DNS结果的最小TTL值。
	
* **TTL**

	(time to live，DNS过期时间，单位是秒)。
	
* **LOG**
	
	日志的等级。可选配置为('DEBUG','INFO','WARN')，对应log4j的等级。开启'DEBUG'级别的日志会显示每次请求和应答，但是会大大降低吞吐量。

#####zones

zones是DNS系统的域名配置文件。BlackHole简化了zones的配置，去掉了SOA、NX等配置项。

BlackHole的配置跟Hosts文件是一样的，但是支持通配符"*"。

例如：

	127.0.0.1	*.codecraf.us

*可以匹配任意长度的任意字符(包括'.')。这项配置可以将所有以.codecraf.us形式结尾的域名全部指向127.0.0.1。

当然，你也可以这样配置：

	127.0.0.1	*

这表示把所有请求导向本地。这个配置在某些场合(例如测试邮件发送服务)会有用。

支持SHELL的系统可以使用blackhole.sh zones来快速配置zones文件。

####5. 运行BlackHole:

blackhole使用Java编写，可以使用于任何平台。支持shell的平台使用"blackhole.sh start"即可启动BlackHole。因为使用了53端口，所以需要具有root权限。

Windows下可以使用Java命令来启动。

	java -jar blackhole.jar -dFOLDER

FOLDER为blackhole所在文件夹。

####6. 管理BlackHole：

blackhole的监控模块使用了作者的另一个开源项目[wifesays](https://github.com/flashsword20/wifesays)。wifesays是一个简单的Java进程内TCP服务器，使用40310端口作为监控端口，基于TCP协议上封装了一些简单的文本命令。

可以使用
	
	java -jar wifesays.jar -cCOMMAND

来通知BlackHole。

COMMAND为命令。目前支持的命令为：

* reload

	重新读取配置，包括zones和blackhole.conf。

* shutdown

	退出。

* clear_cache

	清除缓存(仅当使用缓存时有效)。

	
例如：

重新读取配置文件的命令为：

	java -jar wifesays.jar -creload
	
支持SHELL的系统可以使用以下命令快速操作：
	
	blackhole.sh {start|stop|restart|reload|zones|config}

####7. 配置DNS服务器：

若某个系统希望使用blackhole作为DNS服务器，则在blackhole生效前，需要将blackhole所在地址配置为第一位的DNS服务器，并将系统默认的DNS服务器地址配置到blackhole/config/blackhole.conf中。

####8. 性能：

BlackHole存在两种工作模式："拦截"和"转发"。

* #####拦截


	当DNS客户端的请求在BlackHole中有对应zones配置时，则进入拦截模式。拦截模式下，BlackHole略低于BIND。

* #####转发

	当DNS客户端的请求在BlackHole中没有对应zones配置时，则进入转发模式。转发模式下，BlackHole会将UDP请求转发给另一台DNS服务器，并将该DNS服务器的响应转发回客户端。此时性能降低为拦截模式的1/2。可以选择对DNS服务器的响应进行缓存，那么下次请求的开销会降低。
	
#####基准测试

在基准测试中，拦截模式下不开启cache，qps为BIND的50%，为17000，如果开启cache，对于有缓存的数据达到40000qps，优于BIND，已经能满足企业内网需要。

基准测试结果见：
[BlackHole vs BIND benchmark](https://github.com/flashsword20/blackhole/blob/master/benchmark)

####9. 稳定性

目前BlackHole的稳定性未得到广泛证明，但是作者在不断更新中，欢迎使用并及时反馈。

作者邮箱：
yihua.huang#dianping.com