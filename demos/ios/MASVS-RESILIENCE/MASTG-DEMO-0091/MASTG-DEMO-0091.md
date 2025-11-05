# MASTG-DEMO-0091: Testing Reverse Engineering Tools Detection

## Overview

This demo implements and tests various Frida detection techniques on iOS, demonstrating how apps can detect dynamic instrumentation frameworks and how these detections can be bypassed.

## Detection Techniques

### 1. Dynamic Library Scanning
Scans loaded dylibs using `_dyld_image_count()` and `_dyld_get_image_name()` to detect Frida-related libraries (frida-agent, gadget, etc.).

### 2. Port Scanning
Checks if Frida's default ports (27042, 27043) are open by attempting TCP connections to localhost.

### 3. Thread Count Analysis
Uses `task_threads()` to count active threads. Frida injection typically adds 3-5+ additional threads, triggering detection when count exceeds 12.

### 4. File System Artifacts
Scans `/tmp` and `/var/tmp` for Frida-related temporary files and named pipes.

## Running the Demo

### Prerequisites
- iOS Simulator or jailbroken device
- Frida tools installed: `pip3 install frida-tools`
- MASTestApp built and installed

### Execution
```bash
./run.sh
```

## Testing Scenarios

### Test 1: Clean Run
Launch app normally without Frida.
- **Expected**: ✅ No Frida detected

### Test 2: With Frida Attached
```bash
frida -p <PID>
```
- **Expected**: 🚨 Security alert with multiple detections

### Test 3: With Bypass
```bash
frida -f com.example.MASTestApp -l frida-bypass.js --no-pause
```
- **Expected**: ✅ No Frida detected (bypassed)

## Bypass Techniques

The bypass script uses Frida's Interceptor API to:
1. Hook `_dyld_get_image_name()` and mask Frida libraries
2. Hook `connect()` and block port checks
3. Hook `task_threads()` and normalize thread count
4. Hook `mastgTest()` directly and replace return value

## Analysis

### Difficulty Assessment
- **Trivial bypass**: Yes - single function hooks
- **Detection code identification**: Easy - clear function names
- **Custom code required**: No - standard Frida hooking
- **Time to bypass**: 10-15 minutes
- **Overall difficulty**: Low (intentional for learning)

## References
- [OWASP MASTG](https://mas.owasp.org/MASTG/)
- [Frida Documentation](https://frida.re/docs/)
- [MASTG-TEST-0091](https://mas.owasp.org/MASTG/tests/ios/MASVS-RESILIENCE/MASTG-TEST-0091/)
