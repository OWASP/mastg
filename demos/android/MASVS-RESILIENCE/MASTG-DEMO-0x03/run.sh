#!/bin/bash
frida -U -f org.owasp.mastestapp -i "open" -o frida-output-instrumented.txt
