#!/bin/bash

PSID=$( (scutil | grep PrimaryService | sed -e 's/.*PrimaryService : //')<< EOF
open
get State:/Network/Global/IPv4
d.show
quit
EOF
)

scutil << EOF
open
get State:/Network/Service/$PSID/DNS
d.show
quit
EOF
