#!/bin/bash
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-fileprovider-broad-scope.yml ./filepaths_reversed.xml --text | sed -r "s/\x1B\[([0-9]{1,3}(;[0-9]{1,2})?)?[mGK]//g" > output.txt