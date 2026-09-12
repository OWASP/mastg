#!/usr/bin/env bash
set -euo pipefail

cd /workspace/mas-website

python3 -m venv ./venv

source ./venv/bin/activate

pip3 install -r requirements.txt

mkdocs serve -a localhost:8000
