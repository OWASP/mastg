---
title: Verifying Universal Link Domain Association
platform: ios
---

A universal link is only routed to an app after iOS confirms the association between the app and the domain declared in its `applinks:` Associated Domains entitlement. iOS validates this by downloading the domain's Apple App Site Association (AASA) file and checking that it lists the app's identifier. Hosting and serving the AASA file is the **website's** responsibility, not the app's, so this verification depends on backend configuration that is outside the app package. Use this technique to retrieve the AASA file, confirm the association is correct, and check whether verification actually succeeded. For background on universal links and the AASA file, see @MASTG-KNOW-0080.

## Retrieving the AASA File

iOS fetches the AASA file over HTTPS, without redirects, from one of these locations for each declared domain:

- `https://<domain>/.well-known/apple-app-site-association`
- `https://<domain>/apple-app-site-association`

On modern iOS, the device does not fetch the file directly from the domain. Instead, it retrieves it through Apple's content delivery network, which you can also query:

```bash
curl -v "https://app-site-association.cdn-apple.com/a/v1/<domain>"
```

Inspect the returned JSON and confirm:

- The file is served over **HTTPS** with no redirects and a `Content-Type` of `application/json`.
- The `applinks` section lists the target app's identifier (`<Team ID>.<bundle ID>`) under `appIDs` (or the legacy `appID`).
- The `components` (or legacy `paths`) entries match the URL paths the app is expected to handle.

You can extract the app identifier and the declared `applinks:` domains from the app's entitlements as described in @MASTG-TECH-0111, then cross-check them against the AASA file.

## Checking the On-Device Verification Status

The presence of a valid AASA file does not by itself prove that the device verified the association. To inspect the verification state recorded on the device, use the `swcutil` command (the Shared Web Credentials / associated domains utility) from a device shell (see @MASTG-TECH-0052) or from a connected Mac:

```bash
swcutil dl
```

The output lists each app's associated domains together with the service (`applinks`) and a status such as `Verified` or an error. A non-verified domain means iOS will not open its links in the app; instead, it falls back to Safari. The `swcd` daemon also logs verification activity, which you can review with the unified logging system (see @MASTG-TECH-0060) while reinstalling the app.

## Common Reasons Verification Fails

When a domain is not verified, inspect the AASA file and the hosting setup for these common causes (see [Supporting associated domains](https://developer.apple.com/documentation/xcode/supporting-associated-domains "Supporting associated domains")):

- **Missing file**: there is no AASA file at the `.well-known` path or the root path.
- **Served over HTTP** instead of HTTPS, or behind an authentication wall.
- **Redirects**: the server redirects the request (for example, `example.com` to `www.example.com`), which iOS does not follow for the AASA file.
- **Invalid file**: the JSON is malformed, is served with the wrong `Content-Type`, or does not list the app's `appIDs`.
- **Identifier mismatch**: the `appIDs` value does not match the app's Team ID and bundle ID.
- **Subdomains**: each declared host needs an AASA file that covers it; a file on `example.com` does not automatically cover `sub.example.com`.
