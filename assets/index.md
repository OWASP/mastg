---
hide: toc
title: MAS Assets
---

MAS Assets describe **which data** a MASTG test protects and **in which state** that data is when the test applies. Each test lists its assets in the `assets` metadata field, so that for any category and state of data a tester can see which tests were run and which failed.

## Data categories

Sensitive data in mobile apps falls into two categories, depending on who is harmed when the data is compromised:

- **Intellectual Property (IP)**: compromise harms the organization that publishes the app. This includes API keys, secrets, or certificates that the app uses to authenticate itself to its own backends or to third-party services, and any organization-owned technical data used to protect other data or the system itself, such as an app-wide encryption key.
- **User Data (UD)**: compromise harms the person using the app. This includes Personally Identifiable Information (PII), authentication information (credentials, PINs, biometric templates), financial and health information, device identifiers that may identify a person, and API keys, tokens, or session cookies that a user needs to access their account.

Key material can belong to either category. A key owned by the organization and shared by all installs or users is intellectual property. A key that exists only for one user, for example one derived from the user's password or protected by the user's biometrics, is user data.

Which data counts as sensitive in the first place is defined by the organization's data classification policy. See the "Identifying Sensitive Data" prerequisite for guidance when no policy is available.

## Data states

There are three general states in which data may be accessible:

- **At rest (R)**: the data is stored in a file or data store.
- **In use (U)**: an app has loaded the data into its address space, or displays it on screen.
- **In transit (T)**: the data is being exchanged between the mobile app and an endpoint, or between processes on the device (e.g., during IPC, Inter-Process Communication).

The level of scrutiny appropriate for each state may depend on the data's importance and the likelihood of it being accessed. For example, data held in app memory may be more vulnerable to access via core dumps than data on web servers, because attackers are more likely to gain physical access to mobile devices than to web servers.

## The MAS-ASSET matrix

Combining the two categories with the three states gives six asset types, plus a category-level shorthand for each:

| Category | Data at rest | Data in use | Data in transit | Any state |
| --- | --- | --- | --- | --- |
| **Intellectual Property** | MAS-ASSET-IP-R | MAS-ASSET-IP-U | MAS-ASSET-IP-T | MAS-ASSET-IP |
| **User Data** | MAS-ASSET-UD-R | MAS-ASSET-UD-U | MAS-ASSET-UD-T | MAS-ASSET-UD |

Tests use the state-specific asset types wherever possible. The shorthands (`MAS-ASSET-IP`, `MAS-ASSET-UD`) are reserved for tests where the state is not meaningful, for example permission tests, where the platform mediates access to user data before the app ever holds it.

Tests that protect the app itself rather than a specific data asset, such as binary hardening, anti-tampering, dependency scanning, or input validation, do not list any assets. A missing `assets` field means "no specific data asset".

## Relationship to MAS Testing Profiles

MAS Testing Profiles and MAS Assets answer different questions. A profile (L1, L2, R) selects which tests to run for a given app. The assets of a test describe which data the test protects. Together they let a tester report, for each category and state of data, which tests were run and which failed.
