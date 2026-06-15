## Identifying First-Party Domains

Several tests need to distinguish between first-party domains, which are under the developer's or organization's control, and third-party domains, which are operated by external providers (for example, analytics, advertising, or social SDKs).

First-party domains are the remote endpoints that support the app's core or security-sensitive functionality, such as authentication, account data, user content, or app-specific APIs. These are the domains for which controls like certificate pinning are typically expected, because the developer controls both the app and the server and can coordinate certificate or key rotation.

Third-party domains are outside the developer's control. Their certificates and keys are managed by the external provider, so applying controls such as pinning to them is often impractical and can break connectivity when the provider rotates certificates.

This information is generally not derivable from the app binary alone. Compile a list of first-party domains and services the app is expected to contact, ideally in cooperation with the development team or from architecture and infrastructure documentation. When such information is unavailable, infer likely first-party domains from the app's branding, bundle identifier, and observed traffic, and document the assumptions made.
