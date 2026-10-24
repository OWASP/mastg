---
title: Interacting with Android ContentProviders
platform: android
---

See @MASTG-KNOW-0117 for an overview of Android `ContentProvider`s, including URI structure, access control, and query handling.

## Using @MASTG-TOOL-0004

You can use @MASTG-TOOL-0004 to interact with `ContentProvider`s on a device or emulator via the `content` command.

### Query rows

```bash
adb shell content query --uri content://org.owasp.mastestapp.provider/students
adb shell content query --uri content://org.owasp.mastestapp.provider/students --where "name='Bob'"
```

### Insert a row

```bash
adb shell content insert \
    --uri content://org.owasp.mastestapp.provider/students \
    --bind name:s:Eve
```

### Update rows

```bash
adb shell content update \
    --uri content://org.owasp.mastestapp.provider/students \
    --where "id=1" \
    --bind name:s:"Alice Jr"
```

### Delete rows

```bash
adb shell content delete \
    --uri content://org.owasp.mastestapp.provider/students \
    --where "id=3"
```

## Notes

- The `--where` argument maps directly to the `selection` parameter in `ContentProvider.query()`.
- The command executes in the context of the shell user, so access depends on whether the provider is exported and what permissions are enforced.
- Quoting and escaping are important when passing strings or crafting test inputs, especially when using SQL operators.
