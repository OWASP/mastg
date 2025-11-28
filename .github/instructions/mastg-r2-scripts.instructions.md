## r2 scripts (radare2)

This guide defines how to write and use radare2 scripts in MASTG demos. Scripts are included with the demo and executed by `run.sh` to produce the Observation output.

### Scope and terminology

- “r2 scripts” refers to radare2 command files executed with `-i <file>`.
- The repo uses `.r2` as the primary extension (for example, `cchash.r2`); variations like `.jr2` are not used in this repository.
- Tools background lives under @MASTG-TOOL-0073 (radare2) and @MASTG-TOOL-0129 (rabin2). Do not duplicate setup or installation steps—link to Tools pages.

### Location and naming

- Place scripts inside the demo folder; name them after the target concept or API, using lowercase with underscores if needed.
  - Examples:
    - `demos/ios/MASVS-CRYPTO/MASTG-DEMO-0015/cchash.r2`
    - `demos/ios/MASVS-RESILIENCE/MASTG-DEMO-0021/jailbreak_detection.r2`
- If multiple scripts are required, use descriptive names and document which one to run in the demo Steps and `run.sh`.

### Invocation patterns (run.sh)

- Canonical pattern used in demos:
  - `r2 -q -i script.r2 -A <binary> > output.asm`
    - `-q` quiet mode for stable logs
    - `-i script.r2` executes the script file
    - `-A` performs auto-analysis (as used across our demos)
    - Redirect to a file (commonly `output.asm`) for Observation/Evaluation
- Optional evals used by some demos:
  - `-e emu.str=true` to enable emulation of strings in specific analyses
- For multi-arch or fat binaries (Mach-O), specify architecture when necessary:
  - `r2 -q -i script.r2 -A -n arm64 <binary> > output.asm`
- Keep `run.sh` responsible for resolving the correct binary path/name (for example, `MASTestApp` in iOS demos) and for output redirection.

Notes

- Keep `run.sh` responsible for environment prep (paths, extraction/unzip, codesign stripping, etc.). The script should focus on r2 commands only.
- If a path is dynamic, pass it via `-e` evals (for example, `-e bin.file=<path>`), or let `run.sh` build the `o <path>` command line.

### Coding conventions for .r2 files

- Be explicit and quiet:
  - Start with `e scr.color=0` and `e scr.interactive=false` to keep logs clean and deterministic.
  - Disable ASCII art and hints if present: `e scr.utf8=0`, `e scr.ansi=false` (only if needed).
- Analyze narrowly and only as needed to keep runtime fast and stable:
  - Prefer targeted analysis commands (for example, `axt @ <addr>`, `afl~<sym>`) over full `aaa` unless required.
  - When using full analysis, document why and consider `aa`/`aaa` caps for speed.
- Structure output with short, stable headings using `?e` echo lines as markers for Observation/Evaluation parsing.
- Bound outputs to minimize diffs; use filters like `afl~PATTERN`, `iz~PATTERN`, or limit disassembly lines (for example, `pd-- 5 @ <addr>` as seen in `cchash.r2`).
- Prefer symbol-based references when available; fall back to addresses only when necessary and document the basis (for example, derived from `axt @ 0x...`).

### Example: iOS CommonCrypto hashes (excerpt)

```
?e Uses of CommonCrypto hash function:
afl~CC_

?e
?e xrefs to CC_MD5:
axt @ 0x1000071a8

?e xrefs to CC_SHA1:
axt @ 0x1000071b4

?e
?e Use of MD5:
pd-- 5 @ 0x1000048c4

?e
?e Use of SHA1:
pd-- 5 @ 0x10000456c
```

### Logging and outputs

- Use `?e` for plain echo markers; avoid colors and progress noise. Keep section titles consistent with the demo write-up to ease Evaluation checks.
- Redirect all command output into a file in `run.sh` (for example, `output.txt`), and reference it in the demo’s Observation.

### Safety and troubleshooting

- If a symbol or address is not present across versions, add a preceding discovery block to compute it:
  - Example: `afl~CC_MD5[0]` to list and then `s <addr>` before `pd`.
- For fat binaries or stripped symbols:
  - Specify arch with `-a`/`-b` or `-n` as needed.
  - Use heuristics: strings (`iz`), imports (`ii`), and xrefs (`axt`) to locate targets.
- Keep scripts idempotent: avoid `wt` or write operations; analysis should be read-only.

### Alignment with Tools

- Installation and deeper usage guides are in Tools: @MASTG-TOOL-0073 (radare2), @MASTG-TOOL-0129 (rabin2), and related entries. Do not duplicate installation or environment requirements in demos.
- When relevant, mention GUI options like Iaito @MASTG-TOOL-0098 for interactive exploration, but keep demo scripts CLI-focused and reproducible.

### Cross-links

- Tools: @MASTG-TOOL-0073 (radare2), @MASTG-TOOL-0129 (rabin2), @MASTG-TOOL-0098 (Iaito)
- Techniques: @MASTG-TECH-0067 (Dynamic Analysis), @MASTG-TECH-0045 (Runtime Reverse Engineering)