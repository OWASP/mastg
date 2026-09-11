#!/bin/bash
frida -U -f org.owasp.mastestapp.MASTestApp-iOS -l ./bypass.js -o output.txt
