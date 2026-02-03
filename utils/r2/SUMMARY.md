# r2ooky - Summary

This document provides a high-level overview of the r2ooky project.

## What is r2ooky?

r2ooky is a Python package that provides a clean API over r2pipe for common binary analysis tasks used in MASTG (Mobile Application Security Testing Guide) demos. It replaces hardcoded `.r2` scripts with dynamic analysis that works across different binaries without modification.

## Problem Statement

The current MASTG demos use `.r2` radare2 scripts with hardcoded addresses:

```r2cmd
?e xrefs to CC_MD5:
axt @ 0x1000071a8        # ← Hardcoded address

?e Use of MD5:
pd-- 5 @ 0x1000048c4     # ← Hardcoded address
```

**Issues:**
- Must be updated for each binary version
- Not reusable across different apps
- Brittle and error-prone
- Difficult to maintain

## Solution

r2ooky provides:

1. **Dynamic Resolution** - Find targets by name patterns instead of addresses
2. **Reusable Utilities** - Shared Python modules for common operations
3. **Declarative Configs** - JSON-based specs similar to frooky hooks
4. **Reproducible Output** - Stable text format matching original demos

## Quick Start

### Install Dependencies

```bash
pip install r2pipe
```

### Basic Usage

```python
from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper

with R2Session("MASTestApp", analyze=True) as session:
    # Find function dynamically
    finder = FunctionFinder(session)
    md5_func = finder.find_by_exact("CC_MD5")
    
    # Get cross-references
    xref_analyzer = XRefAnalyzer(session)
    xrefs = xref_analyzer.get_xrefs_to(md5_func.address)
    
    # Disassemble around each usage
    disasm = DisasmHelper(session)
    for xref in xrefs:
        code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
        print(code)
```

### Using Config Files

Create `config.json`:
```json
{
  "category": "CRYPTO",
  "targets": [
    {"type": "function", "name": "CC_MD5", "match_mode": "exact"}
  ],
  "actions": [
    {"type": "show_xrefs", "target": "CC_MD5", "output": "xrefs to CC_MD5"}
  ]
}
```

Run:
```bash
python3 -m r2ooky MASTestApp config.json
```

## Architecture

```
Demo Script (run_r2ooky.py)
    ↓
r2ooky Package
    ├── Finders (FunctionFinder, ImportFinder, StringFinder)
    ├── XRefAnalyzer
    ├── DisasmHelper
    ├── Formatter (OutputBuffer)
    └── Config (JSON loader)
    ↓
R2Session (r2pipe wrapper)
    ↓
r2pipe
    ↓
radare2
```

## Key Components

### R2Session
- Manages radare2 with stable configuration
- Runs analysis automatically
- Provides `cmd()` and `cmdj()` interfaces

### Finders
- **FunctionFinder** - Find functions by name
- **ImportFinder** - Find imports by name
- **StringFinder** - Find strings by content

All support three modes:
- `exact` - Exact match
- `contains` - Substring match (default)
- `regex` - Regular expression match

### XRefAnalyzer
- Get cross-references to/from addresses
- Filter by type (calls, data references)
- Resolve containing functions

### DisasmHelper
- Disassemble at addresses (forward/backward)
- Disassemble entire functions
- Dump bytes and read strings
- Evaluate expressions

### OutputBuffer
- Collect output sections
- Format consistently
- Write to file or stdout

## Migration Patterns

All 19 existing demos follow these patterns:

### Pattern 1: Function → XRefs → Disasm
```python
func = finder.find_by_exact("CC_MD5")
xrefs = xref_analyzer.get_xrefs_to(func.address)
for xref in xrefs:
    code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
```

### Pattern 2: Import → XRefs → Disasm
```python
imp = import_finder.find_by_exact("getaddrinfo")
xrefs = xref_analyzer.get_xrefs_to(imp.address)
for xref in xrefs:
    code = disasm.disasm_function(xref.from_addr)
```

### Pattern 3: String → XRefs → Disasm
```python
strings = string_finder.find_by_contains("http://")
for string in strings:
    xrefs = xref_analyzer.get_xrefs_to(string.address)
    for xref in xrefs:
        code = disasm.disasm_at(xref.from_addr, lines=10)
```

## Migration Status

**Completed: 4 / 19 demos (21%)**

Reference implementations:
- ✅ MASTG-DEMO-0015 - CommonCrypto hash (Pattern 1)
- ✅ MASTG-DEMO-0014 - CryptoKit ECDSA (Pattern 2 + dump)
- ✅ MASTG-DEMO-0086 - BSD sockets (Import pattern)
- ✅ MASTG-DEMO-0084 - HTTP URLs (String pattern)

Remaining: 15 demos across CRYPTO, STORAGE, NETWORK, RESILIENCE, AUTH

## Documentation

| Document | Description |
|----------|-------------|
| README.md | API reference and usage guide |
| MIGRATION.md | Step-by-step migration guide with examples |
| EXAMPLES.md | 15+ complete working examples |
| DESIGN.md | Architecture and design specification |
| MIGRATION_STATUS.md | Progress tracking table |

## Benefits

### For Demo Authors
- Write less code (config + 10-20 lines Python)
- No hardcoded addresses
- Reusable patterns
- Easier maintenance

### For Demo Users
- Works across binary versions
- Clear, reproducible results
- Consistent output format
- Easy to understand

### For the Project
- Reduced maintenance burden
- Better code quality
- Easier testing
- Scalable approach

## Example Comparison

### Before (Old .r2 script)
```r2cmd
?e Uses of CommonCrypto hash function:
afl~CC_

?e xrefs to CC_MD5:
axt @ 0x1000071a8          # Hardcoded!

?e Use of MD5:
pd-- 5 @ 0x1000048c4       # Hardcoded!
```

### After (Python with r2ooky)
```python
# Find dynamically
cc_funcs = finder.find_by_contains("CC_")
print("Uses of CommonCrypto hash function:")
for func in cc_funcs:
    print(f"{func.address:#x} {func.name}")

# Get xrefs dynamically
md5 = finder.find_by_exact("CC_MD5")
xrefs = xref_analyzer.get_xrefs_to(md5.address)
print("\nxrefs to CC_MD5:")
for xref in xrefs:
    print(f"{xref.xref_type} {xref.from_addr:#x}")

# Disassemble
print("\nUse of MD5:")
for xref in xrefs:
    code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
    print(code)
```

**Or use config:**
```json
{
  "targets": [
    {"type": "function", "name": "CC_", "match_mode": "contains"},
    {"type": "function", "name": "CC_MD5", "match_mode": "exact"}
  ],
  "actions": [
    {"type": "list_matches", "target": "CC_", "output": "Uses of CommonCrypto hash function"},
    {"type": "show_xrefs", "target": "CC_MD5", "output": "xrefs to CC_MD5"},
    {"type": "disasm_around_xrefs", "target": "CC_MD5", "before": 5, "output": "Use of MD5"}
  ]
}
```

## Design Principles

1. **No Hardcoded Addresses** - Everything resolved dynamically
2. **JSON-First** - Use structured data, not text scraping
3. **Stable Output** - Match existing demo format
4. **Graceful Degradation** - Handle missing symbols
5. **Minimal State** - Each operation is independent
6. **Cross-Platform** - Works with Mach-O and ELF

## Future Enhancements

Possible extensions:
- Android/ELF-specific utilities
- Call graph analysis
- Data flow tracking
- Batch processing across multiple binaries
- Integration with other MASTG tools (frooky, semgrep)
- CI/CD integration for automated testing

## Getting Help

- **API Reference**: See `README.md`
- **Migration Guide**: See `MIGRATION.md`
- **Examples**: See `EXAMPLES.md`
- **Design Details**: See `DESIGN.md`
- **Issues**: GitHub Issues

## Contributing

To add a new utility:
1. Follow existing patterns (Finders, Analyzers, Helpers)
2. Use JSON commands where possible
3. Return structured results (MatchResult, XRefResult, etc.)
4. Add unit tests with mocked r2pipe
5. Document in docstrings and examples

## License

Same as OWASP MASTG project.

## Credits

Designed to replace hardcoded `.r2` scripts in OWASP MASTG demos with maintainable, reusable Python code using r2pipe.
