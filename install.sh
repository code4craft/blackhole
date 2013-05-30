#!/bin/sh
HOME_DIR=/usr/local/blackhole/
BLACK_HOLE_URL=http://code4craft.qiniudn.com/blackhole-1.2.0.tar.gz
mkdir -p ${HOME_DIR}
cd ${HOME_DIR}
echo "start to download ${BLACK_HOLE_URL}"
curl ${BLACK_HOLE_URL} > blackhole-1.2.0.tar.gz
tar -xzf blackhole-1.2.0.tar.gz
rm -f blackhole-1.2.0.tar.gz
echo "blackhole is installed to ${HOME_DIR}"
