#!/bin/sh
HOME_DIR=/usr/local/blackhole
PATH=$PATH:$HOME_DIR
export PATH

function doCache(){
  case "$1" in
      stat)
        java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -cstat_cache
        ;;
      dump)
        echo "Dump cache to $HOME_DIR/cache.dump"
        java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -cdump_cache > /dev/null
        ;;
      clear)
        echo "Clear cache"
        java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -cclear_cache
        ;;
      *)
    echo "Usage: $0 cache {dump|clear|stat}"
    ;;
  esac
}

case "$1" in
  start)
    echo "Starting blackhole..."
    java -jar -Djava.io.tmpdir="$HOME_DIR/cache" $HOME_DIR/blackhole.jar -d"$HOME_DIR">> $HOME_DIR/log &
    ;;
  stop)
    echo "Stopping blackhole"
    java -jar $HOME_DIR/lib/wifesays-1.0.0-alpha.jar -cshutdown > /dev/null
    ;;
  cache)
    doCache $2
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
    echo "Usage: $0 {start|stop|reload|zones|config|restart|cache}"
    ;;
esac

exit 0
