NO_COLOR=true semgrep -c ../../../../rules/mastg-android-data-unencrypted-shared-storage-no-user-interaction-apis.yml ./MastgTest_reversed.java > output.txt

NO_COLOR=true semgrep -c ../../../../rules/mastg-android-data-unencrypted-shared-storage-no-user-interaction-manifest.yml ./AndroidManifest_reversed.xml > output2.txt
