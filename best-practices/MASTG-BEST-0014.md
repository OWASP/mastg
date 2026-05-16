---
title: Preventing Screenshots and Screen Recording
alias: preventing-screenshots-and-screen-recording
id: MASTG-BEST-0014
platform: android
---

Ensure the app hides sensitive content, such as card numbers and passcodes, from screenshots, screen recording, nonsecure displays, task switcher thumbnails, and remote screen sharing. Malware may capture screen output and extract confidential information. Protect on screen keyboards or custom keypad views as they may leak keystrokes from passcode fields. Screenshots can be saved in locations accessible to other apps or a local attacker.

Setting [`FLAG_SECURE`](https://developer.android.com/security/fraud-prevention/activities#flag_secure) on the window prevents screenshots (or appear black), blocks screen recording, and hides content on nonsecure displays and in the system task switcher.

<div style="display:flex; flex-wrap:wrap; gap:16px; align-items:flex-start; margin:16px 0;">
  <figure style="flex:1 1 220px; margin:0; text-align:center;">
    <img src="Images/Chapters/0x05d/task-switcher-without-flag-secure.png" width="200" alt="Task switcher without FLAG_SECURE">
    <figcaption>Without <code>FLAG_SECURE</code></figcaption>
  </figure>
  <figure style="flex:1 1 220px; margin:0; text-align:center;">
    <img src="Images/Chapters/0x05d/task-switcher-with-flag-secure.png" width="200" alt="Task switcher with FLAG_SECURE">
    <figcaption>With <code>FLAG_SECURE</code></figcaption>
  </figure>
</div>

You can follow the official documentation to implement `FLAG_SECURE` in your app, see ["Secure sensitive activities"](https://developer.android.com/security/fraud-prevention/activities).
