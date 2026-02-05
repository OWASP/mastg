---
title: google-play-scraper
platform: android
source: https://github.com/facundoolano/google-play-scraper
host: [windows, linux, macOS]
---

google-play-scraper is a Node.js library that provides an easy way to scrape application data from the Google Play Store. It allows you to retrieve information about apps, including their metadata, Data Safety section, reviews, and more without requiring official API access.

## Capabilities and Use Cases

- Retrieve app details including description, developer information, and version
- Extract the Data Safety section information from the Google Play Store
- Fetch app reviews and ratings
- Search for apps by keyword
- Get app permissions and categories
- No authentication required for public data

## Installation

To use google-play-scraper, you need Node.js installed on your system. Install the package using npm:

```bash
npm install google-play-scraper
```

For global installation:

```bash
npm install -g google-play-scraper
```

## Usage

### Basic Example

You can use google-play-scraper to retrieve app information programmatically:

```javascript
const gplay = require('google-play-scraper');

gplay.app({appId: 'com.google.android.youtube'})
  .then(console.log)
  .catch(console.log);
```

### Retrieving Data Safety Information

To extract the Data Safety section:

```javascript
const gplay = require('google-play-scraper');

gplay.datasafety({appId: 'com.google.android.youtube'})
  .then(console.log, console.log);
```

### Searching for Apps

```javascript
const gplay = require('google-play-scraper');

gplay.search({
  term: "security",
  num: 10
}).then(console.log);
```

### Command-Line Usage

You can also use it from the command line by creating simple Node.js scripts:

```javascript
// get-app-info.js
const gplay = require('google-play-scraper');

const appId = process.argv[2];
if (!appId) {
  console.error('Usage: node get-app-info.js <package-id>');
  process.exit(1);
}

gplay.app({appId: appId})
  .then(data => console.log(JSON.stringify(data, null, 2)))
  .catch(console.error);
```

Run it with:

```bash
node get-app-info.js com.google.android.youtube
```

## Caveats and Limitations

- The library relies on web scraping, so changes to the Google Play Store website structure may break functionality
- Rate limiting may occur if too many requests are made in a short period
- Some data may not be available for all apps (for example, apps not published on Google Play)
- The library provides read-only access; it cannot modify app data or interact with the store
- No authentication is required for public data, but this also means you cannot access private or unpublished app information

## References

- [google-play-scraper GitHub Repository](https://github.com/facundoolano/google-play-scraper)
- [google-play-scraper npm Package](https://www.npmjs.com/package/google-play-scraper)
- [Google Play Data Safety Documentation](https://support.google.com/googleplay/android-developer/answer/10787469)
