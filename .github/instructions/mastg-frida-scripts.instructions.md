## Frida Scripts

This guide defines how to write and use Frida scripts in MASTG demos. Scripts live alongside the demo content and are executed by `run.sh` to produce the demo’s Observation output.

Version requirement

- Use Frida 17 or later. See @MASTG-TOOL-0031 (Frida): https://mas.owasp.org/MASTG/tools/generic/MASTG-TOOL-0031/#frida-17

### Location and naming

- Place scripts inside the demo folder and name them `script.js` unless multiple scripts are needed.
- If multiple scripts are required, use specific names (for example, `hook_ssl.js`, `hook_keystore.js`) and document which to run in the demo Steps and `run.sh`.

Examples:

- `demos/ios/MASVS-AUTH/MASTG-DEMO-0042/script.js`
- `demos/android/MASVS-NETWORK/MASTG-DEMO-0007/script.js`

### Runtime and invocation

- Typical spawn usage in `run.sh`:
	- `frida -U -f <bundle_or_package_id> -l script.js -o output.txt`

### Coding conventions

- Keep scripts self-contained (no external module imports).
- Keep output concise and deterministic for Evaluation parsing.
- Check class/method existence; log a clear message if missing.
- Avoid global side effects; scope variables within hooks/functions.
- Logging: prefer `console.log()`; add short section headers only when helpful.
- Backtraces: use `DebugSymbol.fromAddress` and cap lines.
- In `onEnter/onLeave`, capture context first (for example, `const ctx = this.context;`) before using nested arrow functions.

### Inspiration

- Don't reinvent the wheel when something already exists. Use existing open-source sources when available, for example, https://codeshare.frida.re/browse.
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

```js
// SOURCE: https://github.com/iddoeldor/frida-snippets?tab=readme-ov-file#os-log

var m = 'libsystem_trace.dylib';
// bool os_log_type_enabled(os_log_t oslog, os_log_type_t type);
var isEnabledFunc = Module.findExportByName(m, 'os_log_type_enabled');
// _os_log_impl(void *dso, os_log_t log, os_log_type_t type, const char *format, uint8_t *buf, unsigned int size);
var logFunc = Module.findExportByName(m, '_os_log_impl');

// Enable all logs
Interceptor.attach(isEnabledFunc, {
  onLeave: function (ret) {
    ret.replace(0x1);
  }
});
...
```

### Logging and outputs

- Redirect script output to `output.txt` from `run.sh`.
- Keep logs minimal and structured so Observation/Evaluation can reference them directly.
- Cap list outputs (for example, backtraces) to keep diffs stable.

### Safety and troubleshooting

- Use try/catch around complex hooks to prevent script termination.
- If a symbol/method is missing, log and continue.
- Spawn vs attach: use `-f` for early instrumentation when needed.
- Consider stripped binaries and symbol resolution; prefer Objective-C/Java-level hooks over raw native symbols where possible.
- Version compatibility: ensure `frida-tools` (CLI on the host) and the device runtime (for example, `frida-server` on Android or injected runtime on iOS) use matching major/minor versions (17.x with 17.x).
