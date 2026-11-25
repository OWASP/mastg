---
masvs_category: MASVS-STORAGE
platform: generic
title: API Keys and Secrets in the App Package
best-practices: [MASTG-BEST-0x02]
---

Apps identify and authenticate themselves to backend and third-party services with values such as API keys, client secrets, and access tokens. This page describes the values that are built into the app package: what kinds exist, what each kind lets a client do, and where the build stores them.

Cryptographic keys that the app uses to protect data are covered in @MASTG-KNOW-0047 for Android and @MASTG-KNOW-0069 for iOS.

## API Keys

An API key is a string that the client sends with each request, for example in a header or a query parameter (see the [OpenAPI Specification](https://spec.openapis.org/oas/v3.1.0.html#security-scheme-object)). The service uses the key to associate the request with an account or a project, for example to apply quotas and billing. An API key authenticates the calling client, not its user, which is how the [OWASP API Security Top 10](https://owasp.org/API-Security/editions/2023/en/0xa2-broken-authentication/) distinguishes it from user authentication.

Many services issue more than one kind of key and document where each kind is meant to be used:

- **Keys meant for client apps**: These identify the account and allow a limited set of operations. Examples are [Stripe publishable keys](https://docs.stripe.com/keys), [Mapbox public tokens](https://docs.mapbox.com/help/dive-deeper/access-tokens/#public-tokens), and [Firebase API keys](https://firebase.google.com/docs/projects/api-keys), which those services document as usable in apps that are distributed to users.
- **Keys meant for servers**: These allow privileged operations, such as creating charges or modifying the account's resources. Examples are Stripe secret keys and [Mapbox secret tokens](https://docs.mapbox.com/help/dive-deeper/access-tokens/#secret-tokens), which those services document as for server-side use.

Other services issue a single kind of key, and what it allows depends on how the owner configures it. A standard [Google Cloud API key](https://cloud.google.com/docs/authentication/api-keys), for example, works with any API that accepts API keys, unless API restrictions have been added to it.

## Other Secrets and Identifiers

The app package can hold other values that the app sends to a service:

- **Secrets that authenticate as an account**: OAuth client secrets, access tokens, and passwords let whoever presents them act as the client or the account they belong to. Some come in two parts: an [AWS access key](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html) consists of an access key ID and a secret access key, and a request needs both.
- **Identifiers**: Values such as an OAuth client ID, an AWS access key ID, or a Firebase app ID name the client or the project and don't grant access by themselves. Firebase, for example, [considers the content of its config file public](https://firebase.google.com/docs/projects/learn-more#config-files-objects).

For OAuth clients, [RFC 8252](https://datatracker.ietf.org/doc/html/rfc8252#section-8.5) states that a secret that's statically included in an app distributed to multiple users isn't confidential, because a user can inspect their copy. It classifies native apps as public clients, unless each instance is provisioned with its own secret.

## Restrictions

Services let the owner limit what a key can do, in two ways:

- **By operation**: The key only works for the APIs, scopes, or permissions that the owner selects. Examples are [API restrictions](https://cloud.google.com/docs/authentication/api-keys#adding-api-restrictions) in Google Cloud, [scopes](https://docs.mapbox.com/help/dive-deeper/access-tokens/#scopes) for Mapbox tokens, and [restricted API keys](https://docs.stripe.com/keys/restricted-api-keys) in Stripe.
- **By client**: The key only works for requests from the clients that the owner specifies. An example is [application restrictions](https://cloud.google.com/docs/authentication/api-keys#adding-application-restrictions) in Google Cloud, which limit a key to an Android package name and signing certificate fingerprint or to an iOS bundle ID.

The service evaluates a client restriction against values that arrive with the request. Google Cloud, for example, reads the package name, the certificate fingerprint, and the bundle ID from the `X-Android-Package`, `X-Android-Cert`, and `X-Ios-Bundle-Identifier` HTTP headers that the client sends. Because the client supplies these values itself, a client restriction doesn't establish which app sent the request, and Google [notes](https://cloud.google.com/docs/authentication/api-keys#adding-application-restrictions) that bypassing it is straightforward.

## Billing and Quotas

Because a key associates requests with an account or a project, the service counts every request made with the key against the quota of that account and, for paid services, bills it to that account. Services let the owner limit usage, for example by [capping the number of requests](https://cloud.google.com/apis/docs/capping-api-usage) per day or per user.

## Where the Values Are Stored

A value that's part of the build is stored in the app package in readable form, unless string encryption (@MASTG-KNOW-0111) is applied, which stores string literals encrypted and decrypts them at runtime. Where a value is stored depends on how it was added to the project.

### Android

- **Code constants**: string literals in Kotlin or Java code, including `BuildConfig` fields, are compiled into the Dalvik Executable (DEX) files.
- **String resources**: values in `res/values/strings.xml` are compiled into the resource table of the APK, which is where some SDKs read their key from, for example the Mapbox Maps SDK, which [reads its public token](https://docs.mapbox.com/android/maps/guides/install/) from a string resource named `mapbox_access_token`.
- **Configuration files**: values from SDK configuration files can become string resources at build time, for example those of the Firebase file `google-services.json`, which the Google services plugin [turns into Android resources](https://developers.google.com/android/guides/google-services-plugin).
- **Manifest metadata**: some SDKs read their key from a `<meta-data>` element in the `AndroidManifest.xml`, for example the Google Maps SDK, which [reads it](https://developers.google.com/maps/documentation/android-sdk/config) from `com.google.android.geo.API_KEY`.
- **Native libraries**: string literals in C or C++ code are stored in the bundled `.so` files.

### iOS

- **Code constants**: string literals in Swift or Objective-C code are stored in the `__TEXT.__cstring` section of the app binary and of each embedded framework binary.
- **`Info.plist` entries**: some SDKs read their key from the app's `Info.plist`, for example the Mapbox Maps SDK, which [reads its public token](https://docs.mapbox.com/ios/maps/guides/install/) from `MBXAccessToken`.
- **Bundled resources**: property lists, JSON files, and other configuration files are copied into the app bundle, for example the Firebase file `GoogleService-Info.plist`, which holds the API key in its `API_KEY` field.

@MASTG-DEMO-0x04 shows such values in the code and the string resources of an Android app, and @MASTG-DEMO-0x05 shows them in the binary of an iOS app.
