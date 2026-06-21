e scr.color=false
e bin.relocs.apply=true

?e [1] App Group identifier that backs the shared container:
izz~group.org.owasp.mastestapp

?e
?e [2] Shared-container write APIs the extension uses
?e     (a file in the shared container, and the shared UserDefaults):
izz~containerURLForSecurityApplicationGroupIdentifier
izz~setObject:forKey:

?e
?e [3] The extension reads the token from the shared Keychain (the correct source it then leaks):
is~SecItemCopyMatching
