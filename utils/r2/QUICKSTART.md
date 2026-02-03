# r2ooky Quick Start Guide

Get started with r2ooky in 5 minutes.

## Installation

```bash
# Install r2pipe dependency
pip install r2pipe

# Ensure radare2 is installed
r2 -v
```

## Your First r2ooky Script

### 1. Create a Config File

Save as `demo-config.json`:

```json
{
  "category": "CRYPTO",
  "description": "Find CommonCrypto hash functions",
  "targets": [
    {
      "type": "function",
      "name": "CC_",
      "match_mode": "contains",
      "description": "CommonCrypto functions"
    }
  ],
  "actions": [
    {
      "type": "list_matches",
      "target": "CommonCrypto functions",
      "output": "Uses of CommonCrypto"
    }
  ]
}
```

### 2. Run via CLI

```bash
cd /path/to/mastg
python3 -m r2ooky path/to/MASTestApp demo-config.json
```

### 3. Or Write a Python Script

Save as `analyze.py`:

```python
#!/usr/bin/env python3
import sys
from pathlib import Path

# Add r2ooky to path
sys.path.insert(0, "utils/r2")

from r2ooky import R2Session, FunctionFinder

def main():
    with R2Session("MASTestApp", analyze=True) as session:
        finder = FunctionFinder(session)
        
        # Find all functions containing "CC_"
        matches = finder.find_by_contains("CC_")
        
        print("CommonCrypto functions:")
        for match in matches:
            print(f"  {match.address:#x} {match.name}")

if __name__ == "__main__":
    main()
```

Run it:
```bash
chmod +x analyze.py
python3 analyze.py
```

## Common Tasks

### Find a Specific Function

```python
from r2ooky import R2Session, FunctionFinder

with R2Session("binary", analyze=True) as session:
    finder = FunctionFinder(session)
    
    # Exact match
    md5 = finder.find_by_exact("CC_MD5")
    if md5:
        print(f"Found at {md5.address:#x}")
```

### Get Cross-References

```python
from r2ooky import R2Session, FunctionFinder, XRefAnalyzer

with R2Session("binary", analyze=True) as session:
    finder = FunctionFinder(session)
    xref_analyzer = XRefAnalyzer(session)
    
    func = finder.find_by_exact("CC_MD5")
    if func:
        xrefs = xref_analyzer.get_xrefs_to(func.address)
        for xref in xrefs:
            print(f"Called from {xref.from_addr:#x}")
```

### Disassemble Code

```python
from r2ooky import R2Session, DisasmHelper

with R2Session("binary", analyze=True) as session:
    disasm = DisasmHelper(session)
    
    # Disassemble 10 instructions
    code = disasm.disasm_at(0x100004000, lines=10)
    print(code)
    
    # Or disassemble backward
    code = disasm.disasm_at(0x100004000, lines=5, backward=True)
    print(code)
```

### Complete Example with Output

```python
#!/usr/bin/env python3
import sys
from pathlib import Path
sys.path.insert(0, "utils/r2")

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer

def main():
    binary = "MASTestApp"
    
    with R2Session(binary, analyze=True) as session:
        finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        output = OutputBuffer()
        
        # Find function
        md5 = finder.find_by_exact("CC_MD5")
        if not md5:
            print("CC_MD5 not found")
            return 1
        
        # Get and show xrefs
        xrefs = xref_analyzer.get_xrefs_to(md5.address)
        output.add_xref_list(xrefs, "xrefs to CC_MD5")
        
        # Disassemble each usage
        for xref in xrefs:
            code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
            output.add_disasm(code, f"Use at {xref.from_addr:#x}")
        
        # Output
        output.print()
        output.write_to_file("output.txt")

if __name__ == "__main__":
    sys.exit(main())
```

## Pattern Templates

### Pattern 1: Function Analysis

```python
# Find function → Get xrefs → Disassemble
func = finder.find_by_exact("function_name")
xrefs = xref_analyzer.get_xrefs_to(func.address)
for xref in xrefs:
    code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
```

### Pattern 2: Import Analysis

```python
from r2ooky import ImportFinder

import_finder = ImportFinder(session)
imp = import_finder.find_by_exact("getaddrinfo")
xrefs = xref_analyzer.get_xrefs_to(imp.address)
for xref in xrefs:
    code = disasm.disasm_function(xref.from_addr)
```

### Pattern 3: String Search

```python
from r2ooky import StringFinder

string_finder = StringFinder(session)
urls = string_finder.find_by_contains("http://")
for url in urls:
    xrefs = xref_analyzer.get_xrefs_to(url.address)
    # Process xrefs...
```

## Config File Template

```json
{
  "category": "MASVS-CATEGORY",
  "description": "What this demo detects",
  "targets": [
    {
      "type": "function",
      "name": "pattern_to_match",
      "match_mode": "exact",
      "description": "Human readable label"
    }
  ],
  "actions": [
    {
      "type": "list_matches",
      "target": "Human readable label",
      "output": "Section title in output"
    },
    {
      "type": "show_xrefs",
      "target": "Human readable label",
      "output": "Another section title"
    },
    {
      "type": "disasm_around_xrefs",
      "target": "Human readable label",
      "before": 5,
      "after": 5,
      "output": "Disassembly section title"
    }
  ]
}
```

## Troubleshooting

### ImportError: No module named 'r2ooky'

Make sure the path is correct:
```python
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))
```

Or use absolute path:
```python
sys.path.insert(0, "/home/runner/work/mastg/mastg/utils/r2")
```

### r2pipe not found

Install it:
```bash
pip install r2pipe
```

### Analysis takes too long

Use lighter analysis:
```python
session = R2Session(binary, analyze=False)
session.cmd("aa")  # Instead of aaa
```

### Symbol not found

Check if it exists:
```python
matches = finder.find_by_exact("CC_MD5")
if not matches:
    print("Symbol not found")
    # Try contains instead
    matches = finder.find_by_contains("MD5")
```

## Next Steps

- **Full Examples:** See `utils/r2/EXAMPLES.md`
- **API Reference:** See `utils/r2/README.md`
- **Migration Guide:** See `utils/r2/MIGRATION.md`
- **Working Demos:** See `demos/ios/MASVS-CRYPTO/MASTG-DEMO-0015/`

## Getting Help

1. Check the documentation in `utils/r2/`
2. Look at working demos in `demos/ios/MASVS-*/MASTG-DEMO-*/run_r2ooky.py`
3. Open an issue on GitHub

## Cheat Sheet

```python
# Session
with R2Session(binary, analyze=True) as session:

# Find
finder.find_by_exact(name)           # → MatchResult | None
finder.find_by_contains(pattern)     # → List[MatchResult]
finder.find_by_regex(pattern)        # → List[MatchResult]

# XRefs
xref_analyzer.get_xrefs_to(addr)     # → List[XRefResult]
xref_analyzer.get_call_sites(addr)   # → List[XRefResult]

# Disasm
disasm.disasm_at(addr, lines=10)                    # Forward
disasm.disasm_at(addr, lines=5, backward=True)      # Backward
disasm.disasm_function(addr)                         # Full function
disasm.disasm_around_xref(xref, before=5, after=5)  # Around xref

# Output
output = OutputBuffer()
output.add_match_list(matches, "Title")
output.add_xref_list(xrefs, "Title")
output.add_disasm(code, "Title")
output.print()
output.write_to_file("file.txt")
```

Happy analyzing! 🎉
