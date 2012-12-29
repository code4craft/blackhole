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
	
###3. 安装

Mac下hostd下载pkd包安装即可。hostd将安装到/usr/local/hostd/目录。hostd需要jre 1.6以上的运行环境。

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

hostd还有一个不常用的配置文件：/etc/blackhole.conf，是hostd使用的DNS服务器BlackHole所使用的配置，一般情况无需更改。配置可以参考[BlackHole Readme](https://github.com/flashsword20/blackhole/blob/master/server/README.md)。

###6. 常见问题

#####Q: hostd无法生效？

#####A: 遇到这个问题，请你做以下检查：
1.请检查hostd进程是否已启动，未启动时即使/etc/hostd进行了配置也无法起作用。方法是再次使用sudo hostd start启动，若未报错，则表示之前未启动。

2.hostd是运行了一个本地DNS服务器，并将本地DNS服务器设置为127.0.0.1以达到DNS拦截的作用，所以建议你查看DNS配置是否为127.0.0.1。查看方式：在终端下输入nslookup xx.com，或者选择"网络偏好设置"=>"高级"=>"DNS服务器"来进行查看。如果首要DNS服务器未配置为"127.0.0.1"，则重新启动hostd即可。

3.如果使用Chome浏览器，那么因为浏览器DNS缓存的原因，域名的配置可能要最长一分钟才能生效，请多尝试几次。

###7. 源码

hostd的源码托管在git[github](https://github.com/flashsword20/blackhole/tree/master/localserver/mac)，是开源DNS服务器BlackHole的一个分支。


作者邮箱：
yihua.huang@dianping.com 若使用中遇到问题，欢迎及时联系。