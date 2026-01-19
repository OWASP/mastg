/**
 * Generates a v4 UUID
 * @returns {string} v4 UUID (e.g. "bf01006f-1d6c-4faa-8680-36818b4681bc")
 */
function generateUUID() {
  var d = new Date().getTime();
  var d2 =
    (typeof performance !== "undefined" &&
      performance.now &&
      performance.now() * 1000) ||
    0;
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
    var r = Math.random() * 16;
    if (d > 0) {
      r = (d + r) % 16 | 0;
      d = Math.floor(d / 16);
    } else {
      r = (d2 + r) % 16 | 0;
      d2 = Math.floor(d2 / 16);
    }
    return (c === "x" ? r : (r & 0x3) | 0x8).toString(16);
  });
}

// --- Native argument decoding utilities (mirroring Android native) ---
function _arrayBufferToHex(buffer) {
  try {
    var bytes = new Uint8Array(buffer);
    var hex = [];
    for (var i = 0; i < bytes.length; i++) {
      var h = bytes[i].toString(16);
      if (h.length < 2) h = "0" + h;
      hex.push(h);
    }
    return hex.join("");
  } catch (e) {
    return "<hex-conversion-error>";
  }
}

// Safely check if a memory range is readable before attempting pointer reads
function _isReadable(ptr, length) {
  try {
    if (!ptr || (ptr.isNull && ptr.isNull())) return false;
    var range = Process.findRangeByAddress(ptr);
    if (!range) return false;
    if (typeof length !== 'number' || length <= 0) return true; // any readable range is fine for cstring
    var start = ptr;
    var end = ptr.add(length);
    var rangeEnd = range.base.add(range.size);
    // Ensure the requested [start, end) lies within the readable range
    return (start.compare(range.base) >= 0) && (end.compare(rangeEnd) <= 0);
  } catch (e) {
    return false;
  }
}

// Generic byte reader returning hex and base64 (string fallback)
function _readBytes(ptr, length) {
  var result = { hex: null, base64: null };
  if (!length || length < 0) return result;
  if (!ptr || (ptr.isNull && ptr.isNull())) return result;
  try {
    if (_isReadable(ptr, length)) {
      var buf = Memory.readByteArray(ptr, length);
      if (buf) {
        result.hex = _arrayBufferToHex(buf);
      }
    }
  } catch (e) {}
  // Attempt Objective-C NSData route if hex missing and ObjC available
  if (result.hex === null && ObjC && ObjC.available) {
    try {
      var data = ObjC.classes.NSData.dataWithBytes_length_(ptr, length);
      if (data) {
        // base64
        try { result.base64 = data.base64EncodedStringWithOptions_(0).toString(); } catch (eB64) {}
        // derive hex by reading its bytes pointer
        try {
          var bytesPtr = data.bytes();
          if (_isReadable(bytesPtr, length)) {
            var buf2 = Memory.readByteArray(bytesPtr, length);
            if (buf2) result.hex = _arrayBufferToHex(buf2);
          }
        } catch (eBytes) {}
      }
    } catch (eData) {}
  }
  // Final fallbacks
  if (result.hex === null && _isReadable(ptr, length)) {
    try {
      var buf3 = Memory.readByteArray(ptr, length);
      if (buf3) result.hex = _arrayBufferToHex(buf3);
    } catch (eF) {}
  }
  return result;
}

// Base64 is the only output format for bytes

function decodeArgByDescriptor(ptr, index, desc, rawArgs, descriptors) {
  var name = desc && desc.name ? desc.name : ("args[" + index + "]");
  var type = desc && desc.type ? desc.type : "string";
  var value = null;
  var selectedFormat = null;
  try {
    switch (type) {
      case "string":
        if (ptr && !(ptr.isNull && ptr.isNull()) && _isReadable(ptr)) {
          try { value = ptr.readCString(); }
          catch (eStr) { value = ptr.toString(); }
        } else {
          value = "<null>";
        }
        break;
      case "int32":
        value = ptr.toInt32();
        break;
      case "uint32":
        value = ptr.toUInt32();
        break;
      case "int64":
        try { value = ptr.toInt64().toString(); } catch (e64) { value = ptr.toInt32(); }
        break;
      case "pointer":
        value = ptr.toString();
        break;
      case "CFData":
        try {
          if (ObjC && ObjC.available && ptr && !(ptr.isNull && ptr.isNull())) {
            var dataObj = new ObjC.Object(ptr);
            try {
              value = dataObj.base64EncodedStringWithOptions_(0).toString();
              selectedFormat = 'base64';
            } catch (eCFDB64) {
              value = "<bytes-read-error>";
              selectedFormat = 'base64';
            }
          } else {
            value = "<null>";
            selectedFormat = 'base64';
          }
        } catch (eCFD) {
          value = "<cf-data-error>";
          selectedFormat = 'base64';
        }
        break;
      case "CFDictionary":
        try {
          if (ObjC && ObjC.available && ptr && !(ptr.isNull && ptr.isNull())) {
            var dict = new ObjC.Object(ptr);
            // Best-effort conversion to plain JS object
            var js = {};
            var keys = dict.allKeys();
            for (var ki = 0; ki < keys.count(); ki++) {
              var k = keys.objectAtIndex_(ki).toString();
              var vObj = dict.objectForKey_(keys.objectAtIndex_(ki));
              var v;
              try {
                if (vObj === null) {
                  v = null;
                } else if (vObj.isKindOfClass_(ObjC.classes.NSString)) {
                  v = vObj.toString();
                } else if (vObj.isKindOfClass_(ObjC.classes.NSNumber)) {
                  v = Number(vObj.toString());
                } else if (vObj.isKindOfClass_(ObjC.classes.NSData)) {
                  v = vObj.base64EncodedStringWithOptions_(0).toString();
                } else if (vObj.isKindOfClass_(ObjC.classes.NSDictionary)) {
                  // shallow stringify
                  v = vObj.description().toString();
                } else if (vObj.isKindOfClass_(ObjC.classes.NSArray)) {
                  v = vObj.description().toString();
                } else {
                  v = vObj.toString();
                }
              } catch(eVal) { v = String(vObj); }
              js[k] = v;
            }
            value = JSON.stringify(js);
          } else {
            value = ptr ? ptr.toString() : "<null>";
          }
        } catch (eCFD) {
          value = "<cf-dictionary-error>";
        }
        break;
      case "bytes":
        var len;
        // Resolve length from lengthInArg by descriptor name if provided
        if (desc && desc.lengthInArg && Array.isArray(descriptors)) {
          var liName = desc.lengthInArg;
          var foundIdx = -1;
          for (var di = 0; di < descriptors.length; di++) {
            if (descriptors[di] && descriptors[di].name === liName) { foundIdx = di; break; }
          }
          if (foundIdx !== -1 && rawArgs && rawArgs[foundIdx]) {
            try { len = rawArgs[foundIdx].toUInt32(); } catch(eLenIn) { try { len = rawArgs[foundIdx].toInt32(); } catch(eLenIn2) { len = 64; } }
          }
        }
        // Legacy index-based dynamic length
        if (len === undefined && desc && typeof desc.lengthArgIndex === 'number' && rawArgs && rawArgs[desc.lengthArgIndex]) {
          try { len = rawArgs[desc.lengthArgIndex].toUInt32(); } catch(eLen) { len = 64; }
        }
        // Fixed length fallback
        if (len === undefined) {
          len = (desc && typeof desc.length === 'number') ? desc.length : 64;
        }
        // Defaults: base64 output, force true unless explicitly false
        var force = (desc && typeof desc.force !== 'undefined') ? !!desc.force : true;
        if (!ptr || (ptr.isNull && ptr.isNull())) {
          value = "<null>";
        } else {
          var bytesObj = _readBytes(ptr, len);
          // Unsafe fallback if both hex and base64 absent and force flag set
          if ((!bytesObj.hex && !bytesObj.base64) && force === true) {
            try {
              var bufForce = Memory.readByteArray(ptr, len);
              if (bufForce) bytesObj.hex = _arrayBufferToHex(bufForce);
            } catch(eForce) {}
          }
          value = (bytesObj.base64 !== null) ? bytesObj.base64 : "<bytes-read-error>";
          selectedFormat = 'base64';
        }
        break;
      case "bool":
        value = !!ptr.toInt32();
        break;
      case "double":
        try { value = ptr.readDouble(); } catch (ed) { value = Number(ptr.toInt32()); }
        break;
      default:
        if (_isReadable(ptr)) {
          try { value = ptr.readCString(); }
          catch(e1) {
            try { value = ptr.toInt32(); }
            catch(e2) {
              if (_isReadable(ptr, 64)) {
                try { var buf2 = Memory.readByteArray(ptr, 64); value = buf2 ? _arrayBufferToHex(buf2) : ptr.toString(); }
                catch(e3) { value = ptr.toString(); }
              } else {
                value = ptr.toString();
              }
            }
          }
        } else {
          value = ptr.toString();
        }
        break;
    }
  } catch (outer) {
    value = "<error: " + outer + ">";
  }
  var result = { name: name, type: type, value: value };
  if (type === 'bytes') {
    result.format = 'base64';
  } else if (type === 'CFData') {
    result.format = 'base64';
  }
  return result;
}

function filtersPass(decodedList, descriptors) {
  if (!descriptors || !descriptors.length) return true;
  for (var i = 0; i < descriptors.length; i++) {
    var d = descriptors[i];
    if (d && Array.isArray(d.filter) && d.filter.length) {
      var decoded = decodedList[i];
      var val = decoded ? decoded.value : null;
      var matched = false;
      for (var f = 0; f < d.filter.length; f++) {
        var term = d.filter[f];
        if (val === null || typeof val === 'undefined') continue;
        if (typeof val === 'string') {
          if (val.indexOf(term) !== -1) { matched = true; break; }
        } else if (typeof val === 'number') {
          if (val === Number(term) || (String(val).indexOf(String(term)) !== -1)) { matched = true; break; }
        } else if (typeof val === 'boolean') {
          if ((term === true || term === false) ? (val === term) : (String(val).toLowerCase() === String(term).toLowerCase())) { matched = true; break; }
        } else {
          if (String(val).indexOf(String(term)) !== -1) { matched = true; break; }
        }
      }
      if (!matched) return false;
    }
  }
  return true;
}

/**
 * Checks if a hook definition is for a native function.
 * @param {object} hook - Hook definition object.
 * @returns {boolean} True if the hook targets a native function.
 */
function isNativeHook(hook) {
  return hook.native === true;
}

/**
 * Checks if a hook definition is for an Objective-C method.
 * @param {object} hook - Hook definition object.
 * @returns {boolean} True if the hook targets an Objective-C method.
 */
function isObjCHook(hook) {
  return hook.native === true && hook.objClass !== undefined;
}

/**
 * Resolves the address of a native symbol for Interceptor.attach.
 * @param {object} hook - Native hook definition with symbol and optional module.
 * @returns {NativePointer|null} The address of the symbol, or null if not found.
 */
function resolveNativeSymbol(hook) {
  try {
    if (hook.module) {
      var mod = Process.getModuleByName(hook.module);
      return mod.getExportByName(hook.symbol);
    } else {
      return Module.getGlobalExportByName(hook.symbol);
    }
  } catch (e) {
    console.error("Failed to resolve native symbol '" + hook.symbol + "'" +
      (hook.module ? " in module '" + hook.module + "'" : "") + ": " + e);
    return null;
  }
}

/**
 * Resolves the implementation address of an Objective-C method.
 * @param {object} hook - ObjC hook definition with objClass and symbol (selector).
 * @returns {NativePointer|null} The implementation address, or null if not found.
 */
function resolveObjCMethod(hook) {
  try {
    if (!ObjC.available) {
      console.error("ObjC runtime is not available.");
      return null;
    }
    var cls = ObjC.classes[hook.objClass];
    if (!cls) {
      console.error("ObjC class '" + hook.objClass + "' not found.");
      return null;
    }
    var method = cls[hook.symbol];
    if (!method) {
      console.error("ObjC method '" + hook.symbol + "' not found in class '" + hook.objClass + "'.");
      return null;
    }
    return method.implementation;
  } catch (e) {
    console.error("Failed to resolve ObjC method '" + hook.symbol + "' in class '" + hook.objClass + "': " + e);
    return null;
  }
}

/**
 * Registers a native function hook using Frida's Interceptor API.
 * @param {object} hook - Native hook definition.
 * @param {string} categoryName - OWASP MAS category for identification.
 * @param {function} callback - Callback function for hook events.
 */
function registerNativeHook(hook, categoryName, callback) {
  var address = resolveNativeSymbol(hook);
  if (!address) {
    console.error("Cannot attach to native symbol '" + hook.symbol + "': address not resolved.");
    return;
  }

  var maxFrames = typeof hook.maxFrames === 'number' ? hook.maxFrames : 8;

  Interceptor.attach(address, {
    onEnter: function(args) {
      // Preserve raw args for potential onLeave output capture
      hook.__lastEnterArgs = [];
      for (var aiSave = 0; aiSave < 16; aiSave++) { // arbitrary upper bound
        if (args[aiSave] === undefined) break;
        hook.__lastEnterArgs[aiSave] = args[aiSave];
      }
      // Capture full native stack first (no truncation yet)
      var fullNativeStack = [];
      try {
        var btFull = Thread.backtrace(this.context, Backtracer.ACCURATE);
        for (var i = 0; i < btFull.length; i++) {
          fullNativeStack.push(DebugSymbol.fromAddress(btFull[i]).toString());
        }
      } catch (e) {
        fullNativeStack.push("<backtrace unavailable: " + e + ">");
      }

      // Backtrace filter by substring, before truncation
      if (hook.filterEventsByStacktrace) {
        var needle = hook.filterEventsByStacktrace;
        var found = false;
        for (var k = 0; k < fullNativeStack.length; k++) {
          if (fullNativeStack[k].indexOf(needle) !== -1) { found = true; break; }
        }
        if (!found) {
          return; // suppress event
        }
      }

      // Truncate for emission, unless filter present (emit full to show matching frame)
      function _truncate(arr) {
        if (hook.filterEventsByStacktrace) return arr.slice();
        if (maxFrames === -1) return arr.slice();
        var out = [];
        for (var t = 0; t < arr.length && t < maxFrames; t++) out.push(arr[t]);
        return out;
      }
      var effectiveStack = _truncate(fullNativeStack);

      // Decode args: descriptors only if provided; else auto up to 5
      var decodedArgs = [];
      var hasOutDescriptors = false;
      try {
        var descriptors = Array.isArray(hook.args) ? hook.args : [];
        if (descriptors.length > 0) {
          for (var ai = 0; ai < descriptors.length; ai++) {
            var p = args[ai];
            if (p === undefined) break;
            var d = descriptors[ai];
            if (d && (d.direction === 'out' || d.returnValue === true)) {
              hasOutDescriptors = true;
              // Placeholder; actual value resolved onLeave
              decodedArgs.push({ name: d.name || ("args["+ai+"]"), type: d.type || 'bytes', value: '<pending-out>' });
            } else {
              decodedArgs.push(decodeArgByDescriptor(p, ai, d, args, descriptors));
            }
          }
        } else {
          var autoCount = 5;
          for (var aj = 0; aj < autoCount; aj++) {
            var p2 = args[aj];
            if (p2 === undefined) break;
            var fallbackVal = null;
            try {
              if (_isReadable(p2)) {
                try { fallbackVal = p2.readCString(); }
                catch(e1) {
                  try { fallbackVal = p2.toInt32(); }
                  catch(e2) {
                    if (_isReadable(p2, 64)) {
                      try { var bufF = Memory.readByteArray(p2, 64); fallbackVal = bufF ? _arrayBufferToHex(bufF) : p2.toString(); }
                      catch(e3) { fallbackVal = p2.toString(); }
                    } else {
                      fallbackVal = p2.toString();
                    }
                  }
                }
              } else {
                fallbackVal = p2.toString();
              }
            } catch(eF) { fallbackVal = "<error: " + eF + ">"; }
            decodedArgs.push({ name: "args["+aj+"]", type: "auto", value: fallbackVal });
          }
        }
      } catch (eDec) {
        decodedArgs = [{ name: "args", type: "auto", value: "<arg-decode-error: " + eDec + ">" }];
      }

      // Apply per-arg filters
      try {
        var descriptors2 = Array.isArray(hook.args) ? hook.args : [];
        if (!filtersPass(decodedArgs, descriptors2)) {
          return;
        }
      } catch (eFilt) {}

      this._mastgEvent = {
        id: generateUUID(),
        type: "native-hook",
        category: categoryName,
        time: new Date().toISOString(),
        module: hook.module || "<global>",
        symbol: hook.symbol,
        address: address.toString(),
        stackTrace: effectiveStack,
        inputParameters: decodedArgs
      };
      // Defer emission if we have out descriptors to enrich after execution
      if (!hasOutDescriptors) {
        callback(this._mastgEvent);
      } else {
        this._deferEmit = true;
      }
    },
    onLeave: function(retval) {
      try {
        if (this._mastgEvent && this._deferEmit && Array.isArray(hook.args)) {
          var descriptors = hook.args;
          var rawArgs = hook.__lastEnterArgs || [];
          for (var di = 0; di < descriptors.length; di++) {
            var desc = descriptors[di];
            if (!(desc && (desc.direction === 'out' || desc.returnValue === true))) continue;
            // Determine pointer source
            var ptr = null;
            if (desc.returnValue === true) {
              ptr = retval;
            } else {
              ptr = rawArgs[di];
            }
            if (!ptr) continue;
            // For bytes, recalc length via lengthInArg name if present
            if (desc.type === 'bytes') {
              var len;
              if (desc.lengthInArg) {
                var nameMatch = desc.lengthInArg;
                for (var sj = 0; sj < descriptors.length; sj++) {
                  if (descriptors[sj] && descriptors[sj].name === nameMatch) {
                    try {
                      if (descriptors[sj].type === 'pointer') {
                        var lenPtr = rawArgs[sj];
                        if (lenPtr && !(lenPtr.isNull && lenPtr.isNull())) {
                          try { len = lenPtr.readU64().toNumber(); }
                          catch(eU64) { try { len = lenPtr.readU32(); } catch(eU32) { len = 0; } }
                        } else {
                          len = 0;
                        }
                      } else {
                        var lenValPtr = rawArgs[sj];
                        try { len = lenValPtr.toUInt32(); } catch(eL1) { try { len = lenValPtr.toInt32(); } catch(eL2) { len = 0; } }
                      }
                    } catch(eL) { len = 0; }
                    break;
                  }
                }
              }
              if (len === undefined) len = (typeof desc.length === 'number') ? desc.length : 0;
              if (len > 0 && _isReadable(ptr, len)) {
                var outBytesObj = _readBytes(ptr, len);
                this._mastgEvent.inputParameters[di].value = (outBytesObj.base64 !== null) ? outBytesObj.base64 : '<bytes-read-error>';
                this._mastgEvent.inputParameters[di].format = 'base64';
              } else {
                this._mastgEvent.inputParameters[di].value = '<unreadable-output>';
                this._mastgEvent.inputParameters[di].format = 'base64';
              }
            } else if (desc.type === 'CFData') {
              if (ptr && !(ptr.isNull && ptr.isNull()) && ObjC && ObjC.available) {
                try {
                  var outCFData = new ObjC.Object(ptr);
                  var b64 = outCFData.base64EncodedStringWithOptions_(0).toString();
                  this._mastgEvent.inputParameters[di].value = b64;
                  this._mastgEvent.inputParameters[di].format = 'base64';
                } catch (eCFDO) {
                  this._mastgEvent.inputParameters[di].value = '<bytes-read-error>';
                  this._mastgEvent.inputParameters[di].format = 'base64';
                }
              } else {
                this._mastgEvent.inputParameters[di].value = '<unreadable-output>';
                this._mastgEvent.inputParameters[di].format = 'base64';
              }
            } else {
              // Non-bytes output: attempt basic pointer/string/int decoding
              var outVal;
              try { outVal = ptr.readCString(); }
              catch(eStr) { try { outVal = ptr.toInt32(); } catch(eInt) { outVal = ptr.toString(); } }
              this._mastgEvent.inputParameters[di].value = outVal;
            }
          }
          this._mastgEvent.retval = retval ? retval.toString() : '<no-retval>';
          callback(this._mastgEvent);
          hook.__lastEnterArgs = undefined;
        }
      } catch(eOut) {
        if (this._mastgEvent && this._deferEmit) { callback(this._mastgEvent); }
      }
    }
  });
}

/**
 * Registers an Objective-C method hook using Frida's Interceptor API.
 * @param {object} hook - ObjC hook definition with objClass and symbol.
 * @param {string} categoryName - OWASP MAS category for identification.
 * @param {function} callback - Callback function for hook events.
 */
function registerObjCHook(hook, categoryName, callback) {
  var address = resolveObjCMethod(hook);
  if (!address) {
    console.error("Cannot attach to ObjC method '" + hook.symbol + "' in class '" + hook.objClass + "': implementation not resolved.");
    return;
  }

  var maxFrames = typeof hook.maxFrames === 'number' ? hook.maxFrames : 8;

  Interceptor.attach(address, {
    onEnter: function(args) {
      var stackTrace = [];
      try {
        var bt = Thread.backtrace(this.context, Backtracer.ACCURATE);
        for (var i = 0; i < bt.length; i++) {
          if (maxFrames !== -1 && i >= maxFrames) break;
          stackTrace.push(DebugSymbol.fromAddress(bt[i]).toString());
        }
      } catch (e) {
        stackTrace.push("<backtrace unavailable: " + e + ">");
      }

      this._mastgEvent = {
        id: generateUUID(),
        type: "objc-hook",
        category: categoryName,
        time: new Date().toISOString(),
        class: hook.objClass,
        symbol: hook.symbol,
        address: address.toString(),
        stackTrace: stackTrace
      };

      callback(this._mastgEvent);
    },
    onLeave: function(retval) {
      // Optionally emit a separate event or extend the onEnter event
    }
  });
}

// Main execution
(function() {
  function callback(event) {
    console.log(JSON.stringify(event, null, 2));
  }

  var hooksSummary = [];
  var errors = [];

  target.hooks.forEach(function(hook) {
    if (!isNativeHook(hook)) {
      console.warn("Non-native hooks are not supported on iOS. Skipping hook.");
      errors.push("Non-native hook skipped: " + JSON.stringify(hook));
      return;
    }

    try {
      if (isObjCHook(hook)) {
        registerObjCHook(hook, target.category, callback);
        hooksSummary.push({
          type: "objc",
          class: hook.objClass,
          symbol: hook.symbol
        });
      } else {
        registerNativeHook(hook, target.category, callback);
        hooksSummary.push({
          type: "native",
          module: hook.module || "<global>",
          symbol: hook.symbol
        });
      }
    } catch (e) {
      var errMsg = "Failed to register hook: " + e;
      console.error(errMsg);
      errors.push(errMsg);
    }
  });

  // Emit summary
  var summary = {
    type: "summary",
    hooks: hooksSummary,
    totalHooks: hooksSummary.length,
    errors: errors,
    totalErrors: errors.length
  };
  console.log(JSON.stringify(summary, null, 2));
})();
