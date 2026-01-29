---
title: Retrieving the Google Data Safety Section
platform: android
---

The Google Play Data Safety section provides information about how an app collects, shares, and secures user data. This information is declared by app developers and helps users make informed decisions about app privacy. As a security tester, you can use this information to identify what sensitive data the app claims to handle and verify those claims during testing.

## Overview

The Data Safety section is available for all apps published on Google Play since July 2022. It contains information about:

- Data collection practices (what data types are collected)
- Data sharing practices (whether data is shared with third parties)
- Data security practices (encryption in transit, encryption at rest, ability to request data deletion)
- Data retention and deletion policies
- Whether data collection is optional or required

This information can be used as a baseline for testing, helping you identify discrepancies between what the app declares and what it actually does.

## Prerequisites

To retrieve the Data Safety section, you need:

- The app's package ID (for example, `com.google.android.youtube`)
- Internet access to the Google Play Store
- One of the following:
    - A web browser
    - @MASTG-TOOL-0145 (google-play-scraper) for automated retrieval

## Steps

### Using a Web Browser

1. Navigate to the app's Google Play Store page using the following URL format:

    ```text
    https://play.google.com/store/apps/datasafety?id=<package-id>
    ```

    Replace `<package-id>` with the app's package identifier. For example:

    ```text
    https://play.google.com/store/apps/datasafety?id=com.google.android.youtube
    ```

2. Review the Data Safety section, which includes:
      - **Data collection**: Types of data collected (location, personal info, financial info, etc.)
      - **Data sharing**: Whether data is shared with third parties
      - **Security practices**: Encryption in transit, encryption at rest, data deletion options
      - **Data usage**: Purpose of data collection (app functionality, analytics, advertising, etc.)

3. Take screenshots or save the information for comparison with actual app behavior during testing.

### Using @MASTG-TOOL-0145

1. Install google-play-scraper:

    ```bash
    npm install google-play-scraper
    ```

2. Create a script to retrieve the Data Safety information:

    ```javascript
    // get-datasafety.js
    const gplay = require('google-play-scraper');

    const appId = process.argv[2];
    if (!appId) {
      console.error('Usage: node get-datasafety.js <package-id>');
      process.exit(1);
    }

    gplay.datasafety({appId: appId})
      .then(data => {
        console.log(JSON.stringify(data, null, 2));
      })
      .catch(error => {
        console.error('Error retrieving data safety:', error.message);
      });
    ```

3. Run the script:

    ```bash
    node get-datasafety.js com.google.android.youtube
    ```

4. The output will contain structured JSON data with the Data Safety information:

    ```json
    {
      "sharedData": [
        {
          "data": "Location",
          "optional": false,
          "purpose": "App functionality, Analytics",
          "type": "Approximate location"
        }
      ],
      "collectedData": [
        {
          "data": "Personal info",
          "optional": false,
          "purpose": "Account management, App functionality",
          "type": "Name, Email address"
        }
      ],
      "securityPractices": [
        {
          "practice": "Data is encrypted in transit",
          "description": "Your data is transferred over a secure connection"
        },
        {
          "practice": "You can request that data be deleted",
          "description": "The developer provides a way for you to request that your data be deleted"
        }
      ]
    }
    ```

## Understanding the Data Safety Sections

According to the [Google Play Data Safety documentation](https://support.google.com/googleplay/android-developer/answer/10787469), the Data Safety section is organized into the following categories:

### Data Types

Apps may collect the following types of data:

- **Location**: Approximate location, Precise location
- **Personal info**: Name, Email address, User IDs, Address, Phone number, Race and ethnicity, Political or religious beliefs, Sexual orientation, Other personal info
- **Financial info**: User payment info, Purchase history, Credit score, Other financial info
- **Health and fitness**: Health info, Fitness info
- **Messages**: Emails, SMS or MMS, Other in-app messages
- **Photos and videos**: Photos, Videos
- **Audio files**: Voice or sound recordings, Music files, Other audio files
- **Files and docs**: Files and docs
- **Calendar**: Calendar events
- **Contacts**: Contacts
- **App activity**: App interactions, In-app search history, Installed apps, Other user-generated content, Other actions
- **Web browsing**: Web browsing history
- **App info and performance**: Crash logs, Diagnostics, Other app performance data
- **Device or other IDs**: Device or other IDs

### Security Practices

The Data Safety section indicates whether the app:

- Encrypts data in transit
- Encrypts data at rest
- Provides a way for users to request data deletion
- Follows the [Play Families Policy](https://support.google.com/googleplay/android-developer/answer/9893335)
- Has committed to following Google Play's [data safety requirements](https://support.google.com/googleplay/android-developer/answer/11971187)
- Has had an independent security review

### Data Usage and Handling

For each data type collected, the app should specify:

- **Collection**: Whether the data is collected
- **Sharing**: Whether the data is shared with third parties
- **Purpose**: Why the data is collected (app functionality, analytics, advertising, fraud prevention, personalization, account management)
- **Optional**: Whether data collection is optional or required for app functionality

## Validation

You should use the Data Safety section as a starting point for testing, not as definitive truth. During your security assessment:

1. Verify that all data types claimed to be collected are actually being collected
2. Identify any data types being collected that are not declared
3. Confirm that security practices (encryption, deletion capabilities) are properly implemented
4. Test whether "optional" data collection can truly be avoided
5. Verify that data sharing claims match actual network traffic analysis

Common issues to look for:

- Apps collecting sensitive data not declared in the Data Safety section
- Claims of encryption that are not implemented or are improperly configured
- Missing functionality for data deletion despite claims in the Data Safety section
- Data sharing with third parties not disclosed
- Required data collection marked as optional

## Caveats and Limitations

- The Data Safety section is self-reported by developers and not verified by Google (unless the app has undergone independent security review)
- Information may be outdated if the app has been updated since the last Data Safety declaration
- Some apps may not have a Data Safety section if they were published before the requirement or have not been updated
- The Data Safety section may not cover all edge cases or detailed implementation specifics
- Apps available outside Google Play will not have a Data Safety section

## References

- [Google Play Data Safety Section](https://developer.android.com/guide/topics/data/collect-share)
- [Data Safety Form Documentation](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Data Safety on Google Play](https://support.google.com/googleplay/answer/11416267)
