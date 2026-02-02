"""
r2ooky - Radare2 automation utilities for MASTG demos.

This package provides a clean Python API over r2pipe for common binary analysis tasks
used in MASTG demos. It replaces hardcoded .r2 scripts with dynamic analysis that works
across different binaries.
"""

from .core import R2Session
from .finder import FunctionFinder, ImportFinder, StringFinder
from .xref import XRefAnalyzer
from .disasm import DisasmHelper
from .config import load_config, DemoConfig

__version__ = "0.1.0"
__all__ = [
    "R2Session",
    "FunctionFinder",
    "ImportFinder", 
    "StringFinder",
    "XRefAnalyzer",
    "DisasmHelper",
    "load_config",
    "DemoConfig",
]
