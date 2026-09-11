#!/bin/bash
# List the services declared as exported in the AndroidManifest.
python3 - <<'PY' > output.txt
import re
xml = open("AndroidManifest_reversed.xml").read()
for m in re.finditer(r"<service\b.*?(?:/>|</service>)", xml, re.S):
    block = m.group(0)
    name = re.search(r'android:name="([^"]+)"', block)
    exported = re.search(r'android:exported="([^"]+)"', block)
    permission = re.search(r'android:permission="([^"]+)"', block)
    if exported and exported.group(1) == "true":
        print("Exported service:", name.group(1) if name else "?",
              "| permission:", permission.group(1) if permission else "none")
PY
