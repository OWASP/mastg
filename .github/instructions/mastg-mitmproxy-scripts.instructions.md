## mitmproxy Python scripts

This guide defines how to write and use mitmproxy scripts in MASTG demos. Scripts live with the demo and are executed by `mitmdump -s` from `run.sh` to produce Observation output.

### Scope and terminology

- “mitmproxy scripts” refers to Python addons for mitmproxy/mitmdump written against the mitmproxy scripting API.
- Tools background (installation, proxy setup, certificates) lives in @MASTG-TOOL-0097 (mitmproxy). Do not duplicate setup steps here—link to the Tools page.
- We use the command-line runner `mitmdump` in demos for reproducibility; `mitmweb` and the interactive `mitmproxy` UI are fine for exploration but not for demo automation.

### Location and naming

- Place scripts inside the demo folder; name them by purpose in snake_case (for example, `mitm_sensitive_logger.py`).
- If multiple scripts are provided, document which one to run in the demo Steps and wire it in `run.sh`.

### Invocation (run.sh)

- Canonical pattern used in demos:
  - `mitmdump -s mitm_sensitive_logger.py`
- Keep `run.sh` responsible for:
  - Starting/stopping mitmdump (foreground or background as appropriate to the demo flow).
  - Managing output files produced by the script (for example, `sensitive_data.log`).
  - Ensuring the device/emulator routes traffic through the proxy (defer details to the Tools page).

### Coding conventions

- Targeted processing via hooks:
  - Implement `def request(flow: http.HTTPFlow): ...` and/or `def response(flow: http.HTTPFlow): ...` as needed.
  - Factor common logic into helpers (for example, `process_flow(flow)`).
- Deterministic logging:
  - Write to a known output filename (for example, `sensitive_data.log`) for Observation parsing.
  - Keep log format stable: include URL, headers summary, and text body as needed.
- Sensitive data handling:
  - Define a clear list of sensitive strings or patterns at the top (for example, `SENSITIVE_DATA`), ideally documented in the demo explaining how they were identified (e.g., from the app’s Data Safety section).
  - Consider case-insensitive matching where appropriate.
- Performance and safety:
  - Avoid excessive buffering—use streaming-friendly `flow.request.text`/`flow.response.text` guarded with presence checks.
  - Wrap parsing in try/except where decoding may fail; log an informative line and continue.
  - Never print secrets to stdout unless the demo requires it; prefer file logging.

### Example (excerpt)

```python
from mitmproxy import http

SENSITIVE_DATA = {
    "precise_location_latitude": "37.7749",
    "precise_location_longitude": "-122.4194",
    "name": "John Doe",
    "email_address": "john.doe@example.com",
    "phone_number": "+11234567890",
    "credit_card_number": "1234 5678 9012 3456",
}

SENSITIVE_STRINGS = SENSITIVE_DATA.values()

def contains_sensitive_data(s: str) -> bool:
    try:
        return any(p in s for p in SENSITIVE_STRINGS)
    except Exception:
        return False

def process_flow(flow: http.HTTPFlow):
    url = flow.request.pretty_url
    req_body = flow.request.text or ""
    resp_body = flow.response.text if flow.response else ""

    if contains_sensitive_data(url) or contains_sensitive_data(req_body) or contains_sensitive_data(resp_body):
        with open("sensitive_data.log", "a", encoding="utf-8") as f:
            if flow.response:
                f.write(f"RESPONSE URL: {url}\n")
                f.write(f"Response Body: {resp_body}\n\n")
            else:
                f.write(f"REQUEST URL: {url}\n")
                f.write(f"Request Body: {req_body}\n\n")

def request(flow: http.HTTPFlow):
    process_flow(flow)

def response(flow: http.HTTPFlow):
    process_flow(flow)
```

### Logging and outputs

- Default to writing a deterministic, append-only file (for example, `sensitive_data.log`) in the demo folder. Reference it in the Observation and Evaluation sections.
- Keep the format consistent and straightforward. Avoid timestamps unless needed for the demo to keep diffs small.
- If multiple outputs are produced, document them in the demo’s Steps.

### Alignment with Tools

- Environment prep (install, certificates, proxy configuration, Android emulator notes) is documented under Tools: @MASTG-TOOL-0097. Link to that page; don’t duplicate instructions in the demo.
- If a demo requires non-default proxy ports or HTTPS sniffing modes, surface them in `run.sh` flags and document briefly in the demo body.

### Cross-links

- Tools: @MASTG-TOOL-0097 (mitmproxy)
- Techniques: @MASTG-TECH-0122 (Passive Eavesdropping), @MASTG-TECH-0123/0124/0125 (MITM Positioning)