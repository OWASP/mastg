# MAS Testing Profiles

In the realm of mobile app security, adopting a structured and systematic approach is essential to ensure the robustness and resilience of mobile apps against ever-evolving threats. To address this need, the [OWASP Mobile App Security (MAS)](https://mas.owasp.org/) project provides a set of testing profiles that serve as comprehensive frameworks for verifying and enhancing the security of mobile apps.

The **MAS Testing Profiles** outline a series of security controls and tests that can be applied to evaluate mobile app security at different levels of assurance, ranging from fundamental security practices to advanced protective measures. With this, the MAS project provides a flexible framework that allows verification of the appropriate security measures based on the app's unique requirements, risk landscape, and compliance needs.

The MAS Testing Profiles can be used in various ways:

- **App Security Assessment**: Security professionals can leverage the appropriate MAS Testing Profiles based on the app's characteristics to conduct comprehensive assessments to identify vulnerabilities, gaps in security controls, and areas for improvement (e.g. cryptography or secure communication best practices).
- **Secure-by-Design Approach**: Developers can use MAS Testing Profiles as a guideline during the app development lifecycle. By incorporating security controls from the chosen profiles into the design and implementation stages, developers can proactively address security requirements and mitigate potential risks from the early stages of app development.
- **Compliance and Risk Management**: Organizations can rely on the MAS Testing Profiles to align with regulatory and compliance standards specific to the mobile app domain. By mapping the applicable security controls and tests to relevant regulations and industry best practices, organizations can ensure adherence to security requirements and demonstrate their commitment to data protection and privacy.
- **App Vetting Process:** Organizations can use the MAS Testing Profiles as the foundation for app vetting processes before deployment on the organization's devices. The profiles would be carefully tailored and potentially extended with organization-specific security requirements to meet their individual security needs and risk tolerance. See ["NIST.SP.800-163r1 \- Vetting the Security of Mobile Applications"](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-163r1.pdf) for more information.

## The MAS Testing Profiles

To maximize the effectiveness of the OWASP MASVS, applications should undergo a threat model to identify relevant threats and select appropriate security controls. When creating a custom profile isn't feasible, default **L1, L2, R, and P profiles** are available.

These profiles provide baseline security and privacy controls that cover a broad range of common threats, helping developers protect their applications even without a comprehensive threat assessment.

The profiles are divided into **two groups**, reflecting the distinct but complementary goals of **security** and **privacy**:

- **Security profiles (L1, L2, R)**: address technical threats and adversarial behavior.
- **Privacy profile (P)**: focuses on protecting users' personal data and ensuring responsible data handling.

While these defaults offer a solid foundation and can be effective for teams with limited time or resources, they may not cover unique or advanced threats. For the highest level of assurance, a custom profile based on a detailed threat model remains the recommended approach.

### Security

#### MAS-L1 - Essential Security

MAS-L1 provides a baseline for the most fundamental security requirements and best practices that every mobile app should meet to protect against **common threats**.

This profile emphasizes adhering to secure defaults provided by the OS and frameworks and implementing well-recognized security measures considered 'essential'. These include, for example, using TLS or up-to-date strong cryptography. Certain tests are included due to their minimal implementation effort relative to their significant security enhancement.

It assumes that:

- the **security controls of the mobile operating system** can be trusted (e.g. the device is not rooted/jailbroken).
- the **primary user of the device** is not viewed as an adversary.
- **other applications** installed on the device are viewed as an adversary.

MAS-L1 is recommended for

- all mobile apps as a baseline
- apps that only deal with (user) **low-risk sensitive data** and do **not contain sensitive functionality**.

#### MAS-L2 - Advanced Security

MAS-L2 extends MAS-L1, introducing additional security measures and best practices for mobile apps to address **advanced threats** requiring more rigorous threat modeling and testing strategies.

It assumes that:

- the **security controls of the mobile operating system** cannot be trusted (e.g. the device is rooted/jailbroken).
- the **primary user of the device** is not viewed as an adversary.
- **other applications** installed on the device are viewed as an adversary.
- a **third party with or without physical access** is viewed as an adversary.

MAS-L2 is recommended for

- apps that handle **high-risk sensitive data** and **contain sensitive functionality**

#### MAS-R - Resilient Security

MAS-R tries to prevent an attacker from extracting intellectual property, bypassing security controls (e.g., license checks, DRM, authentication) or negatively impacting the ecosystem (e.g. cheating at a game, unlocking paid features for free, …). It incorporates a range of security measures aimed at enhancing resilience against **reverse engineering and tampering (client-side) threats**, such as repackaging or extraction of sensitive data, IP theft (e.g., proprietary algorithms), piracy.

It assumes that:

- the **security controls of the mobile operating system** cannot be trusted (e.g. the device is rooted/jailbroken).
- the **primary user of the device** is viewed as an adversary (e.g. a reverse engineer or cheater).
- **other applications** installed on the device are viewed as an adversary.
- a **third party with or without physical access** is viewed as an adversary.

MAS-R is recommended for

- apps that have a strong need to safeguard their own **business assets and logic**.

Note that the absence of any MAS-R measures does not inherently introduce vulnerabilities. Rather, these measures offer additional, threat-specific protection to applications. However, this is provided that these apps also meet the rest of the OWASP MASVS security controls appropriate to their specific threat models. Crucially, **MAS-R is meant to augment and not replace MAS-L1 and MAS-L2.** It should not be used standalone but as an extra layer of defense, supplementing the base security controls in a defense-in-depth strategy.

Note that these measures cannot assure a 100% effectiveness, as the reverse engineer will always have full access to the device and will therefore end up succeeding given enough time and resources.

### Privacy

#### MAS-P - Baseline Privacy profile

MAS-P provides a baseline for **user privacy protection**.

This profile outlines essential practices that mobile apps should follow to protect the disclosure of users' personally identifiable information (PII) or other sensitive information, such as health, medical, or financial data, and ensure compliance with prevailing data protection regulations.

It is intended to work cohesively with other OWASP MAS profiles, such as MAS-L1 and MAS-L2, ensuring a holistic approach to both privacy and security.

MAS-P is recommended for

- all mobile apps that deal with user-sensitive data.

### Examples

<table>
  <tr>
    <th>MAS L1+P</th>
    <th>MAS L1+P+R</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>No business assets</li>
        <li>Low-risk sensitive data</li>
        <li>No sensitive functionality</li>
      </ul>
      <p><strong>Example sensitive data:</strong> name, email</p>
      <p><strong>Example apps:</strong> News (BBC News), Calendar (Google Calendar)</p>
    </td>
    <td>
      <ul>
        <li>Business assets/logic</li>
        <li>Low-risk sensitive data</li>
        <li>No sensitive functionality</li>
      </ul>
      <p><strong>Example business assets:</strong> IP, ad revenue</p>
      <p><strong>Example apps:</strong> Ad-supported Weather app</p>
    </td>
  </tr>
  <tr>
    <th>MAS L2+P</th>
    <th>MAS L2+P+R</th>
  </tr>
  <tr>
    <td>
      <ul>
        <li>No business assets</li>
        <li>Moderate/High-risk sensitive data</li>
        <li>Sensitive functionality</li>
      </ul>
      <p><strong>Example sensitive data:</strong> location, payment, health, access tokens, API keys, crypto key encrypting user data</p>
      <p><strong>Example sensitive functionality:</strong> medical record upload, in-app purchases</p>
      <p><strong>Example apps:</strong> Messenger, Health, Sport</p>
    </td>
    <td>
      <ul>
        <li>Business assets/logic</li>
        <li>Moderate/High-risk sensitive data</li>
        <li>Sensitive functionality</li>
      </ul>
      <p><strong>Example business assets:</strong> IP</p>
      <p><strong>Example sensitive functionality:</strong> money transfers, in-app purchases</p>
      <p><strong>Example apps:</strong> Banking, Insurance, Game, Entertainment</p>
    </td>
  </tr>
</table>

**Disclaimer:** The examples highlight the most representative profile for the apps and are provided for illustrative purposes only and aim to represent the different MAS profiles and suggested profile combinations. They serve to highlight the high-level differences between each profile.

<center>
<img src="Images/Chapters/0x03/example_apps_profiles.png" style="width: 40%; border-radius: 5px"/>
</center>

## Selecting and Tailoring MAS Testing Profiles

When it comes to selecting and tailoring MAS Testing Profiles, it is crucial to follow a defense-in-depth approach, understanding that **the goal is not to comply with every profile or every test within a profile**. Instead, you should take into account the app's specific [threat model](https://owasp.org/www-community/Threat_Modeling), functionality, and [data sensitivity](https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-122.pdf) to determine the most suitable testing profiles and specific tests to apply. This allows for a focused and efficient security verification process, tailored to the unique characteristics and needs of your application.

For example, some apps might not incorporate certain features, such as server communication, Multi-Factor Authentication (MFA), or biometrics. In such cases, it is unnecessary to apply tests that aren't relevant to the app's functionality. Similarly, if an app's interfaces don't handle sensitive data, they become less critical to secure.

It's important to involve all stakeholders in determining the appropriate level of security assurance based on the app's risk landscape and the potential impact of a successful attack. This collaborative approach aims to strike a balance between robust security and practicality, ensuring that resources are effectively allocated to mitigate the most relevant risks.

## About Data Protection and Privacy

When using the MASVS and the MASTG, it is necessary to **conduct a thorough threat model of your app** to determine the appropriate MAS profile. In this process, it is crucial to **consider privacy regulations and laws applicable to your organization and country**. The specific nature and sensitivity of the data often dictate the level of protection required, resulting in different MAS security controls across the profiles. For example, high-risk data that never leaves the device might require MAS-L2 for storage-related tests, but MAS-L1 could suffice for network-related tests.

As guidance, [NIST SP 800-122](https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-122.pdf) offers illustrative impact levels for Personally Identifiable Information (PII). For instance, sensitive information like Social Security Numbers (SSN), medical history, or financial account details is generally deemed more sensitive than non-sensitive data like phone numbers or ZIP codes. Nonetheless, organizations must adapt these impact levels to their specific data and comply with relevant regulations such as the [General Data Protection Regulation (GDPR)](https://gdpr-info.eu/) in Europe or the [Health Insurance Portability and Accountability Act of 1996 (HIPAA)](https://www.cdc.gov/phlp/php/resources/health-insurance-portability-and-accountability-act-of-1996-hipaa.html) in the USA.

Crafting robust policies and procedures for protecting PII confidentiality is essential. Referencing guidelines and regulations alongside the MASVS can help establish comprehensive data protection measures and ensure compliance with privacy regulations.

## References

- [OWASP MAS \- Using the MASVS](https://mas.owasp.org/MASVS/03-Using_the_MASVS/)
- [Threat Modeling | OWASP Foundation](https://owasp.org/www-community/Threat_Modeling)
- [NIST.SP.800-163r1 \- Vetting the Security of Mobile Applications](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-163r1.pdf)
- [OWASP Secure Product Design Cheat Sheet \- The principle of Defense-in-Depth](https://cheatsheetseries.owasp.org/cheatsheets/Secure_Product_Design_Cheat_Sheet.html#2-the-principle-of-defense-in-depth)
- [NIST SP 800-122, Guide to Protecting the Confidentiality of Personally Identifiable Information (PII)](https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-122.pdf)
- [General Data Protection Regulation (GDPR) - EU](https://gdpr-info.eu/)
- [Health Insurance Portability and Accountability Act of 1996 (HIPAA) - USA | CDC](https://www.cdc.gov/phlp/php/resources/health-insurance-portability-and-accountability-act-of-1996-hipaa.html)
- [Children's Online Privacy Protection Rule (COPPA) - USA](https://www.ftc.gov/legal-library/browse/rules/childrens-online-privacy-protection-rule-coppa)
- [Personal Information Protection and Electronic Documents Act (PIPEDA) - Canada](https://www.priv.gc.ca/en/privacy-topics/privacy-laws-in-canada/the-personal-information-protection-and-electronic-documents-act-pipeda/)
- [Protection of Personal Information Act (POPIA) - South Africa](https://inforegulator.org.za/popia/)
- [OWASP MASTG \- Mobile App User Privacy Protection](https://mas.owasp.org/MASTG/0x04i-Testing-User-Privacy-Protection/)
