#!/bin/sh
HOME_DIR=/usr/local/blackhole
PATH=$PATH:$HOME_DIR
export PATH


case "$1" in
  start)
    echo "Starting blackhole"
    nohup java -jar $HOME_DIR/blackhole.jar >> $HOME_DIR/log 2>&1 &
    ;;
  stop)
    echo "Stopping blackhole"
    java -jar $HOME_DIR/wifesays.jar -cshutdown > /dev/null
    ;;
  reload)
    echo "Reload blackhole"
    java -jar $HOME_DIR/wifesays.jar -creload > /dev/null
    ;;
  *)
    echo "Usage: $0 {start|stop|reload}"
    ;;
esac

exit 0
