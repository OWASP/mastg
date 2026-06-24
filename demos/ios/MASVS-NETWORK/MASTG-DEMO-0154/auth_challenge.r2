e scr.color=false
e asm.bytes=false
e asm.var=false

?e Custom authentication-challenge handlers (functions referencing NSURLAuthenticationChallenge):
is~NSURLAuthenticationChallenge

?e

?e Accessors into the challenge protection space (protectionSpace / serverTrust):
is~protectionSpace,serverTrust

?e

?e xrefs to URLSession challenge handler implementations:
axff @@ `f~URLSessionDelegate~didReceiveChallenge`~+didreceive

?e

?e Uses of SecTrustEvaluateWithError — shows which handlers properly evaluate server trust:
is~SecTrustEvaluateWithError

?e

?e xrefs to SecTrustEvaluateWithError:

axt @ sym.imp.SecTrustEvaluateWithError

pdf @ sym.MASTestApp.InsecureURLSessionDelegate.urlSession.allocator.didReceive.completionHandler_...o15NSURLCredentialCSgtctF_ > InsecureURLSessionDelegate.asm
pdf @ sym.MASTestApp.SecureURLSessionDelegate.urlSession.allocator.didReceive.completionHandler_...o15NSURLCredentialCSgtctF_ > SecureURLSessionDelegate.asm
