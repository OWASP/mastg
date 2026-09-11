#!/bin/bash

# Run radare2 static analysis on the binary
r2 -e bin.relocs.apply=true -q -i verbose_logging.r2 -A MASTestApp > output.txt
