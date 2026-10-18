
=== 1) Storage APIs: does the app persist and read back local data? ===
Foundation Data.write(to:) / Data(contentsOf:), UserDefaults
23  0x100009724 NONE FUNC               sym.imp.Foundation.Data.contentsOf.options.URL.NSDataReadingOptions...VtKcfC
28  0x100009754 NONE FUNC               sym.imp.Foundation.Data.write.to.options.URL.NSDataWritingOptions...VtKF

=== 2) Integrity APIs: are HMAC / signature / hash APIs referenced? ===
CryptoKit HMAC, CommonCrypto CCHmac/CC_SHA, Security SecKeyCreateSignature
140 0x1000099f4 NONE FUNC               sym.imp.CryptoKit.HMAC.authenticationCode.for.using.HashedAuthentication...E0VyxGqd___AA12SymmetricKeyVt10Foundation12DataProtocolRd__lFZ
141 0x100009a00 NONE FUNC               sym.imp.CryptoKit.HMAC.isValidAuthenticationCode.authenticating.using..._0_AA12SymmetricKeyVt10Foundation15ContiguousBytesRd__AI12DataProtocolRd_0_r0_lFZ

The app references both storage and HMAC APIs. Their presence alone does not
prove the stored data is protected, so we check where Data.write(to:) is
called from and whether each storage path also computes and verifies an HMAC.

=== 3) Call sites of Data.write(to:): two distinct storage flows ===
sym.func.1000045bc 0x10000499c [CALL:--x] bl sym.imp.Foundation.Data.write.to.options.URL.NSDataWritingOptions...VtKF
sym.func.1000050f8 0x100005394 [CALL:--x] bl sym.imp.Foundation.Data.write.to.options.URL.NSDataWritingOptions...VtKF

=== 4) Storage flow A (fcn 0x1000050f8): writes user_profile.json and reads ===
it back with NO HMAC on this data path, so the stored data is not protected
│      ││   0x100005394      f0100094       bl sym.imp.Foundation.Data.write.to.options.URL.NSDataWritingOptions...VtKF
│    │ ││   0x1000054ac      9e100094       bl sym.imp.Foundation.Data.contentsOf.options.URL.NSDataReadingOptions...VtKcfC

=== 5) Storage flow B (fcn 0x1000045bc): computes an HMAC, writes, reads back, ===
and verifies it, so the stored data is integrity protected (for contrast)
│           0x100004690      d9140094       bl sym.imp.CryptoKit.HMAC.authenticationCode.for.using.HashedAuthentication...E0VyxGqd___AA12SymmetricKeyVt10Foundation12DataProtocolRd__lFZ
│   ││ │    0x10000499c      6e130094       bl sym.imp.Foundation.Data.write.to.options.URL.NSDataWritingOptions...VtKF
│   ││││    0x1000049dc      52130094       bl sym.imp.Foundation.Data.contentsOf.options.URL.NSDataReadingOptions...VtKcfC
│ ││││╎││   0x100005020      78120094       bl sym.imp.CryptoKit.HMAC.isValidAuthenticationCode.authenticating.using..._0_AA12SymmetricKeyVt10Foundation15ContiguousBytesRd__AI12DataProtocolRd_0_r0_lFZ
