#!/usr/bin/env bash
set -euo pipefail

NO_COLOR=true semgrep -c ../../../../rules/mastg-android-native-debugger-checks.yml ./MastgTest_reversed.java --text > output.txt
r2 -q -e bin.relocs.apply=true -e log.quiet=true -i native_debugger_checks.r2 -A libtracerpidcheck.so >> output.txt
