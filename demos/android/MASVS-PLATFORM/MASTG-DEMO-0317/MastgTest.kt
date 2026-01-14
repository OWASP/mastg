// SUMMARY: Demonstrates protection of sensitive UI elements against overlay attacks.
// This demo contrasts insecure and secure configurations of filterTouchesWhenObscured.

// FAIL: [MASTG-TEST-0317] Sensitive input allows interaction when the window is obscured
passwordInput.filterTouchesWhenObscured = false

// PASS: [MASTG-TEST-0317] Sensitive input blocks interaction when the window is obscured
securePasswordInput.filterTouchesWhenObscured = true
