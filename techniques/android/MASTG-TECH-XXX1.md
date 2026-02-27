---
title: Interacting with Android ContentProviders via the ADB Shell Content Command
platform: android
---

Android `ContentProvider`s make structured data available to other applications through `content://` URIs. They specify an authority (a unique identifier), one or more paths (tables/resources), and carry out CRUD operations (`query`, `insert`, `update`, `delete`). Users interact with them via `ContentResolver` or directly from the device shell. The provider's accessibility depends on its `exported` setting and any permissions declared in the application's manifest.

## How ContentProviders Work

- Interface for cross-app data access and IPC on Android.
- Identified by a URI: `content://<authority>/<path>` or `content://<authority>/<path>/<id>`.
- Backed by storage such as SQLite; many apps use `SQLiteQueryBuilder` in `query`.
- Access control via `android:exported` and read/write permissions; signature-level permissions can restrict access to trusted apps only.

## Using Content query

Use @MASTG-TOOL-0004 to interact with providers on a device or emulator via the `content` command:

- Query rows

```bash
adb shell content query --uri content://org.owasp.mastestapp.provider/students
adb shell content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob'"
```

- Insert a row

```bash
adb shell content insert \
    --uri content://org.owasp.mastestapp.provider/students \
    --bind name:s:"Eve"
```

- Update rows

```bash
adb shell content update \
    --uri content://org.owasp.mastestapp.provider/students \
    --where "id=1" \
    --bind name:s:"Alice Jr"
```

- Delete rows

```bash
adb shell content delete --uri content://org.owasp.mastestapp.provider/students --where "id=3"
```
