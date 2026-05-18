?e;?e

?e === Local Storage Read APIs (UserDefaults, Files) ===
?e

?e UserDefaults read calls:
afl~UserDefaults
afl~NSUserDefaults

?e
?e Symbols related to stringForKey / objectForKey:
afl~stringForKey
afl~objectForKey

?e
?e === HMAC / Integrity Validation (nearby operations) ===
?e

?e HMAC and CryptoKit symbols:
afl~HMAC
afl~authenticationCode
afl~SHA256

?e
?e === Cross-references ===
?e

?e xrefs to UserDefaults.string(forKey:):
axt @ sym.imp.Foundation.UserDefaults.string.forKey

?e
?e xrefs to HMAC authenticationCode:
axt @ sym.imp.CryptoKit.HMAC.authenticationCode.for.using
