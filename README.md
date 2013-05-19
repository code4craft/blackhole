BlackHole
=========

####1. 简介

BlackHole是一个迷你型的DNS服务器。它的特点是支持hosts风格域名配置，支持缓存和多路代理，甚至可以为每台用户机指定独立的域名配置，以满足企业内网的DNS缓存和拦截的需求。

BlackHole可以通过特征判断的方式，防止DNS污染。

####2. 用途

BlackHole最简单直接的用途是在开发和测试环境中将域名指向某个IP。与修改hosts文件相比，BlackHole配置更方便，并且支持通配符。

例如：

	127.0.0.1	*.codecraft.us
	
表示将所有以.codecraft.us形式结尾的域名全部指向127.0.0.1。

BlackHole致力于成为一个方便稳定的企业内网DNS服务器，有一个支持配置DNS地址的Web服务端server-suite，正在开发中。目标是能够让用户方便的更改自己本地的DNS拦截规则，同时做到设备无关性，让移动设备也能轻易的修改域名配置，方便开发和测试。

BlackHole还可以防止DNS污染攻击，对于某些"无法访问的网站"可以起到作用。BlackHole防止DNS攻击的方式参见：[http://code4craft.github.com/blog/2013/02/25/blackhole-anti-dns-poison/](http://code4craft.github.com/blog/2013/02/25/blackhole-anti-dns-poison/)

BlackHole还有一个单机版本hostd，整合了系统的DNS服务器修改/恢复等操作，无需用户自己修改，目前有Mac版本，支持10.7，目前已暂停维护。

####3. 安装及配置

若想使用BlackHole作为DNS服务器，则将[https://github.com/code4craft/blackhole/tree/master/server/bin](https://github.com/code4craft/blackhole/tree/master/server/bin)下的文件下载即可。

Linux系列系统请将文件复制到/usr/local/blackhole/文件夹下，若不在此目录，请将blackhole.sh中

	HOME_DIR=/usr/local/blackhole
	
修改成你的目录。然后通过sudo blackhole.sh start可以启动。

Windows系统可将文件保存到任意目录，并运行start.bat(Win7下无需用管理员权限启动)，若弹出终端界面并且持续运行，则启动成功。

具体的设置请看[Blackhole Server Readme](https://github.com/code4craft/blackhole/blob/master/server/README.md)。

####4. 原理：

BlackHole存在两种工作模式："拦截"和"转发"。

* #####拦截


	当DNS客户端的请求在BlackHole中有对应配置时，则进入拦截模式。拦截模式使用正则表达式匹配域名并拦截。同时支持PTR反解。

* #####转发

	当DNS客户端的请求在BlackHole中没有对应zones配置时，则进入转发模式。转发模式下，BlackHole会将UDP请求转发给另外的DNS服务器，并将该DNS服务器的响应转发回客户端。	
#####基准测试

在基准测试中，拦截模式下不开启cache，qps为BIND的50%，为17000，如果开启cache，对于有缓存的数据达到40000qps，优于BIND，已经能满足企业内网需要。

基准测试结果见：
[BlackHole vs BIND benchmark](https://github.com/flashsword20/blackhole/blob/master/benchmark)

####5. 稳定性

目前BlackHole已经比较稳定，有支持企业内部几百人使用的案例。

####6. 协议

BlackHole的连接部分参考了EagleDNS的代码，遵守LGPLv3协议。

作者邮箱：
code4crafter@gmail.com