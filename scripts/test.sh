#!/bin/sh
grep -F '<constructor-arg name="port" type="int" value="53">' ../server/src/main/resources/spring/applicationContext-blackhole.xml
blackhole stop
sleep 1
blackhole start
nslookup www.baidu.com 127.0.0.1
