#!/usr/bin/env python3
"""
MASTG-DEMO-0084: Detect uses of HTTP URLs.

This demo shows how to find strings and analyze their usage.
"""

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent.parent.parent.parent / "utils" / "r2"))

from r2ooky import R2Session, StringFinder, XRefAnalyzer, DisasmHelper
from r2ooky.formatter import OutputBuffer


def main():
    """Run the demo."""
    binary_path = Path(__file__).parent / "MASTestApp"
    output_dir = Path(__file__).parent
    
    if not binary_path.exists():
        print(f"Error: Binary not found at {binary_path}")
        return 1
    
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
                    # Show forward disassembly
                    code = disasm.disasm_at(xref.from_addr, lines=15)
                    output.add_disasm(code, f"Use of {string.name}")
        
        # Output
        output.print()
        output.write_to_file(output_dir / "output_r2ooky.txt")
    
    return 0


if __name__ == "__main__":
    sys.exit(main())
