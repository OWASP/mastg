# r2ooky Examples

Complete examples showing how to use r2ooky for different analysis tasks.

## Table of Contents

1. [Basic Usage](#basic-usage)
2. [Finding Functions](#finding-functions)
3. [Finding Imports](#finding-imports)
4. [Finding Strings](#finding-strings)
5. [Analyzing Cross-References](#analyzing-cross-references)
6. [Disassembly Operations](#disassembly-operations)
7. [Complete Demo Examples](#complete-demo-examples)

## Basic Usage

### Opening a Session

```python
from r2ooky import R2Session

# Basic usage
with R2Session("path/to/binary", analyze=True) as session:
    # Session is ready with analysis complete
    result = session.cmd("afl")  # Get function list as text
    json_result = session.cmdj("aflj")  # Get function list as JSON

# For fat binaries, specify architecture
with R2Session("MASTestApp", analyze=True, arch="arm64") as session:
    # Analysis runs on arm64 slice
    pass
```

### Configuration

The R2Session automatically configures radare2 for stable output:
- `scr.color=false` - No colors
- `scr.interactive=false` - Non-interactive mode
- `asm.bytes=false` - No bytes in disassembly
- `asm.var=false` - No variable substitution

## Finding Functions

### Exact Match

```python
from r2ooky import R2Session, FunctionFinder

with R2Session("MASTestApp", analyze=True) as session:
    finder = FunctionFinder(session)
    
    # Find exact function name
    match = finder.find_by_exact("CC_MD5")
    if match:
        print(f"Found {match.name} at {match.address:#x}")
```

### Contains (Substring) Match

```python
from r2ooky import R2Session, FunctionFinder

with R2Session("MASTestApp", analyze=True) as session:
    finder = FunctionFinder(session)
    
    # Find all functions containing "CC_"
    matches = finder.find_by_contains("CC_")
    for match in matches:
        print(f"{match.address:#x} {match.name}")
```

### Regex Match

```python
from r2ooky import R2Session, FunctionFinder

with R2Session("MASTestApp", analyze=True) as session:
    finder = FunctionFinder(session)
    
    # Find functions matching regex pattern
    matches = finder.find_by_regex(r"CC_(MD5|SHA1)")
    for match in matches:
        print(f"{match.address:#x} {match.name}")
```

### Using Generic Find Method

```python
from r2ooky import R2Session, FunctionFinder

with R2Session("MASTestApp", analyze=True) as session:
    finder = FunctionFinder(session)
    
    # Mode can be "exact", "contains", or "regex"
    matches = finder.find("CryptoKit", mode="contains")
    matches = finder.find("CC_MD5", mode="exact")
    matches = finder.find(r"^sym\.func\.", mode="regex")
```

## Finding Imports

```python
from r2ooky import R2Session, ImportFinder

with R2Session("MASTestApp", analyze=True) as session:
    finder = ImportFinder(session)
    
    # Find all socket-related imports
    socket_imports = finder.find_by_contains("socket")
    for imp in socket_imports:
        print(f"{imp.address:#x} {imp.name}")
    
    # Find specific import
    getaddrinfo = finder.find_by_exact("getaddrinfo")
    if getaddrinfo:
        print(f"Found at {getaddrinfo.address:#x}")
    
    # Get all imports
    all_imports = finder.find_all()
```

## Finding Strings

```python
from r2ooky import R2Session, StringFinder

with R2Session("MASTestApp", analyze=True) as session:
    finder = StringFinder(session)
    
    # Find HTTP URLs
    http_strings = finder.find_by_contains("http://")
    for s in http_strings:
        print(f"{s.address:#x} {s.name}")
    
    # Find specific string
    url = finder.find_by_exact("http://httpbin.org/get")
    
    # Regex search
    email_pattern = r"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"
    emails = finder.find_by_regex(email_pattern)
```

## Analyzing Cross-References

### Get XRefs to Address

```python
from r2ooky import R2Session, FunctionFinder, XRefAnalyzer

with R2Session("MASTestApp", analyze=True) as session:
    func_finder = FunctionFinder(session)
    xref_analyzer = XRefAnalyzer(session)
    
    # Find a function
    md5_func = func_finder.find_by_exact("CC_MD5")
    
    if md5_func:
        # Get all xrefs to it
        xrefs = xref_analyzer.get_xrefs_to(md5_func.address)
        
        for xref in xrefs:
            print(f"{xref.xref_type} from {xref.from_addr:#x}")
            if xref.from_func:
                print(f"  in function: {xref.from_func}")
```

### Get Only Call Sites

```python
from r2ooky import R2Session, FunctionFinder, XRefAnalyzer

with R2Session("MASTestApp", analyze=True) as session:
    func_finder = FunctionFinder(session)
    xref_analyzer = XRefAnalyzer(session)
    
    # Find a function
    func = func_finder.find_by_exact("CC_SHA1")
    
    if func:
        # Get only CALL type xrefs
        call_sites = xref_analyzer.get_call_sites(func.address)
        print(f"Found {len(call_sites)} call sites")
        
        for call in call_sites:
            print(f"Called from {call.from_addr:#x} in {call.from_func}")
```

### XRefs from Address

```python
from r2ooky import R2Session, XRefAnalyzer

with R2Session("MASTestApp", analyze=True) as session:
    xref_analyzer = XRefAnalyzer(session)
    
    # Get xrefs from an address (what it calls/references)
    xrefs_from = xref_analyzer.get_xrefs_from(0x100004000)
    
    for xref in xrefs_from:
        print(f"References {xref.to_addr:#x} (type: {xref.xref_type})")
```

## Disassembly Operations

### Disassemble at Address

```python
from r2ooky import R2Session, DisasmHelper

with R2Session("MASTestApp", analyze=True) as session:
    disasm = DisasmHelper(session)
    
    # Disassemble forward (10 instructions)
    code = disasm.disasm_at(0x100004000, lines=10)
    print(code)
    
    # Disassemble backward (5 instructions before address)
    code = disasm.disasm_at(0x100004100, lines=5, backward=True)
    print(code)
```

### Disassemble Entire Function

```python
from r2ooky import R2Session, FunctionFinder, DisasmHelper

with R2Session("MASTestApp", analyze=True) as session:
    func_finder = FunctionFinder(session)
    disasm = DisasmHelper(session)
    
    # Find function
    func = func_finder.find_by_exact("sym.func.100004000")
    
    if func:
        # Get complete function disassembly
        func_code = disasm.disasm_function(func.address)
        print(func_code)
        
        # Or as JSON
        func_json = disasm.disasm_function_json(func.address)
```

### Disassemble Around XRef

```python
from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper

with R2Session("MASTestApp", analyze=True) as session:
    func_finder = FunctionFinder(session)
    xref_analyzer = XRefAnalyzer(session)
    disasm = DisasmHelper(session)
    
    # Find function and its xrefs
    func = func_finder.find_by_exact("CC_MD5")
    if func:
        xrefs = xref_analyzer.get_xrefs_to(func.address)
        
        # Disassemble around each xref location
        for xref in xrefs:
            print(f"\nAt {xref.from_addr:#x}:")
            code = disasm.disasm_around_xref(xref, before=5, after=5)
            print(code)
```

### Dump Bytes

```python
from r2ooky import R2Session, DisasmHelper

with R2Session("MASTestApp", analyze=True) as session:
    disasm = DisasmHelper(session)
    
    # Dump bytes as hex dump string
    hex_dump = disasm.dump_bytes(0x100010000, size=32)
    print(hex_dump)
    
    # Get bytes as array
    byte_array = disasm.dump_bytes_json(0x100010000, size=32)
    print([f"{b:02x}" for b in byte_array])
```

### Read String at Address

```python
from r2ooky import R2Session, DisasmHelper

with R2Session("MASTestApp", analyze=True) as session:
    disasm = DisasmHelper(session)
    
    # Read null-terminated string
    string = disasm.read_string(0x100006c60)
    print(f"String: {string}")
    
    # Read with max length
    string = disasm.read_string(0x100006c60, length=50)
```

### Evaluate Expressions

```python
from r2ooky import R2Session, DisasmHelper

with R2Session("MASTestApp", analyze=True) as session:
    disasm = DisasmHelper(session)
    
    # Evaluate hex value
    value = disasm.evaluate_expression("0x50")
    print(f"Decimal: {value}")  # Output: 80
    
    # Evaluate complex expression
    value = disasm.evaluate_expression("0x100 + 0x20")
```

## Complete Demo Examples

### Example 1: CommonCrypto Hash Detection

```python
#!/usr/bin/env python3
"""Detect uses of CommonCrypto hash functions."""

import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, FunctionFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    
    with R2Session(str(binary_path), analyze=True) as session:
        finder = FunctionFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        output = OutputBuffer()
        
        # Find all CommonCrypto functions
        cc_funcs = finder.find_by_contains("CC_")
        output.add_match_list(cc_funcs, "Uses of CommonCrypto hash function")
        
        # Process MD5
        md5 = finder.find_by_exact("CC_MD5")
        if md5:
            xrefs = xref_analyzer.get_xrefs_to(md5.address)
            output.add_xref_list(xrefs, "xrefs to CC_MD5")
            
            for xref in xrefs:
                code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                output.add_disasm(code, "Use of MD5")
        
        # Process SHA1
        sha1 = finder.find_by_exact("CC_SHA1")
        if sha1:
            xrefs = xref_analyzer.get_xrefs_to(sha1.address)
            output.add_xref_list(xrefs, "xrefs to CC_SHA1")
            
            for xref in xrefs:
                code = disasm.disasm_at(xref.from_addr, lines=5, backward=True)
                output.add_disasm(code, "Use of SHA1")
        
        # Output
        output.print()
        output.write_to_file(Path(__file__).parent / "output_r2ooky.txt")

if __name__ == "__main__":
    sys.exit(main())
```

### Example 2: BSD Sockets Analysis

```python
#!/usr/bin/env python3
"""Analyze BSD socket function usage."""

import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, ImportFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    socket_funcs = ["getaddrinfo", "send", "recv", "connect", "socket"]
    
    with R2Session(str(binary_path), analyze=True) as session:
        import_finder = ImportFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        output = OutputBuffer()
        
        # Find all socket imports
        found = []
        for name in socket_funcs:
            matches = import_finder.find_by_contains(name)
            found.extend([(name, m) for m in matches])
        
        # List imports
        lines = [f"{m.address:#x} {m.name}" for _, m in found]
        output.add_section("Uses of the BSD sockets functions", "\n".join(lines))
        
        # Analyze each import
        for func_name, match in found:
            xrefs = xref_analyzer.get_xrefs_to(match.address)
            output.add_xref_list(xrefs, f"xrefs to {func_name}")
            
            if xrefs:
                for xref in xrefs:
                    code = disasm.disasm_at(xref.from_addr, lines=20, backward=True)
                    output.add_disasm(code, f"Use of {func_name}")
        
        output.print()
        output.write_to_file(Path(__file__).parent / "output_r2ooky.txt")

if __name__ == "__main__":
    sys.exit(main())
```

### Example 3: HTTP URL Detection

```python
#!/usr/bin/env python3
"""Find and analyze HTTP URL usage."""

import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, StringFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer

def main():
    binary_path = Path(__file__).parent / "MASTestApp"
    
    with R2Session(str(binary_path), analyze=True) as session:
        string_finder = StringFinder(session)
        xref_analyzer = XRefAnalyzer(session)
        disasm = DisasmHelper(session)
        output = OutputBuffer()
        
        # Find HTTP URLs
        http_strings = string_finder.find_by_contains("http://")
        output.add_match_list(http_strings, "Uses of http:// URLs")
        
        # Analyze each URL
        for string in http_strings:
            xrefs = xref_analyzer.get_xrefs_to(string.address)
            output.add_xref_list(xrefs, f"xrefs to {string.name}")
            
            if xrefs:
                for xref in xrefs:
                    code = disasm.disasm_at(xref.from_addr, lines=15)
                    output.add_disasm(code, f"Use of {string.name}")
        
        output.print()
        output.write_to_file(Path(__file__).parent / "output_r2ooky.txt")

if __name__ == "__main__":
    sys.exit(main())
```

### Example 4: Using Config Files

```python
#!/usr/bin/env python3
"""Run demo using config file."""

import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import load_config
from r2ooky.cli import R2ooky

def main():
    config_path = Path(__file__).parent / "config.json"
    binary_path = Path(__file__).parent / "MASTestApp"
    
    # Load config
    config = load_config(str(config_path))
    
    # Run analysis
    runner = R2ooky(str(binary_path), config)
    results = runner.run()
    
    # Results can be processed or formatted as needed
    print(results)

if __name__ == "__main__":
    sys.exit(main())
```

## Tips and Best Practices

1. **Always use context managers**: Use `with R2Session(...)` to ensure proper cleanup
2. **Check for None**: Finders return None or empty lists when nothing is found
3. **Use JSON commands**: Prefer `cmdj()` over `cmd()` for structured data
4. **Handle stripped binaries**: Check if symbols exist before using them
5. **Output buffering**: Use `OutputBuffer` for consistent output formatting
6. **Reuse finders**: Create finder instances once and reuse them
7. **Backward compatibility**: Match original .r2 output format when migrating
