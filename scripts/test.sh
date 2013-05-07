#!/bin/sh
blackhole stop
sleep 1
blackhole start
nslookup www.baidu.com 127.0.0.1