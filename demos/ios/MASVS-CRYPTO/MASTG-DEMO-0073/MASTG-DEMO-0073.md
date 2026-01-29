---
platform: ios
title: Uses of Insecure Random Number Generation with r2
code: [swift]
id: MASTG-DEMO-0073
test: MASTG-TEST-0311
---

## Sample

The following sample demonstrates various methods of generating random tokens, and contrasts insecure and secure approaches. It includes:

- Insecure methods using libc `rand`, `srand`, and `drand48`.
- Other secure methods such as direct reads from `/dev/random`, `arc4random`, `arc4random_uniform`, `SystemRandomNumberGenerator`, and `CCRandomGenerateBytes`.
- A preferred secure method using `SecRandomCopyBytes`.

!!! note
    `rand` and `srand` are not part of the Swift standard library. In this demo, we call the libc `rand` and `srand` symbols via our own bindings.

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file, as described in @MASTG-TECH-0058. For this demo the path is `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 on the binary and use the `-i` option to execute the script below.

{{ run.sh # insecure_random.r2}}

This script:

- Uses `ii` to list imported symbols.
- Filters that list with `~+rand` to keep only imports whose names contain `rand`, such as `rand`, `srand`, `drand48`, `arc4random`, and `arc4random_uniform`.
- Uses `[1]` to select the address column from that output.
- Uses `axtj @@=...` to run `axt` on each of those addresses and print cross references in JSON.

## Observation

The output of the script shows cross references to calls to functions whose names contain `rand` in the sample binary.

{{ output.json }}

**Note:** the output also shows calls to secure sources such as `SecRandomCopyBytes`, `CCRandomGenerateBytes`, `SystemRandomNumberGenerator`, and the Swift `FixedWidthInteger.random` implementation. These are present in the sample for contrast, but they are not the reason the test fails. The evaluation only treats uses of insecure libc PRNGs as findings.

## Evaluation

The test fails because insecure PRNGs are used in a security relevant context. Specifically:

- `sym.imp.rand` and `sym.imp.srand`, which expose the insecure libc PRNG.
- `sym.imp.drand48`, which also uses an insecure linear congruential generator.

{{ evaluation.json # evaluate.sh}}

Now we disassemble the functions that call the insecure PRNGs to confirm their use in security-relevant contexts.

As an example, take `"fcn_name": "sym.MASTestApp.MastgTest.generateRandomTokenDrand48_...yFZ_"` from the evaluation output and disassemble or decompile it.

```bash
[0x00002fa0]> pdf @ sym.MASTestApp.MastgTest.generateRandomTokenDrand48_...yFZ_
<disassembly output>
```

Reading the disassembly confirms that it uses the output of `drand48` to generate a random token (we intentionally don't perform this step for you here, please try it yourself).

Next we look for cross references to see where it is being called from.

```bash
[0x00002fa0]> axt @ sym.MASTestApp.MastgTest.generateRandomTokenDrand48_...yFZ_
sym.MASTestApp.MastgTest.mastg.completion_...FZ_ 0x41d4 [CALL:--x] bl sym.MASTestApp.MastgTest.generateRandomTokenDrand48_...yFZ_
```

Here it is called from `sym.MASTestApp.MastgTest.mastg.completion_...FZ_`. We can disassemble that function to understand its purpose and keep iterating.

If we find that it is called from a security-relevant context, such as during authentication or cryptographic operations, we can conclude that the use of the insecure PRNG is a security issue.
