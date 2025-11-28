---
title: Ensure Proper Error and Exception Handling
alias: ensure-proper-error-and-exception-handling
id: MASTG-BEST-0021
platform: android
---

Secure exception and error handling in Android is about preventing the leakage of sensitive information, managing failures gracefully, and ensuring that errors do not compromise security. User-facing error messages should remain generic, while controlled logging is reserved for developers. The [OWASP DevGuide](https://devguide.owasp.org/en/12-appendices/01-implementation-dos-donts/06-exception-error-handling/) reinforces these principles with a focus on not disclosing internal details to end users, not disclosing sensitive user data to developers, and ensuring secure failure modes that do not weaken authentication or authorization.

- **Avoid leaking sensitive information**: Error messages shown to users should be generic and not reveal internal details. Logs should be sanitized to remove sensitive data and restricted to authorized personnel. The official [Log Info Disclosure](https://developer.android.com/privacy-and-security/risks/log-info-disclosure) guidance warns against including sensitive data or stack traces in production logs and recommends sanitization and reduced verbosity.
- **Fail securely**: Exceptions must not weaken security controls. Any failure in security checks should result in a **deny** outcome, blocking the action rather than allowing weaker assumptions or insecure fallbacks. Security mechanisms should default to denying access until explicitly granted, since fail-open paths are a common attack vector.
- **Validate strictly and abort on errors**: Unexpected formats or values should be treated as errors. Do not continue in a partially verified state. For example, if a network call succeeds at the transport layer but fails validation at the application layer, processing must stop. If the validation fails, do not try to sanitize the data to make the validation succeed.

See these resources for more details:

- ["OWASP - Fail Securely"](https://owasp.org/www-community/Fail_securely)
- ["OWASP - Improper Error Handling"](https://owasp.org/www-community/Improper_Error_Handling)
- ["CWE-636 - Not Failing Securely ('Failing Open')"](https://cwe.mitre.org/data/definitions/636.html)
