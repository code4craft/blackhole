#!/bin/sh
cp ../server/target/blackhole*.jar /usr/local/blackhole/blackhole.jar
rsync -avz --delete ../server/target/lib/ /usr/local/blackhole/lib/