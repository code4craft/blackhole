BlackHole
=========

### 1. 技术结构

BlackHole的连接处理部分参考了EagleDNS，使用了反应堆模式。

BlackHole底层使用dnsjava进行DNS解析，并使用EhCache进行缓存和持久化。

BlackHole的UDP代理部分使用了纯异步的逻辑，支持同时代理多个外部DNS服务器，并使用最快的响应结果。

### 2. 安装

#### 下载编译版本

BlackHole的编译后版本保存在[https://github.com/code4craft/blackhole-bin](https://github.com/code4craft/blackhole-bin)，直接clone这个项目到某一目录即可。

	git clone https://github.com/code4craft/blackhole-bin.git /usr/local/blackhole

你也可以使用自动脚本进行安装：

	curl http://code4craft.github.io/blackhole/install.sh | sh

#### 从源码编译

你也可以使用源码，进行修改和编译。

	git clone https://github.com/code4craft/blackhole.git --recursive
	cd blackhole
	mvn clean package
	sh make.sh	
	
### 3. 启动

然后通过sudo /usr/local/blackhole/blackhole.sh start可以启动。

如果启动时提示53端口被占用，可以查看一下是否已在本地开启其他dns服务。

Ubuntu下默认开启了dnsmasq，如果启动时提示53端口被占用，可以查看dnsmasq是否已开启：

	ps -ef | grep dnsmasq
	
禁用dnsmasq的方法：修改/etc/NetworkManager/NetworkManager.conf，注释掉dns=dnsmasq即可。

### 4. 配置BlackHole:

如果你没有将BlackHole安装到/usr/local/blackhole，则需要修改blackhole.sh，将HOME_DIR更改为你的安装目录。

BlackHole目前有两个配置文件，分别是**config/blackhole.conf**和**config/zones**。
**BlackHole的配置文件都是修改后动态生效的！**

#### conf/blackhole.conf

是blackhole的主要配置文件。

配置格式如下：

	key=value

例如：

	DNS=192.168.0.1
	
目前可配置项如下：

* **DNS**
	
	BlackHole并没有递归查找DNS的功能，如果遇到未在本地配置的域名请求，它会做一个UDP代理的工作，将请求发向一个已有的DNS服务器，并返回这个DNS服务器的结果。
	
	支持多个DNS服务器配置，BlackHole会优先采用较前面的配置的结果。**如果你所在的内网配置了DNS拦截，将你的内网DNS服务器放到第一位可以保证这些拦截优先生效。**

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
	
#### conf/zones

zones是DNS系统的域名配置文件。BlackHole简化了zones的配置，只支持A记录(因为作者觉得A记录够用了！)。

BlackHole的配置跟Hosts文件是一样的，但是支持通配符"*"。

例如：

	127.0.0.1	*.codecraf.us

*可以匹配任意长度的任意字符(包括'.')。这项配置可以将所有以.codecraf.us形式结尾的域名全部指向127.0.0.1。

当然，你也可以这样配置：

	127.0.0.1	*

这表示把所有请求导向本地。这个配置在某些场合(例如测试邮件发送服务)会有用。

BlackHole还支持NS记录的配置。NS记录的意思是，对于某些域名的请求，总是向某个DNS服务器查找结果。例如，你可以使用组合配置：

	173.194.72.103 *.google.com
	NS 8.8.8.8 docs.google.com

这两项配置的意思是：将*.google.com的地址都指向173.194.72.103，但是对于docs.google.com域名，向8.8.8.8进行查询并作为最终结果。

如果两条规则出现冲突，前面的规则会生效。

### 5. 动态管理BlackHole：

blackhole的监控模块使用了作者的另一个开源项目[wifesays](https://github.com/flashsword20/wifesays)。wifesays是一个简单的Java进程内TCP服务器，使用40310端口作为监控端口，基于TCP协议上封装了一些简单的文本命令。

可以使用
	
	java -jar wifesays.jar -cCOMMAND

来通知BlackHole。你也可以直接用telnet来管理BlackHole：
	
	telnet 127.0.0.1 40310
	>COMMAND

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
	
这些操作都集成到了blackhole.sh脚本中。支持SHELL的系统可以使用以下命令快速操作：
	
	blackhole.sh {start|stop|restart|reload|zones|config|cache}

### 6. 协议

BlackHole的连接部分参考了EagleDNS的代码，遵守LGPLv3协议。

作者邮箱：
code4crafter@gmail.com
