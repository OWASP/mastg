#!/bin/bash

plutil -convert xml1 -o Info_reversed.plist Info_reversed.plist
plutil -convert json -o Info_reversed.json Info_reversed.plist

# pretty print json
jq . Info_reversed.json > Info_reversed.json.tmp && mv Info_reversed.json.tmp Info_reversed.json

jq '.NSAppTransportSecurity.NSPinnedDomains' Info_reversed.json > ats_pinned_domains.json

rabin2 -z MASTestApp | grep http > output.txt

