## Identifying First-Party Domains

Several network tests, such as those checking for certificate pinning, depend on knowing which remote endpoints are actually under the developer's control. Only these first-party domains should be evaluated against controls like pinning. Third-party domains outside the developer's control must not be reported as findings only because they appear in the app's traffic.

A domain or service is considered first-party when the developer (or their organization) owns or operates it and relies on it for the app's core or security-sensitive functionality. Common examples include:

- **Authentication and identity endpoints**, such as login, token issuance, or session management services.
- **Account and user-content APIs** that read or write the user's data.
- **App-specific backend APIs** that deliver the core functionality of the app.

By contrast, third-party domains are outside the developer's control. Their certificates and keys are managed by the external provider, so applying controls such as pinning to them is often impractical and can break connectivity when the provider rotates certificates.

The following are typically third-party and outside the scope of these controls: Analytics, crash-reporting, advertising, or social media SDKs endpoints.

This information is generally not derivable from the app binary alone. Compile a list of first-party domains and services the app is expected to contact, ideally in cooperation with the development team or from architecture and infrastructure documentation. When such information is unavailable, infer likely first-party domains from the app's branding, bundle identifier, and observed traffic, and document the assumptions made.
