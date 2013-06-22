#!/bin/sh
grep -F '<constructor-arg name="port" type="int" value="53">' ../server/src/main/resources/spring/applicationContext-blackhole.xml
/usr/local/blackhole/blackhole.sh stop
sleep 1
/usr/local/blackhole/blackhole.sh start
nslookup www.baidu.com 127.0.0.1
