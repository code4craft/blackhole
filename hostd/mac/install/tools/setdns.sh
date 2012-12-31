#!/bin/bash

if [ $# -lt 1 ] ; then
echo "Use: $0 <1.Nameserver> [2.Nameserver]"
echo "Example Use: $0 1.2.3.4 1.2.3.5"
exit 1
fi

PSID=$( (scutil | grep PrimaryService | sed -e 's/.*PrimaryService : //')<< EOF
open
get State:/Network/Global/IPv4
d.show
quit
EOF
)

scutil << EOF
open
d.init
d.add ServerAddresses * $1 $2 
set State:/Network/Service/$PSID/DNS
quit
EOF