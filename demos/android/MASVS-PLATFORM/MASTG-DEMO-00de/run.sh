NO_COLOR=true semgrep -c ../../../../rules/mastg-android-manifest-cleartext.yml ./AndroidManifest_reversed.xml > output.txt

NO_COLOR=true semgrep -c ../../../../rules/mastg-android-webview-bridges.yml ./MastgTestWebView_reversed.java --max-lines-per-finding 20 > output2.txt