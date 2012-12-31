#!/bin/bash

function checkUser()
{
  if [ `whoami` != "root" ]; then
   echo "Must run as root."
   exit 1;
  fi
}


DNS_FILE=/usr/local/hostd/tools/dns

checkUser;

if [ -f $DNS_FILE ] ; then
read line </usr/local/hostd/tools/dns	
PSID=$( (scutil | grep PrimaryService | sed -e 's/.*PrimaryService : //')<< EOF
open
get State:/Network/Global/IPv4
d.show
quit
EOF
)

echo "reset $PSID dns to "$line

scutil << EOF
open
d.init
d.add ServerAddresses * $line
set State:/Network/Service/$PSID/DNS
quit
EOF

dscacheutil -flushcache

fi
