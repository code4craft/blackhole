#!/bin/sh
HOME_DIR=/usr/local/blackhole/
SCRIPT_URL=http://code4craft.github.io/blackhole/install.sh
VERSION=1.2.1
BLACK_HOLE_URL=http://code4craft.qiniudn.com/blackhole-${VERSION}.tar.gz
UPDATE=0

mkdir -p ${HOME_DIR}
if [ $? == 1 ]
then
echo "no permission! try 'curl ${SCRIPT_URL} | sudo sh' or login as root!"
exit 1
fi
cd ${HOME_DIR}
echo "start to download ${BLACK_HOLE_URL}"
curl ${BLACK_HOLE_URL} > blackhole-${VERSION}.tar.gz

if [ -d $HOME_DIR/config ]
then 
	UPDATE=1
	mv $HOME_DIR/config $HOME_DIR/configbak
fi

tar -xzf blackhole-${VERSION}.tar.gz
rm -f blackhole-${VERSION}.tar.gz

if [ $UPDATE == 1 ]
then
rm -rf $HOME_DIR/config
mv $HOME_DIR/configbak $HOME_DIR/config
fi

echo "blackhole is installed to ${HOME_DIR}"
echo "Try '[sudo] /usr/local/blackhole/blackhole.sh start' to start it!"
