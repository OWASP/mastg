e scr.color=false
e bin.relocs.apply=true

?e [1] The app stores the token in the shared Keychain (Security framework):
is~SecItemAdd
izz~kSecAttrAccessGroup

?e
?e [2] The app does NOT touch the App Group shared container, so the following
?e     two queries (App Group identifier and shared file-container API) return no matches:
izz~group.org.owasp.mastestapp
izz~containerURLForSecurityApplicationGroupIdentifier
