e scr.color=0
e scr.interactive=false

?e
?e === 1) Storage APIs: does the app persist and read back local data? ===
?e     Foundation Data.write(to:) / Data(contentsOf:), UserDefaults
ii~Data.write,Data.contentsOf,UserDefaults

?e
?e === 2) Integrity APIs: are HMAC / signature / hash APIs referenced? ===
?e     CryptoKit HMAC, CommonCrypto CCHmac/CC_SHA, Security SecKeyCreateSignature
ii~HMAC,CCHmac,CC_SHA,SecKeyCreateSignature

?e
?e The app references both storage and HMAC APIs. Their presence alone does not
?e prove the stored data is protected, so we check where Data.write(to:) is
?e called from and whether each storage path also computes and verifies an HMAC.

?e
?e === 3) Call sites of Data.write(to:): two distinct storage flows ===
axt @ 0x100009754

?e
?e === 4) Storage flow A (fcn 0x1000050f8): writes user_profile.json and reads ===
?e     it back with NO HMAC on this data path, so the stored data is not protected
pdf @ 0x1000050f8 ~Data.write,Data.contentsOf,CryptoKit.HMAC

?e
?e === 5) Storage flow B (fcn 0x1000045bc): computes an HMAC, writes, reads back, ===
?e     and verifies it, so the stored data is integrity protected (for contrast)
pdf @ 0x1000045bc ~Data.write,Data.contentsOf,CryptoKit.HMAC
