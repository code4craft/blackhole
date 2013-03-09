#!/bin/sh
HOME_DIR=/usr/local/blackhole
PATH=$PATH:$HOME_DIR
export PATH

case "$1" in
  start)
    echo "Starting blackhole..."
    java -jar -Djava.io.tmpdir="$HOME_DIR" $HOME_DIR/blackhole.jar -d"$HOME_DIR">> $HOME_DIR/log &
    ;;
  stop)
    echo "Stopping blackhole"
    java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -cshutdown > /dev/null
    ;;
  restart)
    echo "Stopping blackhole..."
    java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -cshutdown > /dev/null;
    sleep 2;
    echo "Starting blackhole..."
    java -jar $HOME_DIR/blackhole.jar -d"$HOME_DIR">> $HOME_DIR/log &
    ;;
  reload)
    echo "Reloading blackhole"
    java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -creload > /dev/null
    ;;
  zones)
    vi $HOME_DIR/config/zones
    java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -creload > /dev/null
    ;;
  config)
    vi $HOME_DIR/config/blackhole.conf
    java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -creload > /dev/null
    ;;
  *)
    echo "Usage: $0 {start|stop|reload|zones|config|restart}"
    ;;
esac

exit 0
