---
title: Analyzing the AndroidManifest
platform: android
---

Once you've extracted the `AndroidManifest.xml` as described in @MASTG-TECH-0117, you can analyze its content to look for specific attributes, flags, permissions, or component declarations.

Note that the output format differs depending on the extraction tool used:

- Tools like jadx and apktool output **standard XML** where attributes follow the `android:` namespace prefix (e.g., `android:debuggable="true"`).
- Tools like aapt2 output a **custom decoded format** (not XML) that uses different naming conventions (e.g., `application-debuggable`).

## Using grep

Use `grep` to search for a specific attribute or flag in the XML output:

```bash
grep -i "android:debuggable" output_dir/AndroidManifest.xml
```

Example output when the flag is set:

```xml
android:debuggable="true"
```

If the attribute is absent, the flag defaults to `false` for release builds.

## Using aapt2

Use aapt2 to query the manifest without extracting it first:

```bash
aapt2 d badging app.apk | grep -i debuggable
```

Example output when the flag is set:

```txt
application-debuggable
```

If the line is absent, the flag is not set (defaults to `false`).

## Using xmllint or xmlstarlet

For structured XML queries, use `xmllint` or `xmlstarlet` on the extracted XML manifest:

```bash
xmlstarlet sel -t -v "//application/@android:debuggable" -n output_dir/AndroidManifest.xml
```
