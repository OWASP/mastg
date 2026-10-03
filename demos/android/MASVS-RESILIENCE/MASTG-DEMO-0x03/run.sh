#!/bin/bash
frida -U -f org.owasp.mastestapp -i "open" -o output.txt
