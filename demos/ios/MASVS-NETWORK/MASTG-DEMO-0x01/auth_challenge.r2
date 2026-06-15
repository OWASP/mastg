e scr.color=false
e asm.bytes=false
e asm.var=false

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
