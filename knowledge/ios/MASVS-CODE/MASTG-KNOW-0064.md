---
masvs_category: MASVS-CODE
platform: ios
title: Non-Production Resources
---

Non-production resources are assets, endpoints, or configurations intended for development, testing, or staging environments rather than live production use. These resources often have relaxed security controls, debug features, or test data that can introduce vulnerabilities if inadvertently included in production builds. They can expose sensitive information, provide attack vectors, or lead to unintended behaviors in a production environment.

Common examples of non-production resources on iOS apps include:

- Debug or verbose logging frameworks that expose sensitive information.
- Test servers or API endpoints that lack proper authentication or encryption.
- Sample data files containing fake or real user information.
- Feature flags or configurations that enable insecure behaviors.
- Development certificates or provisioning profiles.
- Hardcoded credentials or API keys used for testing purposes.
