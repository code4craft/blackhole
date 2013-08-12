Release Notes
----
*2012-7-16* `version：1.2.2`

极大优化了A记录配置的性能。

修复数万条配置的情况下，性能明显下降的问题[issue#9](https://github.com/code4craft/blackhole/issues/9)。

新增NS格式的配置。

*2012-6-22* `version：1.2.1`

增加自定义缓存过期时间的功能。

优化了缓存目录地址。

修复了缓存持久化不生效的bug。

详细进行了性能测试，并更新了文档。

*2012-5-31* `version：1.2.0`

* [issues#5](https://github.com/code4craft/blackhole/issues/5) 对不同的用户组提供不同的DNS响应，从而可以让每个用户管理自己的DNS配置。

* 开发了一个Web模块[Hostd](http://code4craft.github.io/hostd/)，类似修改hosts，可以供所有用户进行自己DNS配置的管理。

* [issues#8](https://github.com/code4craft/blackhole/issues/8) 对forward的外部DNS设定优先级 便于企业内网配置自己的DNS地址，防止被响应被覆盖。

*2012-5-7*

* 修复启动失败后进程不退出的bug。

*2012-4-27* `version：1.1.3`

1.1.3发布。

* 重写了代理模式，​改为纯异步I/O实现，大大降低使用线程数，并提高了25%的效率。

* 修复一个高并发下某些响应丢失的bug。

*2012-4-2* `version：1.1.2`

1.1.2发布，修复了一个返回空响应体导致DNS查找失败的问题，从此稳定性大大提高[https://github.com/code4craft/blackhole/issues/3](https://github.com/code4craft/blackhole/issues/3)。

BlackHole也迎来第一位企业级用户。争取发展成为一个公司内网使用的简单可配置的DNS服务器。

*2012-3-24* `version：1.1.1`

1.1.1发布，增加NS配置功能，详情见[https://github.com/code4craft/blackhole/blob/master/server/README.md](https://github.com/code4craft/blackhole/blob/master/server/README.md)

*2012-2-25*	`version: 1.1`

1.1发布，偷偷增加反DNS污染功能。原理：
[http://my.oschina.net/flashsword/blog/110276](http://my.oschina.net/flashsword/blog/110276)

*2012-12-31* `version: 1.0-alpha`

1.0-alpha发布，加入功能：

* DNS转发代理，并可缓存结果
* 可配置多个转发服务器
* 增加超时机制

*2012-12-17* `version: 0.1 `

为了满足模拟邮件发送的需求，开发了第一个版本。可以拦截所有请求到某一IP。从EagelDNS的框架修改而来。



