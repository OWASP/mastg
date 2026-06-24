#!/bin/bash
# SUMMARY: This script uses Frooky (MASTG-TOOL-0145) to perform dynamic instrumentation with Frida.
frooky -U -f org.owasp.mastestapp --platform android -o output.json hooks.json
