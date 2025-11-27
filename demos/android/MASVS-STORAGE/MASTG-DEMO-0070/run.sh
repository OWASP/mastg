#!/bin/bash

PACKAGE="org.owasp.mastestapp"
DB_NAME="PrivateUnencryptedRoomDB"
DB_PATH="/data/data/$PACKAGE/databases"
SDCARD_DIR="/sdcard/$PACKAGE-db"
OUTPUT_TXT_FILE="output.txt"

echo "[*] Pulling RoomDB (WAL mode)..."

adb shell "rm -rf $SDCARD_DIR && mkdir -p $SDCARD_DIR"

for f in "$DB_NAME" "$DB_NAME-wal" "$DB_NAME-shm"; do
    if adb shell "run-as $PACKAGE cp $DB_PATH/$f $SDCARD_DIR/"; then
        echo "[*] run-as succeeded for $f"
    else
        echo "[*] run-as failed for $f, trying su"
        adb shell "su 0 sh -c 'cp $DB_PATH/$f $SDCARD_DIR/'"
    fi
done

adb pull "$SDCARD_DIR/." .

echo "--- Users Table Content ---" > "$OUTPUT_TXT_FILE"
sqlite3 "$DB_NAME" "SELECT * FROM users;" >> "$OUTPUT_TXT_FILE"

adb shell "rm -rf $SDCARD_DIR" 2>/dev/null
rm "$DB_NAME" "$DB_NAME-wal" "$DB_NAME-shm" 2>/dev/null

echo "[*] Output written to $OUTPUT_TXT_FILE"
