---
title: Analyzing the Network Security Configuration
platform: android
---

Once the app has been reverse engineered (see @MASTG-TECH-0013), the Network Security Configuration (NSC) file is available in the output directory. Its location is derived from the `android:networkSecurityConfig` attribute in the AndroidManifest.xml (see @MASTG-TECH-0150), where an `@xml/<filename>` reference maps to `<output_dir>/res/xml/<filename>.xml`.

For example, if the manifest contains:

```xml
android:networkSecurityConfig="@xml/network_security_config"
```

The NSC file is at `output_dir/res/xml/network_security_config.xml`.

## Using grep

Use `grep` to search for specific elements or attributes in the NSC file:

```bash
grep -i "certificates" output_dir/res/xml/network_security_config.xml
```

Example output:

```xml
<certificates src="user"/>
```

## Using yq

For structured XML queries, use `yq` on the NSC file.

List all certificate sources in trust anchors:

```bash
yq -p=xml -o=json -r '.network-security-config."base-config"."trust-anchors".certificates[]."+@src"' network_security_config.xml
```

Example output:

```txt
system
user
```

Read the cleartext traffic setting from `<base-config>`:

```bash
yq -p=xml -o=json -r '."network-security-config"."base-config"."+@cleartextTrafficPermitted" // ""' network_security_config.xml
```

Example output:

```txt
true
```

## Conversion to JSON and retrieval with jq

You can use `yq` to convert the NSC XML file to JSON for easier parsing with tools like `jq`:

```bash
yq -p=xml -o=json '.' network_security_config.xml > network_security_config.json
```

From here, you can use `jq` to query the JSON file for specific configurations, such as domains with certificate pinning:

```bash
jq -r '
  .["network-security-config"]["domain-config"][]
  | select(.["pin-set"] != null)
  | .domain
  | if type == "object" then .["+content"] else . end
' network_security_config.json
```

Example output:

```txt
example.com
api.example.org
```

## Using @MASTG-TOOL-0110

Use @MASTG-TOOL-0110 with the MASTG rules to detect insecure patterns in the NSC file. For example, to detect trust anchors that allow user-added CAs you can write a rule like:

```yaml
rules:
  - id: mastg-android-network-insecure-trust-anchors
    severity: WARNING
    languages:
      - xml
    match:
      any:
        - <certificates src="user"
```

And execute the scan with:

```bash
semgrep -c rule.yml network_security_config.xml
```

Example output:

```txt
┌────────────────┐
│ 1 Code Finding │
└────────────────┘

    network_security_config.xml
    ❯❱ rules.mastg-android-network-insecure-trust-anchors

            6┆ <certificates src="user" />
```
