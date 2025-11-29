## Identify Sensitive Screens

When testing for sensitive data exposure in screenshots, the tester must first know which screens or UI states contain confidential information based on the app's business logic and threat model. Typical examples include login forms, banking details, payment cards, personal identifiers, chat messages, medical records, or corporate documents.

During test scoping or walkthrough, developers or security teams should provide a list of app flows where sensitive data appears. The tester can then navigate to those screens and attempt to take screenshots or background the app to verify whether the data is exposed.

This context is equally important for automated testing. Automated test suites cannot reliably determine which screens contain sensitive data unless that information is explicitly documented and shared. Without it, tests may be inconsistent across different runs or environments and results may be difficult to reproduce, which weakens their value for both manual and automated analysis.

Clear communication of sensitive screens ensures that testing is targeted, consistent, and reproducible, allowing security analysts, developers, and QA teams to verify protections with confidence.
