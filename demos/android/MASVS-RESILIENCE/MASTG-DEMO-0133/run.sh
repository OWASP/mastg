#!/usr/bin/env bash
set -euo pipefail

r2 -q -i root_detection.r2 -A librootcheck.so > output.txt
