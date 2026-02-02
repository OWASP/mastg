"""
r2ooky - Radare2 automation utilities for MASTG demos.

This package provides a clean Python API over r2pipe for common binary analysis tasks
used in MASTG demos. It replaces hardcoded .r2 scripts with dynamic analysis that works
across different binaries.
"""

from .core import R2Session
from .finder import FunctionFinder, ImportFinder, StringFinder, MatchResult
from .xref import XRefAnalyzer, XRefResult
from .disasm import DisasmHelper
from .config import load_config, DemoConfig
from .formatter import OutputBuffer, format_match_list, format_xref_list

__version__ = "0.1.0"
__all__ = [
    "R2Session",
    "FunctionFinder",
    "ImportFinder", 
    "StringFinder",
    "MatchResult",
    "XRefAnalyzer",
    "XRefResult",
    "DisasmHelper",
    "load_config",
    "DemoConfig",
    "OutputBuffer",
    "format_match_list",
    "format_xref_list",
]
