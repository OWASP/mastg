#!/bin/bash
# List the broadcast receivers declared as exported in the AndroidManifest.
python3 - <<'PY' > output.txt
import re
xml = open("AndroidManifest_reversed.xml").read()
for m in re.finditer(r"<receiver\b.*?(?:/>|</receiver>)", xml, re.S):
    block = m.group(0)
    name = re.search(r'android:name="([^"]+)"', block)
    exported = re.search(r'android:exported="([^"]+)"', block)
    permission = re.search(r'android:permission="([^"]+)"', block)
    if exported and exported.group(1) == "true":
        print("Exported receiver:", name.group(1) if name else "?",
              "| permission:", permission.group(1) if permission else "none")
PY
