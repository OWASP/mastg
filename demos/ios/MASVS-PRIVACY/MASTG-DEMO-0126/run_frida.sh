#!/bin/bash
frida -U -f org.owasp.mastestapp.MASTestApp-iOS -l ./script.js -o output_frida.txt
