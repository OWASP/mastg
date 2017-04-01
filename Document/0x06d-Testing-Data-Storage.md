## Testing Data Storage

### Testing Local Data Storage -- TODO [Merge with OWASP-DATAST-001-2] --

#### Overview

-- TODO [Create content for "Testing Local Data Storage" on iOS] --

#### Black-box Testing

A way to identify if sensitive information like credentials and keys are stored insecurely and without leveraging the native functions from iOS is to analyse the app data directory. It is important to trigger as much app functionality as possbile before the data is analysed, as the app might only store system credentials as specific functionality is triggered by the user. A static analysis can then be performed for the data dump based on generic keywords and app specifc data. Identify how the application stores data locally on the iOS device. Some of the possible options for the application to store it's data locally includes:

* CoreData/SQLite Databases
* NSUserDefaults
* Property List (Plist) files
* Plain files

Steps :

1. Proceed to trigger functionality that stores potential sensitive data.
2. Connect to the iOS device and browse to the following directory (this is applicable to iOS version 8.0 and higher): `/var/mobile/Containers/Data/Application/$APP_ID/`
3. Perform a grep command of the data that you have stored, such as: `grep -irn "USERID"`.
4. If the sensitive data is being stored in plaintext, it fails this test.

Manual dynamic analysis such as debugging can also be leveraged to verify how specific system credentials are stored and processed on the device. As this approach is more time consuming and is likely conducted manually, it might be only feasible for specific use cases.

#### White-box Testing

When going through the source code it should be analyzed if native mechanisms that are offered by iOS are applied to the identified sensitive information. Ideally sensitive information should not be stored on the device at all. If there is a requirement to store sensitive information on the device itself, several functions/API calls are available to protect the data on IOS device by using for example the Keychain.

#### Remediation

If sensitive information (credentials, keys, PII, etc.) is needed locally on the device, several best practices are offered by iOS that should be used to store data securely instead of reinventing the wheel or leave it unencrypted on the device.

The following is a list of best practice used for secure storage of certificates and keys and sensitve data in general:
* For small amounts of sensitive data such as credentials or keys use the [Keychain Services](https://developer.apple.com/reference/security/1658642-keychain_services?language=objc) to securely store it locally on the device. Keychain data is protected using a class structure similar to the one used in file Data Protection. These classes have behaviors equivalent to file Data Protection classes, but use distinct keys and are part of APIs that are named differently. The the default behaviour is `kSecAttrAccessibleWhenUnlocked`. For more information have a look at the available modes [Keychain Item Accessibility](https://developer.apple.com/reference/security/1658642-keychain_services/1663541-keychain_item_accessibility_cons).
* Cryptographic functions that have been self implemented to encrypt or decrypt local files should be avoided.  
* Avoid insecure storage functions for sensitive information such as credentials and keys as illustrated in chapter OMTG-DATAST-001-2.   


#### References

* [Keychain Services Programming Guide](https://developer.apple.com/library/content/documentation/Security/Conceptual/keychainServConcepts/iPhoneTasks/iPhoneTasks.html)
* [IOS Security Guide](https://www.apple.com/business/docs/iOS_Security_Guide.pdf)

### Testing for Sensitive Data Disclosure in Local Storage

#### Overview

Storing data is essential for many mobile applications, for example in order to keep track of user settings or data a user might has keyed in that needs to stored locally or offline. Data can be stored persistently by a mobile application in various ways on each of the different operating systems. The following table shows those mechanisms that are available on the iOS platform:

* CoreData/SQLite Databases
* NSUserDefaults
* Property List (Plist) files
* Plain files


#### Black-box Testing

Install and use the App as it is intended. It is important to trigger as much app functionality as possbile before the data is analysed, as the app might only store system credentials as specific functionality is triggered by the user. Afterwards check the following items:

-- TODO [Further develop section on black-box testing of "Testing for Sensitive Data Disclosure in Local Storage"] --


#### White-box Testing

##### CoreData/SQLite Databases

- `Core Data` is a framework that you use to manage the model layer objects in your application. It provides generalized and automated solutions to common tasks associated with object life cycle and object graph management, including persistence. Core Data operates on a sqlite database at lower level.

- `sqlite3`: The ‘libsqlite3.dylib’ library in framework section is required to be added in an application, which is a C++ wrapper that provides the API to the SQLite commands.


##### NSUserDefaults

The `NSUserDefaults` class provides a programmatic interface for interacting with the defaults system. The defaults system allows an application to customize its behavior to match a user’s preferences. Data saved by NSUserDefaults can be viewed from the application bundle. It also stores data in a plist file, but it's meant for smaller amounts of data.

##### Plain files / Plist files

* `NSData`: Creates static data objects, and NSMutableData creates dynamic data objects. NSData and NSMutableData are typically used for data storage and are also useful in Distributed Objects applications, where data contained in data objects can be copied or moved between applications.
  * Options for methods used to write NSData objects: `NSDataWritingWithoutOverwriting, NSDataWritingFileProtectionNone, NSDataWritingFileProtectionComplete, NSDataWritingFileProtectionCompleteUnlessOpen, NSDataWritingFileProtectionCompleteUntilFirstUserAuthentication`
  * Store Data as part of the NSData class with: `writeToFile`
* Managing File Paths:  `NSSearchPathForDirectoriesInDomains, NSTemporaryDirectory`
* The `NSFileManager` object lets you examine the contents of the file system and make changes to it. A way to create a file and write to it can be done through `createFileAtPath`.

#### Remediation

-- TODO [Add content on remediation on "Testing for Sensitive Data Disclosure in Local Storage"]

#### References

* [File System Basics](https://developer.apple.com/library/content/documentation/FileManagement/Conceptual/FileSystemProgrammingGuide/FileSystemOverview/FileSystemOverview.html)
* [Foundation Functions](https://developer.apple.com/reference/foundation/1613024-foundation_functions)
* [NSFileManager](https://developer.apple.com/reference/foundation/nsfilemanager)
* [NSUserDefaults](https://developer.apple.com/reference/foundation/userdefaults)

### Testing for Sensitive Data in Logs

#### Overview

There are many legit reasons to create log files on a mobile device, for example to keep track of crashes or errors that are stored locally when being offline and being sent to the application developer/company once online again or for usage statistics. However, logging sensitive data such as credit card number and session IDs might expose the data to attackers or malicious applications.
Log files can be created in various ways on each of the different operating systems. The following list shows the mechanisms that are available on iOS:

* NSLog Method
* printf-like function
* NSAssert-like function
* Macro

Classification of sensitive information can vary between different industries, countries and their laws and regulations. Therefore laws and regulations need to be known that are applicable to it and to be aware of what sensitive information actually is in the context of the App.

#### Black-box Testing

Proceed to a page on the iOS application that contains input fields which prompt users for their sensitive information. Two different methods are applicable to check for sensitive data in log files:

* Connect to the iOS device and execute the following command:
```
tail -f /var/log/syslog
```

* Connect your iOS device via USB and launch Xcode. Navigate to Windows > Devices, select your device and the respective application.

Proceed to complete the input fields prompt and if the sensitive data are displayed in the output of the above command, it fails this test.


#### White-box Testing

Check the source code for usage of predefined/custom Logging statements using the following keywords :
* For predefined and built-in functions:
  * NSLog
  * NSAssert
  * NSCAssert
  * fprintf
* For custom functions:
  * Logging
  * Logfile


#### Remediation

Use a define to enable NSLog statements for development and debugging, and disable these before shipping the software. This can be done by putting the following code into the appropriate PREFIX_HEADER (\*.pch) file:

```C#
#ifdef DEBUG
#   define NSLog (...) NSLog(__VA_ARGS__)
#else
#   define NSLog (...)
#endif
```

#### References

-- TODO [Add references for section "Testing for Sensitive Data in Logs"] --

### Testing Whether Sensitive Data Is Sent to Third Parties

#### Overview

-- TODO [Add content to overview of "Testing Whether Sensitive Data Is Sent to Third Parties" ] --

#### Black-box Testing

-- TODO [Add content on black-box testing of "Testing Whether Sensitive Data Is Sent to Third Parties"] --

#### White-box Testing

-- TODO [Add content on white-box testing of "Testing Whether Sensitive Data Is Sent to Third Parties"] --

#### Remediation

-- TODO [Add content on remediation of "Testing Whether Sensitive Data Is Sent to Third Parties"] --

#### References

-- TODO [Add references for "Testing Whether Sensitive Data Is Sent to Third Parties"] --

### Testing for Sensitive Data in the Keyboard Cache

#### Overview

In order to simplify keyboard input by providing autocorrection, predicative input, spell checking, etc., most of keyboard input by default is cached in /private/var/mobile/Library/Keyboard/dynamic-text.dat

This behavior is achieved by means of UITextInputTraits protocol, which is adopted by UITextField, UITextView and UISearchBar. Keyboard caching is influenced by following properties:

* `var autocorrectionType: UITextAutocorrectionType` determines whether autocorrection is enabled or disabled during typing. With autocorrection enabled, the text object tracks unknown words and suggests a more suitable replacement candidate to the user, replacing the typed text automatically unless the user explicitly overrides the action. The default value for this property is `UIText​Autocorrection​Type​Default`, which for most input methods results in autocorrection being enabled.
* `var secureTextEntry: BOOL` identifies whether text copying and text caching should be disabled and in case of UITextField hides the text being entered. This property is set to `NO` by default. 

#### Black-box Testing

1. Reset your iOS device keyboard cache by going through: Settings > General > Reset > Reset Keyboard Dictionary

2. Proceed to use the application's functionalities. Identify the functions which allow users to enter sensitive data.

3. Dump the keyboard cache file dynamic-text.dat at the following directory (Might be different in iOS below 8.0):
/private/var/mobile/Library/Keyboard/

4. Look for sensitive data such as username, passwords, email addresses, credit card numbers, etc. If the sensitive data can be obtained through the keyboard cache file, it fails this test.

#### White-box Testing

Check with the developers directly if there is any implementation to disable keyboard cache.

* Search through the source code provided to look the following similar implementations.

  ```
  textObject.autocorrectionType = UITextAutocorrectionTypeNo;
  textObject.secureTextEntry = YES;
  ```
* Open xib and storyboard files in Interface Builder and verify states of Secure Text Entry and Correction in Attributes Inspector for appropriate objects.

#### Remediation

The application must ensure that data typed into text fields which contains sensitive information must not be cached. This can be achieved by disabling the feature programmatically by using the `textObject.autocorrectionType = UITextAutocorrectionTypeNo` directive in the desired UITextFields, UITextViews and UISearchBars. For data that should be masked such as PIN and passwords, set the `textObject.secureTextEntry` to `YES`.

```#ObjC
UITextField *textField = [ [ UITextField alloc ] initWithFrame: frame ];
textField.autocorrectionType = UITextAutocorrectionTypeNo;
```

#### References

* [UIText​Input​Traits protocol](https://developer.apple.com/reference/uikit/uitextinputtraits)


### Testing for Sensitive Data in the Clipboard

#### Overview

-- TODO [Add content on overview of "Testing for Sensitive Data in the Clipboard"] --

#### Black-box Testing

Proceeed to a view in the application that has input fields which prompt the user for sensitive information such as username, password, credit card number, etc.

Enter some values and double tap on the input field.

If the "Select", "Select All", and "Paste" option shows up, proceed to tap on the "Select", or "Select All" option, it should allow you to "Cut", "Copy", "Paste", or "Define".

The "Cut" and "Copy" option should be disabled for sensitive input fields, since it will be possible to retrieve the value by pasting it.

If the sensitive input fields allow you to "Cut" or "Copy" the values, it fails this test.

#### White-box Testing

Search through the source code provided to look for any implemented subclass of `UITextField`.

```
@interface name_of_sub_class : UITextField
action == @select(cut:)
action == @select(copy:)
```

#### Remediation

Possible remediation method:

```#ObjC
@interface NoSelectTextField : UITextField

@end

@implementation NoSelectTextField

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender {
    if (action == @selector(paste:) ||
        action == @selector(cut:) ||
        action == @selector(copy:) ||
        action == @selector(select:) ||
        action == @selector(selectAll:) ||
        action == @selector(delete:) ||
        action == @selector(makeTextWritingDirectionLeftToRight:) ||
        action == @selector(makeTextWritingDirectionRightToLeft:) ||
        action == @selector(toggleBoldface:) ||
        action == @selector(toggleItalics:) ||
        action == @selector(toggleUnderline:)
        ) {
            return NO;
    }
    return [super canPerformAction:action withSender:sender];
}

@end
```

http://stackoverflow.com/questions/1426731/how-disable-copy-cut-select-select-all-in-uitextview

#### References

-- TODO [Add references for "Testing for Sensitive Data in the Clipboard"] --

### Testing Whether Sensitive Data Is Exposed via IPC Mechanisms

#### Overview

-- TODO [Add content on overview of "Testing Whether Sensitive Data Is Exposed via IPC Mechanisms"] --

#### Black-box Testing

-- TODO [Add content on black-box testing of "Testing Whether Sensitive Data Is Exposed via IPC Mechanisms"] --

#### White-box Testing

-- TODO [Add content on white-box testing of "Testing Whether Sensitive Data Is Exposed via IPC Mechanisms"] --

#### Remediation

-- TODO [Add remediation on "Testing Whether Sensitive Data Is Exposed via IPC Mechanisms"] --

#### References

-- TODO [Add references for "Testing Whether Sensitive Data Is Exposed via IPC Mechanisms"] --

### Testing for Sensitive Data Disclosure Through the User Interface

##### Overview

-- TODO [Add content on overview for "Testing for Sensitive Data Disclosure Through the User Interface"] --

#### Black-box Testing

-- TODO [Add content on black-box testing of "Testing for Sensitive Data Disclosure Through the User Interface"] --

#### White-box Testing

-- TODO [Add content on white-box testing of "Testing for Sensitive Data Disclosure Through the User Interface"] --

#### Remediation

-- TODO [Add remediation of "Testing for Sensitive Data Disclosure Through the User Interface"] --

#### References

-- TODO [Add references for "Testing for Sensitive Data Disclosure Through the User Interface"] --

### Testing for Sensitive Data in Backups

#### Overview

-- TODO [Add content on overview of "Testing for Sensitive Data in Backups"] --

#### Black-box Testing

-- TODO [Add content on black-box testing of "Testing for Sensitive Data in Backups"] --

#### White-box Testing

-- TODO [Add content on white-box testing of "Testing for Sensitive Data in Backups"] --

#### Remediation

-- TODO [Add content on remediation of "Testing for Sensitive Data in Backups"] --

#### References

-- TODO [Add references for "Testing for Sensitive Data in Backups"] --

### Testing For Sensitive Information in Auto-Generated Screenshots

#### Overview

Manufacturers want to provide device users an aesthetically pleasing effect when an application is entered or exited, hence they introduced the concept of saving a screenshot when the application goes into the background. This feature could potentially pose a security risk for an application, as the screenshot containing sensitive information (e.g. a screenshot of an email or corporate documents) is written to local storage, where it can be recovered either by a rogue application on a jailbroken device, or by someone who steals the device.

#### Black-box Testing

Proceed to a page on the application which displays sensitive information such as username, email address, account details, etc. Background the application by hitting the Home button on your iOS device. Connect to the iOS device and proceed to the following directory (Might be different in iOS below 8.0):

`/var/mobile/Containers/Data/Application/$APP_ID/Library/Caches/Snapshots/`

If the application caches the sensitive information page as a screenshot, it fails this test.

It is highly recommended to have a default screenshot that will be cached whenever the application enters background.

#### White-box Testing

While analyzing the source code, look for the fields or screens where sensitive data is involved. Identify if the application sanitize the screen before being backgrounded.

#### Remediation

Possible remediation method that will set a default screenshot:

```ObjC
@property (UIImageView *)backgroundImage;
 
- (void)applicationDidEnterBackground:(UIApplication *)application {
    UIImageView *myBanner = [[UIImageView alloc] initWithImage:@"overlayImage.png"];
    self.backgroundImage = myBanner;
    [self.window addSubview:myBanner];
}
```
This will cause the background image to be set to the "overlayImage.png" instead whenever the application is being backgrounded. It will prevent sensitive data leaks as the "overlayImage.png" will always override the current view.

#### References

-- TODO [Add references for "Testing For Sensitive Information in Auto-Generated Screenshots" ] --

### Testing the Device-Access-Security Policy

#### Overview

-- TODO [Add content for overview of "Testing the Device-Access-Security Policy"] --

#### Static Analysis

-- TODO [Add content for static analysis of "Testing the Device-Access-Security Policy"] --

#### Dynamic Analysis

-- TODO [Add content for dynamic analysis of "Testing the Device-Access-Security Policy"] --

#### Remediation

-- TODO [Add remediation of "Testing the Device-Access-Security Policy"] --

#### References

##### OWASP MASVS

- V2.11: "The app enforces a minimum device-access-security policy, such as requiring the user to set a device passcode."

##### OWASP Mobile Top 10

* M1 - Improper Platform Usage

##### CWE

- CWE: -- TODO [Add link to CWE issue] --


### Verifying User Education Controls

#### Overview

Educating users is a crucial part in the usage of mobile Apps. Even though many security controls are already in place, they might be circumvented or misused through the users.

The following list shows potential warnings or advises for a user when opening the App the first time and using it:
* App shows after starting it the first time a list of data it is storing locally and remotely. This can also be a link to an external ressource as the information might be quite extensive.
* If a new user account is created within the App it should show the user if the password provided is considered as secure and applies to best practice password policies.
* If the user is installing the App on a rooted device a warning should be shown that this is dangerous and deactivates security controls on OS level and is more likely to be prone to Malware. See also OMTG-DATAST-011 for more details.
* If a user installed the App on an outdated Android version a warning should be shown. See also OMTG-DATAST-010 for more details.

-- TODO [What else can be a warning on Android?] --

#### Static Analysis

-- TODO [Add content for static analysis of "Verifying User Education Controls"] --

#### Dynamic Analysis

After installing the App and also while using it, it should be checked if any warnings are shown to the user, that have an education purpose.

-- TODO [Further develop content of dynamic analysis of "Verifying User Education Controls"] --

#### Remediation

Warnings should be implemented that address the key points listed in the overview section.

-- TODO [Further develop remediation of "Verifying User Education Controls"] --

#### References

-- TODO [Add references for "Verifying User Education Controls"] --

##### OWASP MASVS

- V2.12: "The app educates the user about the types of personally identifiable information processed, as well as security best practices the user should follow in using the app."

##### OWASP Mobile Top 10

* M1 - Improper Platform Usage

##### CWE
- CWE: -- TODO [Add link to CWE issue for "Verifying User Education Controls"] --
