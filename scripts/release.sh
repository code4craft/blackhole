#!/bin/sh
cp ../server/target/blackhole*.jar ../server/bin/blackhole.jar
rsync -avz --delete ../server/target/lib/ ../server/bin/lib/