#!/bin/bash
frida -U -f org.owasp.mastestapp -l bypass.js -o output.txt
