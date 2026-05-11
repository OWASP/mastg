#!/bin/bash

# Configuration 
PACKAGE="org.owasp.mastestapp"
DB_NAME="PrivateUnencryptedRoomDB"
DB_PATH="/data/data/$PACKAGE/databases"
SDCARD_DIR="/sdcard/$PACKAGE-db"
OUTPUT_TXT_FILE="output.txt"


# To ensure test reproducibility, the script triggers the application launch.
# This forces the Room database initialization and initial data write.
echo "Launching app to trigger database write..."
adb shell am start -n "$PACKAGE/.MainActivity"
sleep 5

echo "Extracting Room database files from sandbox..."
adb shell "rm -rf $SDCARD_DIR && mkdir -p $SDCARD_DIR"

# Loop to extract the main DB file and the WAL/SHM journal files
for f in "$DB_NAME" "$DB_NAME-wal" "$DB_NAME-shm"; do
    # Primary attempt: using run-as for debuggable builds
    if adb shell "run-as $PACKAGE cp $DB_PATH/$f $SDCARD_DIR/" 2>/dev/null; then
        echo "[+] Extracted $f via run-as"
    else
        # Fallback: using su for rooted testing environments
        echo "[-] run-as failed for $f, attempting fallback with su..."
        adb shell "su 0 sh -c 'cp $DB_PATH/$f $SDCARD_DIR/'" 2>/dev/null
    fi
done

# Pull files to the host machine for offline analysis
adb pull "$SDCARD_DIR/." . > /dev/null 2>&1

#Verification of plaintext storage.
echo "Inspecting database for plaintext sensitive data..."

if [ -f "$DB_NAME" ]; then
    echo "Unencrypted Room Database Content" > "$OUTPUT_TXT_FILE"
    
    # Querying the database to provide forensic evidence of the vulnerability.
    # This proves the lack of encryption (e.g., absence of SQLCipher).
    echo "Querying 'users' table..."
    sqlite3 "$DB_NAME" "SELECT * FROM users;" >> "$OUTPUT_TXT_FILE"
    
    echo "[+] Evidence collected in $OUTPUT_TXT_FILE:"
    cat "$OUTPUT_TXT_FILE"
else
    echo "ERROR: Database file could not be retrieved."
    echo "Ensure the app is installed, debuggable, and has performed a write operation."
    exit 1
fi

# Remove temporary files from the device and local environment.
adb shell "rm -rf $SDCARD_DIR" 2>/dev/null
rm "$DB_NAME" "$DB_NAME-wal" "$DB_NAME-shm" 2>/dev/null

echo ""
echo "[*] Evaluation: Test case fails if sensitive data (tokens/PII) is visible in plaintext above."
