#!/bin/bash

# Configuration
PACKAGE="org.owasp.mastestapp"
DB_NAME="PrivateUnencryptedRoomDB"
DB_PATH="databases"
OUTPUT_TXT_FILE="output.txt"

echo "Extracting Room database files from sandbox..."

for f in "$DB_NAME" "$DB_NAME-wal" "$DB_NAME-shm"; do
    if adb exec-out run-as "$PACKAGE" cat "$DB_PATH/$f" > "$f" 2>/dev/null; then
        echo "[+] Extracted $f ($(wc -c < "$f") bytes)"
    else
        echo "[-] Failed to extract $f"
    fi
done

echo "Inspecting database for plaintext sensitive data..."

if [ -f "$DB_NAME" ]; then
    echo "--- OBSERVATION: Unencrypted Room Database Content ---" > "$OUTPUT_TXT_FILE"

    echo "Querying 'users' table..."
    sqlite3 "$DB_NAME" "SELECT * FROM users;" >> "$OUTPUT_TXT_FILE"

    echo "[+] Evidence collected in $OUTPUT_TXT_FILE:"
    cat "$OUTPUT_TXT_FILE"
else
    echo "ERROR: Database file could not be retrieved."
    echo "Ensure the app is installed, debuggable, and has performed a write operation."
    exit 1
fi

rm "$DB_NAME" "$DB_NAME-wal" "$DB_NAME-shm" 2>/dev/null

echo ""
echo "[*] Evaluation: Test case fails if sensitive data (tokens/PII) is visible in plaintext above."
