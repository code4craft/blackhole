BlackHole
=========

###1. 简介

BlackHole是一个Java编写的DNS服务器，它可以进行DNS缓存，也支持自定义域名配置，并可以防止DNS污染。比起老牌的DNS软件pdnsd、BIND，BlackHole功能比较简单，但是更容易使用，性能也更好。

BlackHole还包含一个Web管理模块[**Hostd**](https://github.com/code4craft/hostd)，可以让每个用户管理自己的域名配置，并且彼此之间不冲突。

###2. 用途

#### DNS缓存

BlackHole具有DNS缓存以及持久化的功能，可以作为一个DNS缓存服务器使用，以加速DNS访问。

BlackHole缓存性能优秀，可以支持每秒50000次随机查询，平均响应时间0.3ms，高于pdnsd及BIND([测试报告](https://github.com/code4craft/blackhole/blob/master/server/benchmark-other-dns-server))。

#### hosts风格自定义域名

BlackHole也支持修改域名配置，配置域名的方式非常简单，与hosts文件一致，并且支持通配符(目前仅支持A记录)。

例如：

	127.0.0.1	*.codecraft.us
	
表示将所有以.codecraft.us形式结尾的域名全部指向127.0.0.1。

#### 防止DNS污染

BlackHole还可以通过UDP特征判断的方式防止DNS污染攻击，对于某些无法访问的网站可以起到作用。BlackHole防止DNS的方式参见：[http://code4craft.github.com/blog/2013/02/25/blackhole-anti-dns-poison/](http://code4craft.github.com/blog/2013/02/25/blackhole-anti-dns-poison/)


###3. 安装及配置

你使用自动脚本进行安装BlackHole：

	curl http://code4craft.github.io/blackhole/install.sh | [sudo] sh

BlackHole的另一个编译后版本保存在[https://github.com/code4craft/blackhole-bin](https://github.com/code4craft/blackhole-bin)，如果以上脚本对你所在环境不可用，那么可以clone这个项目到某一目录。

	git clone https://github.com/code4craft/blackhole-bin.git /usr/local/blackhole
	
通过sudo /usr/local/blackhole/blackhole.sh start可以启动BlackHole。

Windows系统可将文件保存到任意目录，并运行start.bat(Win7下无需用管理员权限启动)，若弹出终端界面并且持续运行，则启动成功。

各种问题解决、具体的设置以及技术细节请看[Blackhole Server Docs](https://github.com/code4craft/blackhole/blob/master/server/README.md)。

###4. 协议

BlackHole的连接部分参考了EagleDNS的代码，遵守LGPLv3协议。

作者邮箱：
code4crafter@gmail.com

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/code4craft/blackhole/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

