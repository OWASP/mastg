---
platform: android
title: Dependencies with Known Vulnerabilities in the App's SBOM
id: MASTG-TEST-0274
type: [static, developer]
weakness: MASWE-0076
profiles: [L1, L2]
---

## Overview

In this test case we are identifying dependencies with known vulnerabilities by relying on a Software Bill of Material (SBOM).

## Steps

1. Use @MASTG-TECH-0130 to generate a SBOM, or request one in CycloneDX format from the development team.
2. Upload the SBOM to @MASTG-TOOL-0132.
3. Inspect the @MASTG-TOOL-0132 project for the use of vulnerable dependencies.

## Observation

The output should include a list of dependencies with names and CVE identifiers, if any.

## Evaluation

The test case fails if you can find dependencies with known vulnerabilities.
