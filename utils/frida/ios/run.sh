#!/bin/bash

hookPath=$1
hook=$(cat "$hookPath")
fridaScript=$(cat "$(dirname $0)"/base_script.js)
randomNumber=$RANDOM

# merging the different parts of the frida.re scripts and writing it to a temporary file
{
  echo "$hook"
  echo $'\n'
  echo "$fridaScript"
}  > /tmp/frida_script_$randomNumber.js

# run the merged frida.re script
frida -U -f org.owasp.mastestapp.MASTestApp-iOS -l /tmp/frida_script_$randomNumber.js -o output.json
# frida -n MASTestApp -l /tmp/frida_script_$randomNumber.js -o output.json

# cleanup
rm /tmp/frida_script_$randomNumber.js
