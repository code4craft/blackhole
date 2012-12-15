###BlackHole

####简介

BlackHole是一个迷你型的DNS服务器。它的主要特色是可简单配置，将DNS请求导向某些特定IP。



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
	
	BlackHole并没有查找DNS的功能，如果遇到未在本地配置的域名请求，它会做一个代理的工作，将请求发向一个已有的DNS服务器，并返回这个DNS服务器的结果。**如果你只希望用BlackHole做某些域名的拦截，需要对这个DNS服务器的地址进行配置。**

#####zones

zones是DNS系统的域名配置文件。BlackHole简化了zones的配置，去掉了SOA、NX等配置项。

BlackHole的配置跟Hosts文件是一样的，但是支持通配符"*"。

例如：

	127.0.0.1	*.diandian.com

*可以匹配任意长度的任意字符(包括'.')。这项配置可以将所有以.diandian.com形式结尾的域名全部指向127.0.0.1。是的，改Host什么的弱爆了!

当然，你也可以这样配置：

	127.0.0.1	*

这表示把所有请求导向本地。

####启动BlackHole:

blackhole.sh start 
