#!/bin/sh
HOME_DIR=/usr/local/blackhole
PATH=$PATH:$HOME_DIR
export PATH

case "$1" in
  start)
    echo "Starting blackhole"
    java -jar $HOME_DIR/blackhole.jar -d$HOME_DIR >> $HOME_DIR/log &
    ;;
  stop)
    echo "Stopping blackhole"
    java -jar $HOME_DIR/wifesays.jar -cshutdown > /dev/null
    ;;
  reload)
    echo "Reloading blackhole"
    java -jar $HOME_DIR/wifesays.jar -creload > /dev/null
    ;;
  zones)
    vi $HOME_DIR/config/zones
    java -jar $HOME_DIR/wifesays.jar -creload > /dev/null
    ;;
  *)
    echo "Usage: $0 {start|stop|reload|zones}"
    ;;
esac

exit 0
