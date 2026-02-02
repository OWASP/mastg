"""
Output formatting utilities for r2ooky.

Provides functions to format analysis results in various styles matching
the original .r2 script output format.
"""

from typing import List, Dict, Any
from .finder import MatchResult
from .xref import XRefResult


def format_match_list(matches: List[MatchResult], title: str = "") -> str:
    """
    Format a list of matches as text.
    
    Args:
        matches: List of MatchResult objects
        title: Optional title to print before list
        
    Returns:
        Formatted string
    """
    lines = []
    if title:
        lines.append(title + ":")
    
    for match in matches:
        lines.append(f"0x{match.address:x} {match.name}")
    
    return "\n".join(lines)


def format_xref_list(xrefs: List[XRefResult], title: str = "", 
                    show_func: bool = True) -> str:
    """
    Format a list of xrefs as text.
    
    Args:
        xrefs: List of XRefResult objects
        title: Optional title to print before list
        show_func: Whether to show function name
        
    Returns:
        Formatted string
    """
    lines = []
    if title:
        lines.append(title + ":")
    
    for xref in xrefs:
        func_info = f" (in {xref.from_func})" if show_func and xref.from_func else ""
        lines.append(f"{xref.xref_type} 0x{xref.from_addr:x}{func_info}")
    
    return "\n".join(lines)


def format_section(title: str, content: str) -> str:
    """
    Format a section with title and content.
    
    Args:
        title: Section title
        content: Section content
        
    Returns:
        Formatted string with empty line before title
    """
    return f"\n{title}:\n{content}" if content else ""


def print_section(title: str, content: str = ""):
    """
    Print a section with consistent formatting.
    
    Args:
        title: Section title
        content: Optional content to print
    """
    print()
    print(f"{title}:")
    if content:
        print(content)


def format_address(addr: int, prefix: bool = True) -> str:
    """
    Format an address consistently.
    
    Args:
        addr: Address to format
        prefix: Whether to include "0x" prefix
        
    Returns:
        Formatted address string
    """
    if prefix:
        return f"0x{addr:x}"
    else:
        return f"{addr:x}"


def format_hex_bytes(byte_array: List[int], bytes_per_line: int = 16) -> str:
    """
    Format byte array as hex dump.
    
    Args:
        byte_array: List of byte values
        bytes_per_line: Bytes to show per line
        
    Returns:
        Formatted hex dump
    """
    lines = []
    for i in range(0, len(byte_array), bytes_per_line):
        chunk = byte_array[i:i + bytes_per_line]
        hex_str = " ".join(f"{b:02x}" for b in chunk)
        lines.append(hex_str)
    return "\n".join(lines)


class OutputBuffer:
    """
    Buffer for collecting output sections that can be printed or written to file.
    """
    
    def __init__(self):
        """Initialize empty buffer."""
        self.sections = []
    
    def add_section(self, title: str, content: str):
        """
        Add a section to the buffer.
        
        Args:
            title: Section title
            content: Section content
        """
        self.sections.append((title, content))
    
    def add_match_list(self, matches: List[MatchResult], title: str):
        """
        Add a formatted match list.
        
        Args:
            matches: List of matches
            title: Section title
        """
        content = "\n".join(f"0x{m.address:x} {m.name}" for m in matches)
        self.add_section(title, content)
    
    def add_xref_list(self, xrefs: List[XRefResult], title: str, show_func: bool = True):
        """
        Add a formatted xref list.
        
        Args:
            xrefs: List of xrefs
            title: Section title
            show_func: Whether to show function names
        """
        lines = []
        for xref in xrefs:
            func_info = f" (in {xref.from_func})" if show_func and xref.from_func else ""
            lines.append(f"{xref.xref_type} 0x{xref.from_addr:x}{func_info}")
        self.add_section(title, "\n".join(lines))
    
    def add_disasm(self, disasm_text: str, title: str):
        """
        Add disassembly section.
        
        Args:
            disasm_text: Disassembly text
            title: Section title
        """
        self.add_section(title, disasm_text)
    
    def to_string(self) -> str:
        """
        Convert buffer to string.
        
        Returns:
            Complete output as string
        """
        lines = []
        for title, content in self.sections:
            lines.append("")
            lines.append(f"{title}:")
            if content:
                lines.append(content)
        return "\n".join(lines)
    
    def print(self):
        """Print the buffer to stdout."""
        print(self.to_string())
    
    def write_to_file(self, filepath: str):
        """
        Write buffer to file.
        
        Args:
            filepath: Path to output file
        """
        with open(filepath, 'w') as f:
            f.write(self.to_string())
