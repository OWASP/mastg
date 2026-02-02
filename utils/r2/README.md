# r2ooky - Radare2 Automation for MASTG Demos

r2ooky is a Python package that provides a clean API over r2pipe for common binary analysis tasks used in MASTG demos. It replaces hardcoded `.r2` scripts with dynamic analysis that works across different binaries.

## Overview

The current MASTG demos use `.r2` scripts with hardcoded addresses that must be updated for each binary version. r2ooky solves this by:

- **Dynamic resolution**: Finds targets by name patterns instead of hardcoded addresses
- **Declarative configs**: JSON-based configuration similar to frooky hooks
- **Reusable utilities**: Shared Python modules for common operations
- **Reproducible output**: Stable text output matching current demo format

## Package Structure

```
utils/r2/
├── r2ooky/
│   ├── __init__.py       # Package exports
│   ├── core.py           # R2Session wrapper
│   ├── finder.py         # FunctionFinder, ImportFinder, StringFinder
│   ├── xref.py           # XRefAnalyzer for cross-references
│   ├── disasm.py         # DisasmHelper for disassembly
│   ├── config.py         # Config loading and schema
│   └── cli.py            # Command-line interface
└── r2ooky-run.py         # Executable entry point
```

## Core Components

### R2Session

Manages a radare2 session with consistent configuration:

```python
from r2ooky import R2Session

with R2Session("path/to/binary", analyze=True) as session:
    # Analysis is automatically run
    # Colors and interactive mode are disabled
    # Output is stable and reproducible
    result = session.cmd("afl")
    json_result = session.cmdj("aflj")
```

### Finders

Find functions, imports, and strings by pattern:

```python
from r2ooky import FunctionFinder, ImportFinder, StringFinder

# Find functions
func_finder = FunctionFinder(session)
matches = func_finder.find("CC_", mode="contains")  # All CC_* functions
md5_func = func_finder.find_by_exact("CC_MD5")

# Find imports
import_finder = ImportFinder(session)
socket_imports = import_finder.find("socket", mode="contains")

# Find strings
string_finder = StringFinder(session)
urls = string_finder.find("http://", mode="contains")
```

Match modes:
- `exact`: Exact name match
- `contains`: Substring match (default)
- `regex`: Regular expression match

### XRefAnalyzer

Analyze cross-references:

```python
from r2ooky import XRefAnalyzer

xref_analyzer = XRefAnalyzer(session)

# Get xrefs to an address
xrefs = xref_analyzer.get_xrefs_to(match.address)
for xref in xrefs:
    print(f"{xref.xref_type} from {xref.from_addr:#x} in {xref.from_func}")

# Get only call sites
call_sites = xref_analyzer.get_call_sites(function_addr)
```

### DisasmHelper

Disassemble code and dump bytes:

```python
from r2ooky import DisasmHelper

disasm = DisasmHelper(session)

# Disassemble at address
code = disasm.disasm_at(address, lines=10)
code_backward = disasm.disasm_at(address, lines=5, backward=True)

# Disassemble entire function
func_code = disasm.disasm_function(address)

# Disassemble around xref
disasm_text = disasm.disasm_around_xref(xref, before=5, after=5)

# Dump bytes
hex_dump = disasm.dump_bytes(address, size=32)
bytes_array = disasm.dump_bytes_json(address, size=32)
```

## Configuration Format

Demo configurations are JSON files that declaratively specify targets and actions:

```json
{
  "category": "CRYPTO",
  "description": "Detect uses of CommonCrypto hash functions",
  "targets": [
    {
      "type": "function",
      "name": "CC_",
      "match_mode": "contains",
      "description": "All CommonCrypto functions"
    },
    {
      "type": "function",
      "name": "CC_MD5",
      "match_mode": "exact",
      "description": "CC_MD5"
    }
  ],
  "actions": [
    {
      "type": "list_matches",
      "target": "All CommonCrypto functions",
      "output": "Uses of CommonCrypto hash function"
    },
    {
      "type": "show_xrefs",
      "target": "CC_MD5",
      "output": "xrefs to CC_MD5"
    },
    {
      "type": "disasm_around_xrefs",
      "target": "CC_MD5",
      "before": 5,
      "after": 0,
      "output": "Use of MD5"
    }
  ]
}
```

### Target Types

- `function`: Match function names
- `import`: Match import names
- `string`: Match string content

### Action Types

- `list_matches`: List all matches for a target
- `show_xrefs`: Show cross-references to a target
- `disasm_around_xrefs`: Disassemble around each xref
- `dump_function`: Dump entire function disassembly
- `dump_bytes`: Dump bytes at address

## Usage

### Command Line

```bash
# Using the CLI
python3 -m r2ooky path/to/binary config.json

# Or use the runner script
python3 utils/r2/r2ooky-run.py path/to/binary config.json

# Options
python3 -m r2ooky binary config.json --arch arm64     # For fat binaries
python3 -m r2ooky binary config.json --json           # JSON output
python3 -m r2ooky binary config.json --output file    # Write to file
```

### Python Script

```python
#!/usr/bin/env python3
import sys
from pathlib import Path

# Add r2ooky to path
sys.path.insert(0, "path/to/utils/r2")

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper

def main():
    binary_path = "MASTestApp"
    
    with R2Session(binary_path, analyze=True) as session:
        func_finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        
        # Find functions
        print("Uses of CommonCrypto hash function:")
        cc_funcs = func_finder.find_by_contains("CC_")
        for func in cc_funcs:
            print(f"{func.address:#x} {func.name}")
        
        # Get xrefs
        md5_func = func_finder.find_by_exact("CC_MD5")
        if md5_func:
            print("\nxrefs to CC_MD5:")
            xrefs = xref_analyzer.get_xrefs_to(md5_func.address)
            for xref in xrefs:
                print(f"{xref.xref_type} {xref.from_addr:#x}")
                
                # Show disassembly
                print("\nUse of MD5:")
                code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                print(code)

if __name__ == "__main__":
    main()
```

## Migration Guide

### Converting .r2 Scripts

Old `.r2` script with hardcoded addresses:
```r2cmd
?e Uses of CommonCrypto hash function:
afl~CC_

?e xrefs to CC_MD5:
axt @ 0x1000071a8

?e Use of MD5:
pd-- 5 @ 0x1000048c4
```

New Python script:
```python
func_finder = FunctionFinder(session)
xref_analyzer = XRefAnalyzer(session)
disasm = DisasmHelper(session)

print("Uses of CommonCrypto hash function:")
cc_funcs = func_finder.find_by_contains("CC_")
for func in cc_funcs:
    print(f"{func.address:#x} {func.name}")

md5_func = func_finder.find_by_exact("CC_MD5")
if md5_func:
    print("\nxrefs to CC_MD5:")
    xrefs = xref_analyzer.get_xrefs_to(md5_func.address)
    for xref in xrefs:
        print(f"{xref.xref_type} {xref.from_addr:#x}")
        
        print("\nUse of MD5:")
        code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
        print(code)
```

Or use a config file:
```json
{
  "targets": [
    {"type": "function", "name": "CC_", "match_mode": "contains", "description": "CC funcs"},
    {"type": "function", "name": "CC_MD5", "match_mode": "exact", "description": "CC_MD5"}
  ],
  "actions": [
    {"type": "list_matches", "target": "CC funcs", "output": "Uses of CommonCrypto hash function"},
    {"type": "show_xrefs", "target": "CC_MD5", "output": "xrefs to CC_MD5"},
    {"type": "disasm_around_xrefs", "target": "CC_MD5", "before": 5, "after": 0, "output": "Use of MD5"}
  ]
}
```

### Common Patterns

#### Pattern 1: Function List + Xrefs + Disasm

```python
# Find function
matches = func_finder.find("pattern", mode="contains")

# For each match, get xrefs
for match in matches:
    xrefs = xref_analyzer.get_xrefs_to(match.address)
    
    # Disassemble around each xref
    for xref in xrefs:
        code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
```

#### Pattern 2: Import Resolution + Xrefs

```python
# Find import
import_matches = import_finder.find("getaddrinfo", mode="exact")

# Get xrefs to import
for match in import_matches:
    xrefs = xref_analyzer.get_xrefs_to(match.address)
    for xref in xrefs:
        # Show usage
        code = disasm.disasm_function(xref.from_addr)
```

#### Pattern 3: String Search + Xrefs

```python
# Find strings
url_strings = string_finder.find("http://", mode="contains")

# For each string, show where it's used
for string_match in url_strings:
    print(f"\nString: {string_match.name}")
    xrefs = xref_analyzer.get_xrefs_to(string_match.address)
    for xref in xrefs:
        code = disasm.disasm_at(xref.from_addr, lines=10)
        print(code)
```

#### Pattern 4: Evaluating Literals

```python
# Find function call
xrefs = xref_analyzer.get_xrefs_to(function_addr)

# For each call site, evaluate literal argument
for xref in xrefs:
    # Disassemble to find the mov instruction
    code = disasm.disasm_at(xref.from_addr, lines=10, backward=True)
    
    # Or evaluate expression directly
    value = disasm.evaluate_expression("0x50")
    print(f"Port value: {value}")
```

## Design Principles

1. **No hardcoded addresses**: All resolution is dynamic via symbols, imports, strings
2. **Prefer JSON commands**: Use `cmdj()` over `cmd()` and parse structured data
3. **Stable output**: Match existing demo output format for easy migration
4. **Graceful degradation**: Handle stripped binaries and missing symbols
5. **Minimal state**: Each operation is independent and reproducible
6. **Cross-platform**: Works with both Mach-O (iOS) and ELF (Android)

## Testing

```python
# Unit tests can mock r2pipe responses
from unittest.mock import Mock

session = Mock()
session.cmdj.return_value = [
    {"name": "CC_MD5", "offset": 0x1000071a8},
    {"name": "CC_SHA1", "offset": 0x1000071b4}
]

finder = FunctionFinder(session)
matches = finder.find_all()
assert len(matches) == 2
```

## Dependencies

- Python 3.6+
- r2pipe (`pip install r2pipe`)
- radare2 installed and in PATH

## See Also

- [r2pipe documentation](https://book.rada.re/scripting/r2pipe.html)
- [radare2 commands](https://book.rada.re/first_steps/commandline.html)
- [JSON commands reference](https://r2wiki.readthedocs.io/en/latest/home/radare2-python-scripting/)
