#!/bin/bash

plutil -convert json -o Info_reversed.json Info_reversed.plist

# Format JSON output
jq . Info_reversed.json > Info_reversed.json.tmp && mv Info_reversed.json.tmp Info_reversed.json

gron -m Info_reversed.json \
| egrep 'json\.NSAppTransportSecurity\.(NSAllowsArbitraryLoads|NSExceptionDomains\["[^"]+"\]\.(NSExceptionMinimumTLSVersion|NSTemporaryExceptionMinimumTLSVersion|NSExceptionRequiresForwardSecrecy))' \
> output.txt
