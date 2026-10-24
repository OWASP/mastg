#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-biometric-event-bound.yml ../MASTG-DEMO-0090/MastgTest_reversed.java --text | sed -r "s/\x1B\[([0-9]{1,3}(;[0-9]{1,2})?)?[mGK]//g" > output.txt

# Using sed to remove the escaped ANSI code for text formatting (specifically for bold text). This happens because semgrep is outputting colored/formatted text even though it's redirected to a file. `NO_COLOR=true` should prevent this, but in case it doesn't, the sed command will clean the output.
