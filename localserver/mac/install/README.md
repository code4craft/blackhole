hostd
=========

###1. 简介

hostd是一款简单可配置的本地DNS服务器，它用于在本地开发环境中临时将某个域名指向特定IP，是修改hosts文件的替代方案。

###2. 特色

hostd具有三大特性：

* #####简单
	hostd启动之后可进行DNS拦截，关闭后则恢复正常，不侵入系统。
* #####灵活
	hostd采用hosts文件同样的格式，但是支持通配符"*"，对于多个二级域名的站点配置更加方便。
* #####高效
	hostd基于DNS服务器BlackHole，单机服务器平均响应时间小于0.5毫秒，吞吐量达到数万/秒，不会对正常网络访问有任何影响。
	
###3. 下载

TODO.hostd需要jre 1.6以上的运行环境。

###4. 使用

hostd需要root权限启动，启动之后方可拦截DNS请求。

启动：sudo hostd start

关闭：hostd stop

或者你可以使用"sudo hostd"命令快捷启动hostd并编辑配置文件。若hostd已启动，则此命令仅仅会打开配置文件供你编辑。


###5. 配置

hostd的主要配置文件是/etc/hostd。同hosts文件一样，用户可以直接修改/etc/hostd文件，与修改hosts文件相比，hostd配置更方便，并且支持通配符。

例如：

	127.0.0.1	*.codecraft.us
	
表示将所有以.codecraft.us形式结尾的域名全部指向127.0.0.1。

用户修改/etc/hostd是实时生效的。

hostd还有一个不常用的配置文件：/etc/blackhole.conf，是hostd使用的DNS服务器BlackHole所使用的配置，一般情况无需更改。配置可以参考TODO。

###6. 常见问题


作者邮箱：
yihua.huang#dianping.com