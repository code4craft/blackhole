#!/bin/sh
HOME_DIR=/usr/local/hostd
PATH=$PATH:$HOME_DIR
VERSION="1.0.0-alpha"
export PATH
export HOME_DIR

function checkUser()
{
  if [ `whoami` != "root" ]; then
   echo "Must run as root."
   exit 1;
  fi
}

function startIfNot()
{
    java -jar $HOME_DIR/bin/lib/wifesays-${VERSION}.jar -cstatus > /dev/null
    if [ $? -ne 0 ]; then
      nohup java -jar $HOME_DIR/bin/blackhole-localserver-${VERSION}.jar -d$HOME_DIR 1>>$HOME_DIR/log/`date "+%Y-%m-%d"`.log 2>$HOME_DIR/log/error.log &
    fi
}


if [ $# -lt 1 ] ; then
    checkUser;
    startIfNot;
    vim /etc/hostd;
else 
  case "$1" in
  start)
    checkUser;
    echo "Starting hostd..."
    nohup java -jar $HOME_DIR/bin/blackhole-localserver-${VERSION}.jar -d$HOME_DIR 1>>$HOME_DIR/log/`date "+%Y-%m-%d"`.log 2>$HOME_DIR/log/error.log &
    sleep 0.5
    ;;
  stop)
    echo "Stopping hostd..."
    java -jar $HOME_DIR/bin/lib/wifesays-${VERSION}.jar -cshutdown > /dev/null
    ;;
  *)
    echo "Usage: $0 [start|stop]"
    ;;
esac
fi

if [ -f $HOME_DIR/log/error.log ] ; then
  cat $HOME_DIR/log/error.log
  rm $HOME_DIR/log/error.log
fi

exit 0
