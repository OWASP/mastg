---
name: 'Writing r2ooky scripts (Python with r2pipe) for MASTG demos'
applyTo: 'demos/**/run_r2ooky.py, demos/**/config.json'
---

This guide defines how to write and use r2ooky Python scripts in MASTG demos. r2ooky replaces hardcoded `.r2` scripts with dynamic analysis using Python and r2pipe.

## Overview

r2ooky is a Python package that provides a clean API over r2pipe for common binary analysis tasks. It eliminates hardcoded addresses by resolving targets dynamically through symbols, imports, strings, and cross-references.

**Key Benefits:**
- No hardcoded addresses - everything resolved dynamically
- Reusable utilities across demos
- Declarative JSON configuration
- Consistent, reproducible output
- Works across binary versions

## Location and Naming

Place r2ooky files inside the demo folder:

```
demos/ios/MASVS-CRYPTO/MASTG-DEMO-0015/
├── config.json          # Configuration (targets and actions)
├── run_r2ooky.py       # Python runner script
└── output_r2ooky.txt   # Generated output
```

Keep original `.r2` files for reference during transition:
```
├── cchash.r2           # Original (keep for reference)
├── run.sh              # Original (keep for reference)
├── output.txt          # Original output
```

## Configuration Files (config.json)

Configuration files declaratively specify targets to find and actions to perform.

### Structure

```json
{
  "category": "CRYPTO",
  "description": "Detect uses of CommonCrypto hash functions",
  "targets": [
    {
      "type": "function|import|string",
      "name": "pattern",
      "match_mode": "exact|contains|regex",
      "description": "human-readable label"
    }
  ],
  "actions": [
    {
      "type": "list_matches|show_xrefs|disasm_around_xrefs|dump_function|dump_bytes",
      "target": "reference to target description",
      "output": "section title",
      "before": 5,    // optional: for disasm_around_xrefs
      "after": 5,     // optional: for disasm_around_xrefs
      "size": 32      // optional: for dump_bytes
    }
  ]
}
```

### Target Types

- `function`: Match function names (uses `aflj`)
- `import`: Match import names (uses `iij`)
- `string`: Match string content (uses `izj`)

### Match Modes

- `exact`: Exact name match
- `contains`: Substring match (default)
- `regex`: Regular expression match

### Action Types

- `list_matches`: List all resolved matches
- `show_xrefs`: Show cross-references to target
- `disasm_around_xrefs`: Disassemble around each xref location
- `dump_function`: Dump complete function disassembly
- `dump_bytes`: Dump bytes at address

## Python Scripts (run_r2ooky.py)

### Basic Template

```python
#!/usr/bin/env python3
"""
MASTG-DEMO-XXXX: Demo description.

Brief explanation of what this demo shows.
"""

import sys
from pathlib import Path

# Add r2ooky to path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer


def main():
    """Run the demo."""
    binary_path = Path(__file__).parent / "MASTestApp"
    output_dir = Path(__file__).parent
    
    if not binary_path.exists():
        print(f"Error: Binary not found at {binary_path}")
        return 1
    
    with R2Session(str(binary_path), analyze=True) as session:
        finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        output = OutputBuffer()
        
        # Your analysis logic here
        
        # Output
        output.print()
        output.write_to_file(output_dir / "output_r2ooky.txt")
    
    return 0


if __name__ == "__main__":
    sys.exit(main())
```

### Common Patterns

#### Pattern 1: Function → XRefs → Disasm

```python
# Find function
func = finder.find_by_exact("CC_MD5")

if func:
    # Get xrefs
    xrefs = xref_analyzer.get_xrefs_to(func.address)
    output.add_xref_list(xrefs, "xrefs to CC_MD5")
    
    # Disassemble around each xref
    for xref in xrefs:
        code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
        output.add_disasm(code, "Use of MD5")
```

#### Pattern 2: Import → XRefs → Disasm

```python
from r2ooky import ImportFinder

import_finder = ImportFinder(session)

# Find import
imports = import_finder.find_by_contains("getaddrinfo")

for imp in imports:
    xrefs = xref_analyzer.get_xrefs_to(imp.address)
    output.add_xref_list(xrefs, f"xrefs to {imp.name}")
    
    for xref in xrefs:
        code = disasm.disasm_function(xref.from_addr)
        output.add_disasm(code, f"Use of {imp.name}")
```

#### Pattern 3: String → XRefs → Disasm

```python
from r2ooky import StringFinder

string_finder = StringFinder(session)

# Find strings
http_urls = string_finder.find_by_contains("http://")
output.add_match_list(http_urls, "Uses of http:// URLs")

for string in http_urls:
    xrefs = xref_analyzer.get_xrefs_to(string.address)
    for xref in xrefs:
        code = disasm.disasm_at(xref.from_addr, lines=15)
        output.add_disasm(code, f"Use of {string.name}")
```

## Output Format

Match the original `.r2` script output format for consistency:

```
Uses of CommonCrypto hash function:
0x1000071a8 CC_MD5
0x1000071b4 CC_SHA1

xrefs to CC_MD5:
CALL 0x1000048c4

Use of MD5:

[disassembly here]
```

Use `OutputBuffer` to maintain consistent formatting:

```python
output = OutputBuffer()
output.add_match_list(matches, "Title")
output.add_xref_list(xrefs, "Title")
output.add_disasm(code, "Title")
output.print()
```

## Best Practices

### Error Handling

Always check for None or empty results:

```python
func = finder.find_by_exact("CC_MD5")
if func:
    # Process
else:
    print("Warning: CC_MD5 not found")
```

### List vs Single Result

- `find_by_exact()` returns single MatchResult or None
- `find_by_contains()` and `find_by_regex()` return List[MatchResult]

```python
# Single result
md5 = finder.find_by_exact("CC_MD5")
if md5:
    process(md5)

# Multiple results
cc_funcs = finder.find_by_contains("CC_")
for func in cc_funcs:
    process(func)
```

### Resource Management

Always use context managers:

```python
# Good
with R2Session(binary_path, analyze=True) as session:
    # Analysis here
    pass

# Bad - session not closed properly
session = R2Session(binary_path, analyze=True)
# ... do work ...
```

### Architecture for Fat Binaries

Specify architecture when needed (iOS/macOS fat binaries):

```python
with R2Session(binary_path, analyze=True, arch="arm64") as session:
    # Analysis on arm64 slice
    pass
```

## Validation

Before finalizing migration:

- [ ] Script runs without errors
- [ ] Output matches original format
- [ ] No hardcoded addresses in code
- [ ] Handles missing symbols gracefully
- [ ] Config file is valid JSON
- [ ] Script is executable (`chmod +x`)
- [ ] Output file is generated correctly

## Integration with run.sh

Update or create `run.sh` to call Python script:

```bash
#!/bin/bash
python3 run_r2ooky.py
```

Or keep both for comparison:

```bash
#!/bin/bash
# Original
r2 -q -i cchash.r2 -A MASTestApp > output.txt

# New r2ooky version
python3 run_r2ooky.py
```

## Troubleshooting

### Import Errors

If `from r2ooky import ...` fails:

```python
# Verify path calculation is correct
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))
```

### r2pipe Not Found

Install dependencies:

```bash
pip install r2pipe
```

### Analysis Too Slow

Use lighter analysis:

```python
# Instead of default aaa
session = R2Session(binary_path, analyze=False)
session.cmd("aa")  # Lighter analysis
```

### Missing Symbols

Check if symbol exists before using:

```python
matches = finder.find_by_exact("CC_MD5")
if not matches:
    print("Symbol not found - binary may be stripped")
    return
```

## Examples

See comprehensive examples in:
- `utils/r2/EXAMPLES.md` - 15+ working examples
- `utils/r2/MIGRATION.md` - Pattern-by-pattern migration guide
- `demos/ios/MASVS-CRYPTO/MASTG-DEMO-0015/` - Reference implementation

## See Also

- r2ooky API Reference: `utils/r2/README.md`
- Design Specification: `utils/r2/DESIGN.md`
- Migration Status: `utils/r2/MIGRATION_STATUS.md`
- [r2pipe Documentation](https://book.rada.re/scripting/r2pipe.html)
- [radare2 Book](https://book.rada.re/)
