---
name: 'Writing Frida scripts for MASTG demos'
applyTo: 'demos/**/script.js'
---

This guide defines how to write and use Frida scripts in MASTG demos. Scripts live alongside the demo content and are executed by `run.sh` to produce the demo's Observation output.

**Note:** Prefer Frooky hooks over Frida scripts where possible, as they require less code. See `mastg-frooky-scripts.instructions.md` for details.

## Location and naming

- Place scripts inside the demo folder and name them `script.js` unless multiple scripts are needed.
- If multiple scripts are required, use specific names (for example, `hook_ssl.js`, `hook_keystore.js`) and document which to run in the demo Steps and `run.sh`.

Examples:

- `demos/ios/MASVS-AUTH/MASTG-DEMO-0042/script.js`
- `demos/android/MASVS-NETWORK/MASTG-DEMO-0007/script.js`

## Runtime and invocation

- Typical spawn usage in `run.sh`:
    - `frida -U -f <bundle_or_package_id> -l script.js -o output.txt`

## Coding conventions

- Keep scripts self-contained (no external module imports).
- Keep output concise and deterministic for Evaluation parsing.
- Check class/method existence; log a clear message if missing.
- Avoid global side effects; scope variables within hooks/functions.
- Logging: prefer `console.log()`; add short section headers only when helpful.
- Backtraces: use `DebugSymbol.fromAddress` and cap lines.
- In `onEnter/onLeave`, capture context first (for example, `const ctx = this.context;`) before using nested arrow functions.

## Use Frida 17 APIs exclusively

| Area           | Before Frida 17                                     | Frida 17 and later                                            | Notes                                             |
| -------------- | --------------------------------------------------- | ------------------------------------------------------------- | ------------------------------------------------- |
| Global exports | `Module.getExportByName(null, "open")`        | `Module.getGlobalExportByName("open")`                        | Global lookup no longer accepts a module argument |
| Global exports | `Module.findExportByName(null, "open")`             | `Module.findGlobalExportByName("open")`                       | Use this when the export may not exist            |
| Symbols        | `Module.getSymbolByName(null, "open")`              | `Module.getGlobalExportByName("open")`                        | Symbol helpers removed for common cases           |
| Module exports | `Module.findExportByName("libc.so", "open")`        | `Process.getModuleByName("libc.so").findExportByName("open")` | Static Module helpers removed                     |
| Module exports | `Module.getExportByName("libc.so", "open")`         | `Process.getModuleByName("libc.so").getExportByName("open")`  | Same pattern applies to symbols and enumeration   |
| Module base    | `Module.getBaseAddress("libc.so")`                  | `Process.getModuleByName("libc.so").base`                     | `findBaseAddress` removed as well                 |
| Enumeration    | `Process.enumerateModules({ onMatch, onComplete })` | `Process.enumerateModules()`                                  | Same change applies to threads and ranges         |
| Memory reads   | `Memory.readInt(ptr)`                               | `ptr.readInt()`                                               | Applies to all `read*` helpers                    |
| Memory writes  | `Memory.writeUInt(ptr, val)`                        | `ptr.writeUInt(val)`                                          | Applies to all `write*` helpers                   |
| Strings        | `Memory.readUtf8String(ptr)`                        | `ptr.readUtf8String()`                                        | Same for C, UTF16, ANSI strings                   |
| Byte arrays    | `Memory.readByteArray(ptr, len)`                    | `ptr.readByteArray(len)`                                      | Writing uses the same pattern                     |

See:

- <https://mas.owasp.org/MASTG/tools/generic/MASTG-TOOL-0031/#frida-17>
- <https://frida.re/news/2025/05/17/frida-17-0-0-released/>

Always validate against the latest [JavaScript API](https://frida.re/docs/javascript-api/).

## Inspiration

- Don't reinvent the wheel when something already exists. Use existing open-source sources when available, for example, <https://codeshare.frida.re/browse>.
- If you use a source, be sure to document it and give credit to the author. Include a link to the source in a comment at the beginning of the frida script.

Example:

```js
// SOURCE: https://codeshare.frida.re/@TheDauntless/disable-flutter-tls-v1/

// Configuration object containing patterns to locate the ssl_verify_peer_cert function for different platforms and architectures.
var config = {
    "ios":{
        "modulename": "Flutter",
        "patterns":{
            "arm64": [
                ...
```

## Logging and outputs

- Redirect script output to `output.txt` from `run.sh`.
- Keep logs minimal and structured so Observation/Evaluation can reference them directly.
- Cap list outputs (for example, backtraces) to keep diffs stable.

## Safety and troubleshooting

- Use try/catch around complex hooks to prevent script termination.
- If a symbol/method is missing, log and continue.
- Spawn vs attach: use `-f` for early instrumentation when needed.
- Consider stripped binaries and symbol resolution; prefer Objective-C/Java-level hooks over raw native symbols where possible.
- Version compatibility: ensure `frida-tools` (CLI on the host) and the device runtime (for example, `frida-server` on Android or injected runtime on iOS) use matching major/minor versions (17.x with 17.x).
