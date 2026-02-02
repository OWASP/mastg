#!/usr/bin/env python3
"""
r2ooky - Command-line interface for MASTG radare2 demos.

Usage:
    r2ooky <binary> <config.json> [options]
    
Options:
    --arch ARCH     Specify architecture for fat binaries (e.g., arm64)
    --output FILE   Write output to file instead of stdout
    --json          Output in JSON format
    --no-color      Disable colored output
"""

import sys
import json
import argparse
from pathlib import Path
from typing import Dict, Any, List

from .core import R2Session
from .finder import FunctionFinder, ImportFinder, StringFinder, MatchResult
from .xref import XRefAnalyzer, XRefResult
from .disasm import DisasmHelper
from .config import load_config, DemoConfig


class R2ooky:
    """
    Main r2ooky runner that executes demo configurations.
    """
    
    def __init__(self, binary_path: str, config: DemoConfig, arch: str = None):
        """
        Initialize r2ooky runner.
        
        Args:
            binary_path: Path to binary to analyze
            config: Demo configuration
            arch: Optional architecture for fat binaries
        """
        self.binary_path = binary_path
        self.config = config
        self.arch = arch
        self.session = None
        self.matches = {}  # Store resolved matches by name
    
    def run(self) -> Dict[str, Any]:
        """
        Execute the demo configuration.
        
        Returns:
            Results dictionary
        """
        results = {
            "binary": self.binary_path,
            "category": self.config.category,
            "description": self.config.description,
            "sections": []
        }
        
        with R2Session(self.binary_path, analyze=True, arch=self.arch) as session:
            self.session = session
            
            # Initialize helpers
            func_finder = FunctionFinder(session)
            import_finder = ImportFinder(session)
            string_finder = StringFinder(session)
            xref_analyzer = XRefAnalyzer(session)
            disasm = DisasmHelper(session)
            
            # Resolve all targets first
            self._resolve_targets(func_finder, import_finder, string_finder)
            
            # Execute actions
            for action in self.config.actions:
                section = self._execute_action(
                    action, 
                    func_finder, 
                    import_finder, 
                    string_finder,
                    xref_analyzer,
                    disasm
                )
                if section:
                    results["sections"].append(section)
        
        return results
    
    def _resolve_targets(self, func_finder, import_finder, string_finder):
        """Resolve all targets defined in config."""
        for target in self.config.targets:
            target_type = target.get("type")
            name = target.get("name")
            mode = target.get("match_mode", "contains")
            
            if target_type == "function":
                matches = func_finder.find(name, mode)
            elif target_type == "import":
                matches = import_finder.find(name, mode)
            elif target_type == "string":
                matches = string_finder.find(name, mode)
            else:
                continue
            
            # Store matches by target description or name
            key = target.get("description", name)
            self.matches[key] = matches
    
    def _execute_action(self, action, func_finder, import_finder, 
                       string_finder, xref_analyzer, disasm) -> Dict[str, Any]:
        """Execute a single action."""
        action_type = action.get("type")
        target_name = action.get("target")
        output_label = action.get("output", target_name)
        
        section = {
            "title": output_label,
            "type": action_type,
            "content": []
        }
        
        # Get matches for this target
        matches = self.matches.get(target_name, [])
        
        if action_type == "list_matches":
            for match in matches:
                section["content"].append({
                    "name": match.name,
                    "address": f"0x{match.address:x}",
                    "match_type": match.match_type
                })
        
        elif action_type == "show_xrefs":
            for match in matches:
                xrefs = xref_analyzer.get_xrefs_to(match.address)
                xref_data = {
                    "target": match.name,
                    "target_address": f"0x{match.address:x}",
                    "xrefs": []
                }
                for xref in xrefs:
                    xref_data["xrefs"].append({
                        "from": f"0x{xref.from_addr:x}",
                        "type": xref.xref_type,
                        "from_func": xref.from_func
                    })
                section["content"].append(xref_data)
        
        elif action_type == "disasm_around_xrefs":
            before = action.get("before", 5)
            after = action.get("after", 5)
            
            for match in matches:
                xrefs = xref_analyzer.get_xrefs_to(match.address)
                for xref in xrefs:
                    disasm_text = disasm.disasm_around_xref(xref, before=before, after=after)
                    section["content"].append({
                        "target": match.name,
                        "xref_from": f"0x{xref.from_addr:x}",
                        "xref_from_func": xref.from_func,
                        "disassembly": disasm_text
                    })
        
        elif action_type == "dump_function":
            for match in matches:
                func_disasm = disasm.disasm_function(match.address)
                section["content"].append({
                    "function": match.name,
                    "address": f"0x{match.address:x}",
                    "disassembly": func_disasm
                })
        
        elif action_type == "dump_bytes":
            size = action.get("size", 32)
            for match in matches:
                bytes_dump = disasm.dump_bytes(match.address, size)
                section["content"].append({
                    "target": match.name,
                    "address": f"0x{match.address:x}",
                    "bytes": bytes_dump
                })
        
        return section


def format_text_output(results: Dict[str, Any]) -> str:
    """Format results as text (similar to original .r2 output)."""
    lines = []
    
    for section in results.get("sections", []):
        lines.append("")
        lines.append(section["title"] + ":")
        
        if section["type"] == "list_matches":
            for item in section["content"]:
                lines.append(f"{item['address']} {item['name']}")
        
        elif section["type"] == "show_xrefs":
            for item in section["content"]:
                for xref in item.get("xrefs", []):
                    func_info = f" (in {xref['from_func']})" if xref.get('from_func') else ""
                    lines.append(f"{xref['type']} {xref['from']}{func_info}")
        
        elif section["type"] == "disasm_around_xrefs":
            for item in section["content"]:
                lines.append(f"\n{item['disassembly']}")
        
        elif section["type"] == "dump_function":
            for item in section["content"]:
                lines.append(f"\n{item['disassembly']}")
        
        elif section["type"] == "dump_bytes":
            for item in section["content"]:
                lines.append(f"\n{item['bytes']}")
    
    return "\n".join(lines)


def main():
    """Main entry point."""
    parser = argparse.ArgumentParser(
        description="r2ooky - Radare2 automation for MASTG demos"
    )
    parser.add_argument("binary", help="Path to binary to analyze")
    parser.add_argument("config", help="Path to demo configuration JSON")
    parser.add_argument("--arch", help="Architecture for fat binaries")
    parser.add_argument("--output", help="Output file (default: stdout)")
    parser.add_argument("--json", action="store_true", help="Output JSON format")
    parser.add_argument("--no-color", action="store_true", help="Disable colors")
    
    args = parser.parse_args()
    
    # Validate inputs
    if not Path(args.binary).exists():
        print(f"Error: Binary not found: {args.binary}", file=sys.stderr)
        return 1
    
    if not Path(args.config).exists():
        print(f"Error: Config not found: {args.config}", file=sys.stderr)
        return 1
    
    try:
        # Load config
        config = load_config(args.config)
        
        # Run analysis
        runner = R2ooky(args.binary, config, arch=args.arch)
        results = runner.run()
        
        # Format output
        if args.json:
            output = json.dumps(results, indent=2)
        else:
            output = format_text_output(results)
        
        # Write output
        if args.output:
            with open(args.output, 'w') as f:
                f.write(output)
        else:
            print(output)
        
        return 0
    
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        return 1


if __name__ == "__main__":
    sys.exit(main())
