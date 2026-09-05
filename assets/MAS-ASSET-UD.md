---
id: MAS-ASSET-UD
title: User Data (Any State)
---

This shorthand covers user data without regard to its state. Use it only when the state is not meaningful for the test, for example permission tests, where the platform mediates access to user data before the app holds it. Prefer the state-specific types `MAS-ASSET-UD-R`, `MAS-ASSET-UD-U`, and `MAS-ASSET-UD-T` wherever they apply.

Tests that protect the app itself rather than a data asset, such as binary hardening or anti-tampering, should not use this shorthand. They should not list any assets.
