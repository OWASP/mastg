---
name: 'Writing MASTG Frooky Hook Configurations'
applyTo: 'demos/**/hooks.json'
---

This guide defines how to write and use Frooky hook configurations in MASTG demos. Frooky (@MASTG-TOOL-0145) is a Frida-based dynamic analysis tool that uses JSON-based hook configurations, providing a declarative approach to method interception without writing custom Frida scripts.

### Version Requirements

- Python 3+
- Frooky installed via pip
- Frida server 17 or later running on the target device

### Location and naming

- Place hook configurations inside the demo folder and name them `hooks.json`.
- If multiple hook configurations are needed, use specific names (for example, `hooks_ssl.json`, `hooks_keystore.json`) and document which to run in the demo Steps and `run.sh`.

Examples:

- `demos/android/MASVS-CRYPTO/MASTG-DEMO-0058/hooks.json`
- iOS DEMO: TBD (currently no iOS demos use Frooky)

### Runtime and invocation

Typical spawn usage in `run.sh`:

```bash
frooky -U -f org.owasp.mastestapp --platform android hooks.json

### Hook configuration format

Hook configurations are JSON files that declare which classes and methods to instrument:

```json
{
  "category": "STORAGE",
  "hooks": [
    {
      "class": "androidx.security.crypto.EncryptedSharedPreferences$Editor",
      "methods": [
        "putString",
        "putStringSet"
      ]
    }
  ]
}
```

Key fields:

- `category`: A label for grouping hooks (for example, `STORAGE`, `CRYPTO`, `NETWORK`)
- `hooks`: Array of hook definitions
    - `class`: Fully qualified class name to hook
    - `methods`: Array of method names to intercept

See more in the [frooky Usage page](https://github.com/cpholguera/frooky/blob/main/docs/usage.md).

### Output format

Frooky outputs JSON Lines (NDJSON) format to `output.json`.

### Best practices

- Keep hook configurations focused on the specific APIs relevant to the demo.
- Use meaningful category names that align with MASVS categories.
- The output JSON provides structured data that can be directly referenced in the demo's **Observation** section. For example as `{{ output.json }}`.
- Stack traces are automatically captured, helping identify the code locations calling the hooked methods.

### Troubleshooting

- Ensure Frida server is running on the target device before executing frooky.
- Use `-f` for spawn mode when early instrumentation is needed.
- Use `-n` for attach mode when the app is already running.
- Check that the class and method names in `hooks.json` match the target app's actual implementation.
