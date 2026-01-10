#!/bin/bash

hookPath=$1
hook=$(cat "$hookPath")
decoderScript=$(cat "$(dirname $0)"/android_decoder.js)
fridaScript=$(cat "$(dirname $0)"/base_script.js)
randomNumber=$RANDOM

# use project-local temp directory instead of global /tmp
workDir="$(pwd)/.tmp"
scriptPath="$workDir/frida_script_$randomNumber.js"

# ensure temp directory exists
mkdir -p "$workDir"


# merging the different parts of the frida.re scripts and writing it to a temporary file
{
  echo "$hook"
  echo $'\n'
  echo "$decoderScript"
  echo $'\n'
  echo "$fridaScript"
}   > "$scriptPath"


# run the merged frida.re script
frida -U -f org.owasp.mastestapp -l "$scriptPath" -o output.json

# cleanup
rm "$scriptPath"
