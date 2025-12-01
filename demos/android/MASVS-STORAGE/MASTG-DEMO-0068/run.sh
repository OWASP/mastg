#!/bin/bash

PACKAGE="org.owasp.mastestapp"
DB_NAME="PrivateUnencryptedData"
DB_PATH="/data/data/$PACKAGE/databases/$DB_NAME"
SDCARD_PULL_PATH="/sdcard/$DB_NAME.db"
OUTPUT_DB_FILE="${DB_NAME}.db"
OUTPUT_TXT_FILE="output.txt"

# Copy Database to the sdcard
if ! adb shell "run-as $PACKAGE cp databases/$DB_NAME $SDCARD_PULL_PATH"; then
  echo "run-as failed, trying su"
  adb shell "su 0 sh -c 'cp $DB_PATH $SDCARD_PULL_PATH'"
fi

# Pull the file to the host machine
adb pull "$SDCARD_PULL_PATH" .

echo "--- Users Table Content ---" > $OUTPUT_TXT_FILE
sqlite3 "$OUTPUT_DB_FILE" "SELECT * FROM Users;" >> $OUTPUT_TXT_FILE

adb shell "rm $SDCARD_PULL_PATH" 2>/dev/null
rm $OUTPUT_DB_FILE 2>/dev/null
