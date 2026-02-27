#!/bin/bash

plutil -convert json -o Info_reversed.json Info_reversed.plist

# pretty print json
jq . Info_reversed.json > Info_reversed.json.tmp && mv Info_reversed.json.tmp Info_reversed.json

gron -m Info_reversed.json | grep 'TSKExpirationDate' > output.txt
