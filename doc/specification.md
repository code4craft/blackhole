
BlackHole技术说明
---


BlackHole的UDP连接管理模块参考了开源项目EagelDNS的技术实现，服务器处理策略借鉴了tomcat的connector和container机制，以及jetty的handler方式。DNS协议的解析方面使用了dnsjava包。


--------------------
####拦截模式：

<table>
    <tr>
        <td width="100">Type</td>
        <td width="100">Support</td>
        <td>Description</td>
    </tr>
    <tr>
        <td>A</td>
        <td>yes</td>
        <td>可配置</td>
    </tr>
    <tr>
        <td>AAAA</td>
        <td>no</td>
        <td>IPV6尚未支持</td>
    </tr>
    <tr>
        <td>PTR</td>
        <td>yes</td>
        <td>使用最近一次的A解析记录的结果作为参照。</td>
    </tr>
    <tr>
        <td>MX</td>
        <td>yes</td>
        <td>在请求的名字前加上mail.伪造host，然后将mail.name指向配置好的IP。</td>
    </tr>
        
</table>

---------------------------

####转发模式：

转发模式目前不负责解析请求。后期将引入缓存，缓存策略为缓存逻辑对象而不是UDP报文。下面列表是TODO List。

<table>
    <tr>
        <td width="100">Type</td>
        <td width="100">Support</td>
        <td>Description</td>
    </tr>
    <tr>
        <td>A</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>AAAA</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>PTR</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>MX</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>NS</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>CNAME</td>
        <td>no</td>
        <td></td>
    </tr>
</table>
