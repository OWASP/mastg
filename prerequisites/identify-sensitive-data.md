## Identifying Sensitive Data

Classifications of sensitive information differ by industry, sector, and country. Organizations may take a restrictive view of sensitive data, and they may have a data classification policy that clearly defines sensitive information. A definition of "sensitive data" must be established before testing begins, because detecting sensitive data leakage without one may be impossible.

When no data classification policy is available, treat the following as sensitive:

- **Intellectual Property data**, whose compromise harms the organization that publishes the app:
    - API keys, secrets, or certificates that the app uses to authenticate itself to its own backends or to third-party services (such as Crashlytics or Google Maps).
    - Any organization-owned technical data used to protect other data or the system itself, such as an app-wide encryption key.
- **User data**, whose compromise harms the person using the app:
    - Personally Identifiable Information (PII) that can be abused for identity theft: Social Security numbers, credit card numbers, bank account numbers, health information.
    - User authentication information (credentials, PINs, biometric templates, etc.).
    - Financial and health information.
    - Device identifiers that may identify a person.
    - Key material derived from or bound to a specific user, and API keys, tokens, or session cookies that a user needs to access their account.
- Any data whose compromise would lead to reputational harm and/or financial costs.
- Any data whose protection is a legal obligation.

The MAS Assets taxonomy in `assets/` classifies this data further by the state in which it is accessed (at rest, in use, in transit) and is used by each test to declare which data it protects.
