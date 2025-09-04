# shellcheck disable=SC2148
NO_COLOR=true semgrep -c ../../../../rules/mastg-android-autoverify-missing.yml ../MASTG-DEMO-0062/AndroidManifest_reversed.xml --text -o output.txt