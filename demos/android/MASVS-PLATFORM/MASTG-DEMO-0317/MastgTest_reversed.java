// SUMMARY: Reverse-engineered view of overlay protection logic.

// FAIL: [MASTG-TEST-0317] Overlay interaction is not blocked.
passwordInput.setFilterTouchesWhenObscured(false);

// PASS: [MASTG-TEST-0317] Overlay interaction is blocked.
securePasswordInput.setFilterTouchesWhenObscured(true);
