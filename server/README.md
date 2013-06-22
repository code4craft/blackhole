BlackHole
=========

####1. 简介

BlackHole是一个迷你型的DNS服务器。它的主要特色是可以通过简单配置，将DNS请求导向某些特定IP。同时可以通过特征判断的方式，防止DNS污染。

####2. 用途

BlackHole最简单直接的用途是在开发和测试环境中将域名指向某个IP。与修改hosts文件相比，BlackHole配置更方便，并且支持通配符。

例如：

	127.0.0.1	*.codecraft.us
	
表示将所有以.codecraft.us形式结尾的域名全部指向127.0.0.1。

BlackHole还可以防止DNS污染攻击，对于某些无法访问的网站可以起到作用。BlackHole防止DNS攻击的方式参见：[http://code4craft.github.com/blog/2013/02/25/blackhole-anti-dns-poison/](http://code4craft.github.com/blog/2013/02/25/blackhole-anti-dns-poison/)

BlackHole还有一个单机版本hostd，整合了系统的DNS服务器修改/恢复等操作，无需用户自己修改，目前有Mac版本。


####3. 安装BlackHole:

BlackHole的编译后版本保存在https://github.com/code4craft/blackhole-bin，直接clone这个项目到某一目录即可。

	git clone https://github.com/code4craft/blackhole-bin.git /usr/local/blackhole

你也可以使用自动脚本进行安装：

	curl http://code4craft.github.io/blackhole/install.sh | sh
	
然后通过sudo /usr/local/blackhole/blackhole.sh start可以启动。

Windows系统可将文件保存到任意目录，并运行start.bat(Win7下无需用管理员权限启动)，若弹出终端界面并且持续运行，则启动成功。

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
	
	支持多个DNS服务器配置，BlackHole会优先采用较前面的配置的结果。如果你在内网配置了DNS拦截，请将你的内网DNS服务器放到第一位。

* **DNS_TIMEOUT**
	
	请求外部DNS的超时时间，单位为毫秒。
	
* **CACHE**
	
	转发模式下，是否对DNS结果进行缓存。默认是true。
	
* **CACHE_EXPIRE**
	
	设置缓存的过期时间，单位是秒。不设置此项或设置为0，则使用默认值。默认缓存的失效时间是DNS结果的TLL值。
	
* **TTL**

	BlackHole拦截的DNS请求的过期时间，单位是秒。
	
* **LOG**
	
	日志的等级。可选配置为('DEBUG','INFO','WARN')，对应log4j的等级。开启'DEBUG'级别的日志会显示每次请求和应答，但是会大大降低吞吐量。

* **SAFE_BOX**
	
	是否开启反DNS污染功能。关闭此项可增加吞吐量。

* **FAKE_DNS**
	
	伪造的DNS服务器，用于检测DNS污染。一般无须更改。
	
#####zones

zones是DNS系统的域名配置文件。BlackHole简化了zones的配置。

BlackHole的配置跟Hosts文件是一样的，但是支持通配符"*"。

例如：

	127.0.0.1	*.codecraf.us

*可以匹配任意长度的任意字符(包括'.')。这项配置可以将所有以.codecraf.us形式结尾的域名全部指向127.0.0.1。

当然，你也可以这样配置：

	127.0.0.1	*

这表示把所有请求导向本地。这个配置在某些场合(例如测试邮件发送服务)会有用。

BlackHole还支持NS记录的配置。NS记录的意思是，对于某些域名的请求，总是向某个DNS服务器查找结果。例如，你可以使用组合配置：

	173.194.72.103 *.google.com
	NS docs.google.com

这两项配置的意思是：将*.google.com的地址都指向173.194.72.103，但是对于docs.google.com域名，则不进行拦截，直接使用外部DNS服务器的返回值作为结果。

支持SHELL的系统可以使用blackhole.sh zones来快速配置zones文件。修改zones文件是动态生效的。

如果两条规则存在交集，前面的规则会生效。

####5. 运行BlackHole:

blackhole使用Java编写，可以使用于任何平台。支持shell的平台使用"blackhole.sh start"即可启动BlackHole。因为使用了53端口，所以需要具有root权限。

Windows下可以使用start.bat脚本来启动，不需要管理员权限运行。

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
	
* stat_cache 
	
	显示缓存状态。

* dump_cache
	
	将缓存输出到文件cache.dump，便于调试。
	
例如：

重新读取配置文件的命令为：

	java -jar wifesays.jar -creload
	
支持SHELL的系统可以使用以下命令快速操作：
	
	blackhole.sh {start|stop|restart|reload|zones|config|cache}

####7. 配置DNS服务器：

若某个系统希望使用blackhole作为DNS服务器，则在blackhole生效前，需要将blackhole所在地址配置为第一位的DNS服务器，并将系统默认的DNS服务器地址配置到blackhole/config/blackhole.conf中。

####8. 性能：

BlackHole存在两种工作模式："拦截"和"转发"。

* #####拦截


	当DNS客户端的请求在BlackHole中有对应zones配置时，则进入拦截模式。拦截模式下，BlackHole略低于BIND。

* #####转发

	当DNS客户端的请求在BlackHole中没有对应zones配置时，则进入转发模式。转发模式下，BlackHole会将UDP请求转发给另外的DNS服务器，并将该DNS服务器的响应转发回客户端。BlackHole转发模式下会进行DNS污染的防御。
	
#####基准测试

在基准测试中，拦截模式下不开启cache，qps为BIND的50%，为17000，如果开启cache，对于有缓存的数据达到40000qps，优于BIND，已经能满足企业内网需要。

基准测试结果见：
[BlackHole vs BIND benchmark](https://github.com/code4craft/blackhole/blob/master/server/benchmark)

####9. 稳定性

目前BlackHole的稳定性未得到广泛证明，但是作者在不断更新中，欢迎使用并及时反馈。如果经常出现访问不了的情况，请关闭本地cache(blackhole.conf中设置cache=false)。

####10. 协议

BlackHole的连接部分参考了EagleDNS的代码，遵守LGPLv3协议。

作者邮箱：
code4crafter@gmail.com
