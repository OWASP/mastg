References to URLSessionConfiguration tlsMinimumSupportedProtocolVersion setter:
0x100006d40 12 sym.imp.Foundation.URLSessionConfiguration.tlsMinimumSupportedProtocolVersion.setter

xrefs to tlsMinimumSupportedProtocolVersion setter:
sym.func.mastgTest 0x100004850 [CALL:--x] bl sym.imp.Foundation.URLSessionConfiguration.tlsMinimumSupportedProtocolVersion.setter

Code setting tlsMinimumSupportedProtocolVersion:
│           0x100004838      ldr x0, [x19, 0x10]
│           0x10000483c      mov w1, 0x302                             ; tls_protocol_version_TLSv11
│           0x100004840      bl sym.imp.Foundation.URLSessionConfiguration.tlsMinimumSupportedProtocolVersion.setter
│           0x100004844      ldr x8, [x19, 0x18]
│           0x100004848      str x8, [sp, 0x18]
