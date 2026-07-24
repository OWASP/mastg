#!/bin/bash

PACKAGE="org.owasp.mastestapp"
APP_DIR="/data/data/$PACKAGE/files/datastore"
OUTPUT_TXT_FILE="output.txt"

echo "[*] Pulling DataStore files directly from app sandbox..."
echo "--- DataStore Extracted Content ---" > "$OUTPUT_TXT_FILE"

# Wyciągamy wszystkie pliki bezpośrednio do strings
for f in $(adb exec-out run-as $PACKAGE ls "$APP_DIR"); do
    echo "[FILE] $f" >> "$OUTPUT_TXT_FILE"
    adb exec-out run-as $PACKAGE cat "$APP_DIR/$f" | strings >> "$OUTPUT_TXT_FILE"
    echo >> "$OUTPUT_TXT_FILE"
done

echo "[*] Output written to $OUTPUT_TXT_FILE"
