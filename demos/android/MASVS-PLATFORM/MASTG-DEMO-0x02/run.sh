#!/bin/bash

# Spawn the sample app and capture the Compose sensitivity hook and semantics tree.
frida -U -f org.owasp.mastestapp -l ./script.js -o output.txt
