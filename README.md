BlackHole
=========

####简介

BlackHole是一个迷你型的DNS服务器。它的主要特色是可通过简单配置，将DNS请求导向某些特定IP。

BlackHole最简单直接的用途是在开发和测试环境中将域名指向某个IP。与修改hosts文件相比，BlackHole配置更方便，并且支持通配符。

例如：

	127.0.0.1	*.codecraft.us
	
表示将所有以.codecraft.us形式结尾的域名全部指向127.0.0.1。

####安装BlackHole:

将[https://github.com/flashsword20/blackhole/tree/master/bin](https://github.com/flashsword20/blackhole/tree/master/bin)下的文件全部拷贝到/usr/local/blackhole目录下即可。

####配置BlackHole:

如果你没有将BlackHole安装到/usr/local/blackhole，则需要修改blackhole.sh，将HOME_DIR更改为你的安装目录。

BlackHole目前有两个配置文件，分别是config/blackhole.conf和config/zones。

#####blackhole.conf

是blackhole的主要配置文件。

配置格式如下：

	key=value

例如：

	DNS=192.168.0.1
	
目前可配置项如下：

* **TTL**

	(time to live，DNS过期时间，单位是秒)。
* **DNS**
	
	BlackHole并没有递归查找DNS的功能，如果遇到未在本地配置的域名请求，它会做一个UDP代理的工作，将请求发向一个已有的DNS服务器，并返回这个DNS服务器的结果。**如果你只希望用BlackHole做某些域名的拦截，需要对这个DNS服务器的地址进行配置。**

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

####运行BlackHole:

blackhole使用Java编写，可以使用于任何平台。支持shell的平台使用"blackhole.sh start"即可启动BlackHole。因为使用了53端口，所以需要具有root权限。

Windows下可以使用Java命令来启动。

	java -jar blackhole.jar -dFOLDER

FOLDER为blackhole所在文件夹。

####管理BlackHole：

blackhole的监控模块使用了作者的另一个开源项目[wifesays](https://github.com/flashsword20/wifesays)。wifesays是一个简单的Java进程内TCP服务器，使用40310端口作为监控端口，基于TCP协议上封装了一些简单的文本命令。

可以使用
	
	java -jar wifesays.jar -cCOMMAND

来通知BlackHole。

COMMAND为命令。目前支持的命令为：

* reload

	重新读取配置，包括zones和blackhole.conf。

* shutdown

	退出。
	
例如：

重新读取配置文件的命令为：

	java -jar wifesays.jar -creload
	
支持SHELL的系统可以使用以下命令快速操作：
	
	blackhole.sh {start|reload|stop}

####配置DNS服务器：

在blackhole生效前，需要将blackhole所在地址配置为第一位的DNS服务器，并将系统默认的DNS服务器地址配置到blackhole/config/blackhole.conf中。
