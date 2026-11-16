#!/bin/bash

# Spawn the app with Frida, hook the accessibility data sensitivity setters, and dump the view hierarchy
frida -U -f org.owasp.mastestapp -l ./script.js -o output.txt
