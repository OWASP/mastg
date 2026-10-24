e scr.color=false
e asm.bytes=false
e asm.var=false

?e Custom authentication-challenge handlers (functions referencing NSURLAuthenticationChallenge):
is~NSURLAuthenticationChallenge

?e

?e Accessors into the challenge protection space (protectionSpace / serverTrust):
is~protectionSpace,serverTrust

?e

?e xrefs to WKNavigationDelegate challenge handler implementation:
axff @@ `f~WKNavigationDelegate~didReceiveAuthenticationChallenge`~+didreceive

?e

?e Server-trust evaluation calls:
is~SecTrustEvaluateWithError,SecTrustEvaluateAsyncWithError

?e

?e xrefs to SecTrustEvaluateWithError:
axt @ sym.imp.SecTrustEvaluateWithError

?e

?e xrefs to SecTrustEvaluateAsyncWithError:
axt @ sym.imp.SecTrustEvaluateAsyncWithError

pdf @ sym.MASTestApp.InsecureWKNavigationDelegate.webView.allocator.didReceive.completionHandler_...o15NSURLCredentialCSgtctF_ > InsecureWKNavigationDelegate.asm
