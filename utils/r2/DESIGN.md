# r2ooky Design Specification

Complete design specification for the r2ooky package.

## Table of Contents

1. [Architecture](#architecture)
2. [API Reference](#api-reference)
3. [Configuration Schema](#configuration-schema)
4. [Extension Points](#extension-points)
5. [Testing Strategy](#testing-strategy)
6. [Performance Considerations](#performance-considerations)

## Architecture

### Component Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Demo Script / CLI                     │
│         (run_r2ooky.py / r2ooky-run.py)                 │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│                   r2ooky Package                         │
│  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐   │
│  │   Config     │  │  Formatter  │  │     CLI      │   │
│  └──────┬───────┘  └──────┬──────┘  └──────┬───────┘   │
│         │                  │                 │           │
│  ┌──────┴──────────────────┴─────────────────┘           │
│  │                                                        │
│  ▼                                                        │
│  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐   │
│  │   Finders    │  │  XRefAnalyzer│  │  DisasmHelper│   │
│  └──────┬───────┘  └──────┬──────┘  └──────┬───────┘   │
│         └──────────────────┴─────────────────┘           │
│                            │                             │
│                            ▼                             │
│                  ┌─────────────────┐                     │
│                  │    R2Session    │                     │
│                  └────────┬────────┘                     │
└───────────────────────────┼──────────────────────────────┘
                            │
                            ▼
                    ┌───────────────┐
                    │    r2pipe     │
                    └───────┬───────┘
                            │
                            ▼
                    ┌───────────────┐
                    │   radare2     │
                    └───────────────┘
```

### Design Principles

1. **Separation of Concerns**
   - Core radare2 interaction isolated in R2Session
   - High-level operations in specialized classes (Finders, XRefAnalyzer, etc.)
   - Output formatting separate from analysis logic

2. **Stateless Operations**
   - Each operation is independent
   - No hidden state in helper classes
   - Session is the only stateful component

3. **JSON-First**
   - Prefer radare2 JSON commands (`cmdj`)
   - Parse structured data, not text
   - Wrap non-JSON commands with stable interfaces

4. **Fail Gracefully**
   - Handle missing symbols/functions
   - Return empty lists instead of raising exceptions
   - Provide helpful diagnostics

5. **Configuration Over Code**
   - Declarative JSON configs for demo specs
   - Minimal per-demo Python code
   - Reusable utility functions

## API Reference

### R2Session

```python
class R2Session:
    def __init__(self, binary_path: str, analyze: bool = True, 
                 arch: Optional[str] = None)
    def cmd(self, command: str) -> str
    def cmdj(self, command: str) -> Any
    def get_info(self) -> Dict[str, Any]
    def close(self)
    def __enter__(self) -> 'R2Session'
    def __exit__(self, exc_type, exc_val, exc_tb)
```

**Purpose**: Manages radare2 session with consistent configuration.

**Configuration Applied**:
- `scr.color=false` - Disable colors
- `scr.interactive=false` - Non-interactive mode
- `asm.bytes=false` - No bytes in disassembly
- `asm.var=false` - No variable substitution

**Thread Safety**: Not thread-safe. Create one session per thread.

### MatchResult

```python
class MatchResult:
    name: str           # Symbol/string name
    address: int        # Address of match
    match_type: str     # "exact", "contains", "regex"
    metadata: Dict      # Additional info
    
    def to_dict(self) -> Dict[str, Any]
```

**Purpose**: Represents a matched symbol, import, or string.

### FunctionFinder

```python
class FunctionFinder:
    def __init__(self, session: R2Session)
    def find_all(self) -> List[Dict[str, Any]]
    def find_by_exact(self, name: str) -> Optional[MatchResult]
    def find_by_contains(self, pattern: str) -> List[MatchResult]
    def find_by_regex(self, pattern: str) -> List[MatchResult]
    def find(self, pattern: str, mode: str = "contains") -> List[MatchResult]
```

**Purpose**: Find functions by name patterns.

**Radare2 Commands Used**:
- `aflj` - Get all functions as JSON

**Return Values**:
- `find_by_exact`: Single MatchResult or None
- `find_by_contains/regex`: List of MatchResult (may be empty)

### ImportFinder

```python
class ImportFinder:
    def __init__(self, session: R2Session)
    def find_all(self) -> List[Dict[str, Any]]
    def find_by_exact(self, name: str) -> Optional[MatchResult]
    def find_by_contains(self, pattern: str) -> List[MatchResult]
    def find_by_regex(self, pattern: str) -> List[MatchResult]
    def find(self, pattern: str, mode: str = "contains") -> List[MatchResult]
```

**Purpose**: Find imports by name patterns.

**Radare2 Commands Used**:
- `iij` - Get all imports as JSON

**Notes**:
- Address field contains PLT address (if available)
- Metadata includes import type

### StringFinder

```python
class StringFinder:
    def __init__(self, session: R2Session)
    def find_all(self) -> List[Dict[str, Any]]
    def find_by_exact(self, content: str) -> Optional[MatchResult]
    def find_by_contains(self, pattern: str) -> List[MatchResult]
    def find_by_regex(self, pattern: str) -> List[MatchResult]
    def find(self, pattern: str, mode: str = "contains") -> List[MatchResult]
```

**Purpose**: Find strings by content patterns.

**Radare2 Commands Used**:
- `izj` - Get all strings as JSON

**Notes**:
- Name field contains string content
- Metadata includes string length and type

### XRefResult

```python
class XRefResult:
    from_addr: int              # Reference source
    to_addr: int                # Reference target
    xref_type: str              # "CALL", "DATA", etc.
    from_func: Optional[str]    # Containing function
    metadata: Dict              # Additional info
    
    def to_dict(self) -> Dict[str, Any]
```

**Purpose**: Represents a cross-reference.

### XRefAnalyzer

```python
class XRefAnalyzer:
    def __init__(self, session: R2Session)
    def get_xrefs_to(self, address: int) -> List[XRefResult]
    def get_xrefs_from(self, address: int) -> List[XRefResult]
    def get_call_sites(self, target_address: int) -> List[XRefResult]
    def resolve_and_get_xrefs(self, match: MatchResult) -> List[XRefResult]
```

**Purpose**: Analyze cross-references.

**Radare2 Commands Used**:
- `axtj @ {addr}` - Get xrefs to address as JSON
- `axfj @ {addr}` - Get xrefs from address as JSON
- `afij @ {addr}` - Get function info at address

**Notes**:
- Automatically resolves containing function for each xref
- `get_call_sites` filters only CALL/CODE type xrefs

### DisasmHelper

```python
class DisasmHelper:
    def __init__(self, session: R2Session)
    def disasm_at(self, address: int, lines: int = 10, 
                  backward: bool = False) -> str
    def disasm_function(self, address: int) -> str
    def disasm_function_json(self, address: int) -> Optional[Dict]
    def disasm_around_xref(self, xref: XRefResult, 
                           before: int = 5, after: int = 5) -> str
    def dump_bytes(self, address: int, size: int) -> str
    def dump_bytes_json(self, address: int, size: int) -> List[int]
    def read_string(self, address: int, 
                    length: Optional[int] = None) -> str
    def evaluate_expression(self, expression: str) -> Optional[int]
    def seek_to_function_start(self, address: int) -> Optional[int]
```

**Purpose**: Disassembly and code inspection.

**Radare2 Commands Used**:
- `pd {n} @ {addr}` - Disassemble forward
- `pd-- {n} @ {addr}` - Disassemble backward
- `pdf @ {addr}` - Disassemble function
- `pdfj @ {addr}` - Function disassembly as JSON
- `px {n} @ {addr}` - Hex dump
- `pxj {n} @ {addr}` - Bytes as JSON array
- `ps @ {addr}` - Read string
- `?v {expr}` - Evaluate expression
- `afij @ {addr}` - Function info

### OutputBuffer

```python
class OutputBuffer:
    def __init__(self)
    def add_section(self, title: str, content: str)
    def add_match_list(self, matches: List[MatchResult], title: str)
    def add_xref_list(self, xrefs: List[XRefResult], title: str, 
                      show_func: bool = True)
    def add_disasm(self, disasm_text: str, title: str)
    def to_string(self) -> str
    def print(self)
    def write_to_file(self, filepath: str)
```

**Purpose**: Buffer and format output sections.

### Helper Functions

```python
def format_match_list(matches: List[MatchResult], 
                     title: str = "") -> str
def format_xref_list(xrefs: List[XRefResult], title: str = "", 
                    show_func: bool = True) -> str
def format_section(title: str, content: str) -> str
def format_address(addr: int, prefix: bool = True) -> str
def format_hex_bytes(byte_array: List[int], 
                    bytes_per_line: int = 16) -> str
```

## Configuration Schema

### Config File Structure

```json
{
  "category": "string",           // MASVS category
  "description": "string",        // Demo description
  "targets": [                    // Analysis targets
    {
      "type": "function|import|string",
      "name": "string",           // Pattern to match
      "match_mode": "exact|contains|regex",
      "description": "string"     // Human-readable label
    }
  ],
  "actions": [                    // Actions to perform
    {
      "type": "list_matches|show_xrefs|disasm_around_xrefs|dump_function|dump_bytes",
      "target": "string",         // Reference to target description
      "output": "string",         // Output section title
      "before": 5,                // (disasm_around_xrefs) Lines before
      "after": 5,                 // (disasm_around_xrefs) Lines after
      "size": 32                  // (dump_bytes) Byte count
    }
  ]
}
```

### Target Types

| Type | Purpose | name Field | Example |
|------|---------|------------|---------|
| function | Match function names | Function name pattern | `"CC_MD5"`, `"CC_"` |
| import | Match import names | Import name pattern | `"getaddrinfo"`, `"socket"` |
| string | Match string content | String content pattern | `"http://"`, `"password"` |

### Action Types

| Type | Description | Required Fields | Optional Fields |
|------|-------------|-----------------|-----------------|
| list_matches | List all matches | target, output | - |
| show_xrefs | Show cross-references | target, output | - |
| disasm_around_xrefs | Disassemble around xref | target, output | before, after |
| dump_function | Dump entire function | target, output | - |
| dump_bytes | Dump bytes at address | target, output | size |

## Extension Points

### Adding New Finders

To add a new finder (e.g., ClassFinder):

```python
class ClassFinder:
    def __init__(self, session: R2Session):
        self.session = session
    
    def find_all(self) -> List[Dict[str, Any]]:
        # Use appropriate radare2 JSON command
        return self.session.cmdj("icj") or []
    
    def find_by_exact(self, name: str) -> Optional[MatchResult]:
        classes = self.find_all()
        for cls in classes:
            if cls.get("name") == name:
                return MatchResult(
                    name=cls["name"],
                    address=cls["offset"],
                    match_type="exact"
                )
        return None
    
    # Add find_by_contains, find_by_regex, find as in other finders
```

### Adding New Actions

To add a new action type:

1. Update config schema in `config.py`
2. Add action handler in `cli.py`:

```python
def _execute_action(self, action, ...):
    # ... existing code ...
    
    elif action_type == "new_action_type":
        param = action.get("param")
        for match in matches:
            # Perform action using helpers
            result = some_helper.do_something(match, param)
            section["content"].append({
                "data": result
            })
```

### Custom Output Formatters

Create custom formatters for specific output needs:

```python
class CustomFormatter:
    def format_as_sarif(self, results: Dict) -> str:
        # Convert results to SARIF format
        pass
    
    def format_as_markdown(self, results: Dict) -> str:
        # Convert results to Markdown table
        pass
```

## Testing Strategy

### Unit Tests

Mock r2pipe responses:

```python
from unittest.mock import Mock
from r2ooky import R2Session, FunctionFinder

def test_function_finder():
    # Create mock session
    session = Mock()
    session.cmdj.return_value = [
        {"name": "CC_MD5", "offset": 0x1000071a8, "size": 100},
        {"name": "CC_SHA1", "offset": 0x1000071b4, "size": 100}
    ]
    
    # Test finder
    finder = FunctionFinder(session)
    matches = finder.find_by_contains("CC_")
    
    assert len(matches) == 2
    assert matches[0].name == "CC_MD5"
    assert matches[0].address == 0x1000071a8
```

### Integration Tests

Test against real binaries:

```python
def test_with_real_binary():
    binary = "path/to/test/binary"
    
    with R2Session(binary, analyze=True) as session:
        finder = FunctionFinder(session)
        
        # Test known functions exist
        matches = finder.find_by_contains("main")
        assert len(matches) > 0
```

### Validation Tests

Compare output with original .r2 scripts:

```python
def test_output_matches_original():
    # Run r2ooky version
    r2ooky_output = run_r2ooky_demo()
    
    # Compare with original output
    original = read_file("output.txt")
    
    # Check key sections match
    assert "Uses of CommonCrypto" in r2ooky_output
    assert "xrefs to CC_MD5" in r2ooky_output
```

## Performance Considerations

### Analysis Time

- Analysis (`aaa`) can be slow on large binaries
- Consider using `aa` or targeted analysis for faster results
- Cache session across multiple queries

### Memory Usage

- JSON results are loaded into memory
- For very large binaries, consider streaming or pagination
- Close sessions promptly with context managers

### Optimization Tips

1. **Reuse Finders**: Create finder instances once
2. **Batch Queries**: Query all targets first, then process
3. **Limit Output**: Use `head_limit` for large result sets
4. **Targeted Analysis**: Don't analyze more than needed

### Performance Metrics

Typical performance on iOS binaries (arm64):

- Session open + analysis: 2-5 seconds
- Function finding: < 100ms
- Import finding: < 50ms
- String finding: 100-300ms
- XRef query: 10-50ms per target
- Disassembly: 10-100ms depending on size

## Best Practices

1. **Error Handling**
   - Check for None returns from find operations
   - Handle empty lists gracefully
   - Provide meaningful error messages

2. **Resource Management**
   - Always use context managers for R2Session
   - Close sessions explicitly if not using `with`
   - Don't keep sessions open longer than needed

3. **Output Consistency**
   - Use OutputBuffer for structured output
   - Match original .r2 script format during migration
   - Include section titles for readability

4. **Documentation**
   - Document non-obvious radare2 commands
   - Explain address resolution strategy
   - Note platform-specific behaviors

5. **Versioning**
   - Tag releases for stability
   - Document breaking changes
   - Maintain backward compatibility in configs
