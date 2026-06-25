#!/bin/bash
frida -U -f org.owasp.mastestapp -l script.js --no-pause > output.txt 2>&1
