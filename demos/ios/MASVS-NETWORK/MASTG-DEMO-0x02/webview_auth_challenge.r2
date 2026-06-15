e scr.color=false
e asm.bytes=false
e asm.var=false

?e xrefs to WKNavigationDelegate challenge handler implementation:
axff @@ `f~WKNavigationDelegate~didReceiveAuthenticationChallenge`~+didreceive

?e

?e [2] SecTrustEvaluateWithError calls — empty output confirms server trust is never evaluated:
is~SecTrustEvaluateWithError

?e

?e xrefs to SecTrustEvaluateWithError:

axt @ sym.imp.SecTrustEvaluateWithError

pdf @ sym.MASTestApp.InsecureWKNavigationDelegate.webView.allocator.didReceive.completionHandler_...o15NSURLCredentialCSgtctF_ > InsecureWKNavigationDelegate.asm
