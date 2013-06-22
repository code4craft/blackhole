#!/bin/sh
HOME_DIR=/usr/local/blackhole/
VERSION=1.2.1
BLACK_HOLE_URL=http://code4craft.qiniudn.com/blackhole-${VERSION}.tar.gz
mkdir -p ${HOME_DIR}
cd ${HOME_DIR}
echo "start to download ${BLACK_HOLE_URL}"
curl ${BLACK_HOLE_URL} > blackhole-${VERSION}.tar.gz
tar -xzf blackhole-${VERSION}.tar.gz
rm -f blackhole-${VERSION}.tar.gz
echo "blackhole is installed to ${HOME_DIR}"
echo "Try '[sudo] /usr/local/blackhole/blackhole.sh start' to start it!"
