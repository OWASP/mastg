---
platform: ios
title: Identify Dependencies with Known Vulnerabilities by Scanning Dependency Managers Artifacts
id: MASTG-TEST-0273
type: [static, code]
weakness: MASWE-0076
profiles: [L1, L2]
---

## Overview

In this test case we are identifying dependencies with known vulnerabilities in iOS. Dependencies are integrated through dependency managers, and there might be one or more of them being used. We therefore need all of the relevant artifacts created by them to analyse them with a SCA scanning tool.

## Steps

1. Use @MASTG-TECH-0133 for an overview of the package managers and to request the relevant artifact files from the development team.
2. Use @MASTG-TECH-0133 to scan the artifact files created by the dependency managers and to look for vulnerable dependencies.

## Observation

The output should include the dependency name and the CVE identifiers for any dependency with known vulnerabilities.

## Evaluation

The test case fails if you can find dependencies with known vulnerabilities.
