#!/bin/sh
mkdir -p /usr/local/blackhole/
mkdir -p /usr/local/blackhole/lib
if [ ! -d /usr/local/blackhole/config ]
then
mkdir -p /usr/local/blackhole/config
cp ./server/config/* /usr/local/blackhole/config/
fi
cp ./server/blackhole.sh /usr/local/blackhole/
cp ./server/target/blackhole*.jar /usr/local/blackhole/blackhole.jar
rsync -avz --delete ./server/target/lib/ /usr/local/blackhole/lib/
