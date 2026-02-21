#!/bin/bash

plutil -convert json -o Info_reversed.json Info_reversed.plist

# pretty print json
jq . Info_reversed.json > Info_reversed.json.tmp && mv Info_reversed.json.tmp Info_reversed.json

gron -m Info_reversed.json \
| egrep 'json\.NSAppTransportSecurity\.(NSAllowsArbitraryLoads|NSAllowsArbitraryLoadsInWebContent|NSAllowsArbitraryLoadsForMedia|NSAllowsArbitraryLoadsForLocalNetworking|NSExceptionDomains\["[^"]+"\]\.(NSExceptionAllowsInsecureHTTPLoads|NSTemporaryExceptionAllowsInsecureHTTPLoads|NSAllowsArbitraryLoads))' \
| egrep ' = (true|"true"|1|"1");$' > output.txt
