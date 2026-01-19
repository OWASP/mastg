/**
 * Decodes the parameter types of a Java method.
 * @param {string} methodHeader - Java method (e.g., `function setBlockModes([Ljava.lang.String;): android.security.keystore.KeyGenParameterSpec$Builder`)
 * @returns {[string]} The decoded parameter types (e.g., "['[Ljava.lang.String;']")
 */
function parseParameterTypes(methodHeader) {
  let regex = /\((.*?)\)/;
  let parameterString = regex.exec(methodHeader)[1];
  if (parameterString === "") {
    return [];
  }
  return parameterString.replace(/ /g, "").split(",");
}

/**
 * Decodes the type of the return value of a Java method.
 * @param {string} methodHeader - Java method (e.g., "function setBlockModes([Ljava.lang.String;): android.security.keystore.KeyGenParameterSpec$Builder")
 * @returns {string} The decoded parameter types (e.g., "android.security.keystore.KeyGenParameterSpec$Builder")
 */
function parseReturnValue(methodHeader) {
  return methodHeader.split(":")[1].trim();
}

/**
 * Generates a v4 UUID
 * @returns {string} v4 UUID (e.g. "bf01006f-1d6c-4faa-8680-36818b4681bc")
 */
function generateUUID() {
  let d = new Date().getTime();
  let d2 =
      (typeof performance !== "undefined" &&
          performance.now &&
          performance.now() * 1000) ||
      0;
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
    let r = Math.random() * 16;
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


/**
 * Checks if a hook definition is for a native function.
 * @param {object} hook - Hook definition object.
 * @returns {boolean} True if the hook targets a native function.
 */
function isNativeHook(hook) {
  return hook.native === true;
}

/**
 * Resolves the address of a native symbol for Interceptor.attach.
 * @param {object} hook - Native hook definition with symbol and optional module.
 * @returns {NativePointer|null} The address of the symbol, or null if not found.
 */
function resolveNativeSymbol(hook) {
  try {
    if (hook.module) {
      let mod = Process.getModuleByName(hook.module);
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
 * Registers a native function hook using Frida's Interceptor API.
 * @param {object} hook - Native hook definition.
 * @param {string} categoryName - OWASP MAS category for identification.
 */
function registerNativeHook(hook, categoryName) {
  let address = resolveNativeSymbol(hook);
  if (!address) {
    console.error("Cannot attach to native symbol '" + hook.symbol + "': address not resolved.");
    return;
  }

  let maxFrames = typeof hook.maxFrames === 'number' ? hook.maxFrames : 8;

  Interceptor.attach(address, {
    onEnter: function(args) {
      // Capture full native stack first (no truncation yet)
      let fullNativeStack = [];
      try {
        let btFull = Thread.backtrace(this.context, Backtracer.FUZZY);
        for (let i = 0; i < btFull.length; i++) {
          try {
            fullNativeStack.push(DebugSymbol.fromAddress(btFull[i]).toString());
          } catch (e2) {
            fullNativeStack.push(btFull[i].toString());
          }
        }
      } catch (e) {
        fullNativeStack.push("<backtrace unavailable: " + e + ">");
      }

      // Capture full Java stack (no truncation yet)
      let fullJavaStack = null;
      if (Java.available) {
        try {
          let Exception = Java.use("java.lang.Exception");
          let stJavaFull = Exception.$new().getStackTrace();
          let jstFull = [];
          for (let j = 0; j < stJavaFull.length; j++) {
            jstFull.push(stJavaFull[j].toString());
          }
          fullJavaStack = jstFull;
        } catch (je) {
          // ignore
        }
      }

      // Filtering uses full stacks before truncation
      if (hook.filterEventsByStacktrace) {
        let combinedFull = (fullJavaStack && fullJavaStack.length ? fullJavaStack : fullNativeStack);
        let needle = hook.filterEventsByStacktrace;
        let found = false;
        for (let k = 0; k < combinedFull.length; k++) {
            if (combinedFull[k].indexOf(needle) !== -1) { found = true; break; }
        }
        if (!found) {
          return; // suppress event
        }
      }

      // Apply maxFrames truncation only for emission. If filtering was used, emit full stack to ensure visibility of matching frame.
      function _truncate(arr) {
        if (hook.filterEventsByStacktrace) return arr.slice();
        if (maxFrames === -1) return arr.slice();
        let out = [];
        for (let t = 0; t < arr.length && t < maxFrames; t++) out.push(arr[t]);
        return out;
      }
      let effectiveStack = fullJavaStack && fullJavaStack.length ? _truncate(fullJavaStack) : _truncate(fullNativeStack);

      // Decode native args: if descriptors provided, decode only those; else auto decode up to 5
      let decodedArgs = [];
      try {
        let descriptors = Array.isArray(hook.args) ? hook.args : [];
        if (descriptors.length > 0) {
          for (let ai = 0; ai < descriptors.length; ai++) {
            let p = args[ai];
            if (p === undefined) break;
            decodedArgs.push(decodeArgByDescriptor(p, ai, descriptors[ai]));
          }
        } else {
          // Auto mode
          let autoCount = 5;
          for (let aj = 0; aj < autoCount; aj++) {
            let p2 = args[aj];
            if (p2 === undefined) break;
            let fallbackVal = null;
            try {
              try { fallbackVal = p2.readCString(); } catch(e1) {
                try { fallbackVal = p2.toInt32(); } catch(e2) {
                  try { let bufF = Memory.readByteArray(p2, 64); fallbackVal = bufF ? _arrayBufferToHex(bufF) : p2.toString(); } catch(e3) { fallbackVal = p2.toString(); }
                }
              }
            } catch(eF) { fallbackVal = "<error: " + eF + ">"; }
            decodedArgs.push({ name: "args["+aj+"]", type: "auto", value: fallbackVal });
          }
        }
      } catch (eDec) {
        decodedArgs = [{ name: "args", type: "auto", value: "<arg-decode-error: " + eDec + ">" }];
      }

      // Apply per-arg filters (if present) before emitting
      try {
        let descriptors2 = Array.isArray(hook.args) ? hook.args : [];
        if (!filtersPass(decodedArgs, descriptors2)) {
          if (hook.debug === true) {
            console.log(JSON.stringify({ type: 'native-filter-suppressed', symbol: hook.symbol, args: decodedArgs }));
          }
          return; // suppress event when filters don't match
        }
      } catch (eFilt) {
        // If filtering fails, default to emitting
      }

      let _mastgEvent = {
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

      console.log(JSON.stringify(_mastgEvent, null, 2));
    },
    onLeave: function(retval) {
      // Optionally emit a separate event or extend the onEnter event
      // For now, we just log the return if needed
    }
  });
}


/**
 * Overloads a method. If the method is called, the parameters and the return value are decoded and together with a stack trace send back to the frida.re client.
 * @param {string} clazz - Java class (e.g., "android.security.keystore.KeyGenParameterSpec$Builder").
 * @param {string} method - Name of the method which should be overloaded (e.g., "setBlockModes").
 * @param {number} overloadIndex - If there are overloaded methods available, this number represents them (e.g., 0 for the first one)
 * @param {string} categoryName - OWASP MAS category for easier identification (e.g., "CRYPTO")
 * @param {number} maxFrames - Maximum number of stack frames to capture (default is 8, set to -1 for unlimited frames).
 */
function registerHook(
    clazz,
    method,
    overloadIndex,
    categoryName,
    maxFrames = 8
) {

  let Exception = Java.use("java.lang.Exception");
  let System = Java.use('java.lang.System');

  let toHook = Java.use(clazz)[method];

  let methodHeader = toHook.overloads[overloadIndex].toString();

  toHook.overloads[overloadIndex].implementation = function () {

    let st = Exception.$new().getStackTrace();
    let stackTrace = [];
    st.forEach(function (stElement, index) {
      if (maxFrames === -1 || index < maxFrames) {
        let stLine = stElement.toString();
        stackTrace.push(stLine);
      }
    });

    let parameterTypes = parseParameterTypes(methodHeader);
    let returnType = parseReturnValue(methodHeader);

    let instanceId;
    if (this && this.$className && typeof this.$h === 'undefined') {
      instanceId = 'static';
    } else {
      // call Java’s identityHashCode on the real object
      try {
        instanceId = System.identityHashCode(this);
      } catch (e) {
        console.error("Error in identityHashCode", e)
        instanceId = "error"
      }
    }

    const event = {
      id: generateUUID(),
      type: "hook",
      category: categoryName,
      time: new Date().toISOString(),
      class: clazz,
      method: method,
      instanceId: instanceId,
      stackTrace: stackTrace,
      inputParameters: decodeArguments(parameterTypes, arguments),
    };

    try {
      let returnValue = this[method].apply(this, arguments);
      event.returnValue = decodeArguments([returnType], [returnValue]);
      console.log(JSON.stringify(event, null, 2))
      return returnValue;
    } catch (e) {
      event.exception = e.toString();
      console.log(JSON.stringify(event, null, 2))
      throw e;
    }
  };
}

/**
 * Finds the overload index that matches the given argument types.
 * @param {Object} methodHandle - Frida method handle with overloads.
 * @param {string[]} argTypes - Array of argument type strings (e.g., ["android.net.Uri", "android.content.ContentValues"]).
 * @returns {number} The index of the matching overload, or -1 if not found.
 */
function findOverloadIndex(methodHandle, argTypes) {
  for (let i = 0; i < methodHandle.overloads.length; i++) {
    let overload = methodHandle.overloads[i];
    let parameterTypes = parseParameterTypes(overload.toString());

    if (parameterTypes.length !== argTypes.length) {
      continue;
    }

    let match = true;
    for (let j = 0; j < argTypes.length; j++) {
      if (parameterTypes[j] !== argTypes[j]) {
        match = false;
        break;
      }
    }

    if (match) {
      return i;
    }
  }
  return -1;
}

/**
 * Builds a normalized list of hook operations for a single hook definition.
 * Each operation contains clazz, method, overloadIndex, and args array (decoded parameter types).
 * This centralizes selection logic used for both summary emission and hook registration.
 *
 * The function supports several hook configuration scenarios:
 * - If both `methods` and `overloads` are specified, the configuration is considered invalid and no operations are returned.
 * - If a single `method` and an explicit list of `overloads` are provided, only those overloads are considered.
 * - If only `methods` is provided, all overloads for each method are included.
 * - If only `method` is provided, all overloads for that method are included.
 * - If neither is provided, or if the configuration is invalid, no operations are returned.
 *
 * Error handling:
 * - If an explicit overload is not found, it is skipped and not included in the operations.
 * - If an exception occurs during processing, it is logged and the function returns the operations collected so far.
 *
 * @param {object} hook - Hook definition object. Supported formats:
 *   - { class: string, method: string }
 *   - { class: string, methods: string[] }
 *   - { class: string, method: string, overloads: Array<{ args: string[] }> }
 * @returns {{operations: Array<{clazz:string, method:string, overloadIndex:number, args:string[]}>, count:number}}
 *
 * @example
 * // Hook all overloads of a single method
 * buildHookOperations({ class: "android.net.Uri", method: "parse" });
 *
 * @example
 * // Hook all overloads of multiple methods
 * buildHookOperations({ class: "android.net.Uri", methods: ["parse", "toString"] });
 *
 * @example
 * // Hook specific overloads of a method
 * buildHookOperations({
 *   class: "android.net.Uri",
 *   method: "parse",
 *   overloads: [
 *     { args: ["java.lang.String"] },
 *     { args: ["android.net.Uri"] }
 *   ]
 * });
 *
 * @example
 * // Invalid configuration: both methods and overloads
 * buildHookOperations({
 *   class: "android.net.Uri",
 *   methods: ["parse"],
 *   overloads: [{ args: ["java.lang.String"] }]
 * });
 * // Returns { operations: [], count: 0 }
 */
function buildHookOperations(hook) {
  let operations = [];
  let errors = [];

  try {
    // Invalid configuration: methods + overloads (logged elsewhere)
    if (hook.methods && hook.overloads && hook.overloads.length > 0) {
      let errInvalid = "Invalid hook configuration for " + hook.class + ": 'overloads' is only supported with a singular 'method', not with 'methods'.";
      console.error(errInvalid);
      errors.push(errInvalid);
      return {operations: operations, count: 0, errors: errors, errorCount: errors.length};
    }

    // Explicit overload list for single method
    if (hook.method && hook.overloads && hook.overloads.length > 0) {
      try {
        let handle = Java.use(hook.class)[hook.method];
        for (let o = 0; o < hook.overloads.length; o++) {
          let def = hook.overloads[o];
          let argsExplicit = Array.isArray(def.args) ? def.args : [];
          let idx = findOverloadIndex(handle, argsExplicit);
          if (idx !== -1) {
            let params = parseParameterTypes(handle.overloads[idx].toString());
            operations.push({clazz: hook.class, method: hook.method, overloadIndex: idx, args: params});
          } else {
            console.warn(
                "[frida-android] Warning: Overload not found for class '" +
                hook.class +
                "', method '" +
                hook.method +
                "', args [" +
                argsExplicit.join(", ") +
                "]. This hook will be skipped."
            );
            errors.push("Overload not found for " + hook.class + ":" + hook.method + " with args [" + argsExplicit.join(", ") + "]");
          }
        }
      } catch (e) {
        let errMsg = "Failed to process method '" + hook.method + "' in class '" + hook.class + "': " + e;
        console.warn("Warning: " + errMsg);
        errors.push(errMsg);
      }
      return {operations: operations, count: operations.length, errors: errors, errorCount: errors.length};
    }

    // Single method without explicit overloads: all overloads
    if (hook.method && (!hook.overloads || hook.overloads.length === 0)) {
      try {
        let handleAll = Java.use(hook.class)[hook.method];
        for (let i = 0; i < handleAll.overloads.length; i++) {
          let paramsAll = parseParameterTypes(handleAll.overloads[i].toString());
          operations.push({clazz: hook.class, method: hook.method, overloadIndex: i, args: paramsAll});
        }
      } catch (e) {
        let errMsg2 = "Failed to process method '" + hook.method + "' in class '" + hook.class + "': " + e;
        console.warn("Warning: " + errMsg2);
        errors.push(errMsg2);
      }
      return {operations: operations, count: operations.length, errors: errors, errorCount: errors.length};
    }

    // Multiple methods: all overloads for each
    if (hook.methods) {
      for (let m = 0; m < hook.methods.length; m++) {
        let mName = hook.methods[m];
        try {
          let handleEach = Java.use(hook.class)[mName];
          for (let j = 0; j < handleEach.overloads.length; j++) {
            let paramsEach = parseParameterTypes(handleEach.overloads[j].toString());
            operations.push({clazz: hook.class, method: mName, overloadIndex: j, args: paramsEach});
          }
        } catch (e) {
          let errMsg3 = "Failed to process method '" + mName + "' in class '" + hook.class + "': " + e;
          console.warn("Warning: " + errMsg3);
          errors.push(errMsg3);
        }
      }
      return {operations: operations, count: operations.length, errors: errors, errorCount: errors.length};
    }
  } catch (e) {
    // Log the error to aid debugging; returning partial results
    let errMsg4 = "Error in buildHookOperations for hook: " + (hook && hook.class ? hook.class : "<unknown>") + ": " + e;
    console.error(errMsg4);
    errors.push(errMsg4);
  }

  return {operations: operations, count: operations.length, errors: errors, errorCount: errors.length};
}

/**
 * Takes an array of objects usually defined in the `hooks.js` file of a DEMO and loads all classes and functions stated in there.
 * @param {[object]} hook - Contains a list of objects which contains all methods which will be overloaded.
 *   Basic format: {class: "android.security.keystore.KeyGenParameterSpec$Builder", methods: ["setBlockModes"]}
 *   With overloads: {class: "android.content.ContentResolver", method: "insert", overloads: [{args: ["android.net.Uri", "android.content.ContentValues"]}]}
 * @param {string} categoryName - OWASP MAS category for easier identification (e.g., "CRYPTO")
 * @param {{operations: Array<{clazz:string, method:string, overloadIndex:number, args:string[]}>, count:number}} [cachedOperations] - Optional pre-computed hook operations to avoid redundant processing.
 */
function registerAllHooks(hook, categoryName, cachedOperations) {
  if (hook.methods && hook.overloads && hook.overloads.length > 0) {
    console.error(`Invalid hook configuration for ${hook.class}: 'overloads' is only supported with a singular 'method', not with 'methods'.`);
    return;
  }
  let built = cachedOperations || buildHookOperations(hook);
  built.operations.forEach(function (op) {
    try {
      registerHook(op.clazz, op.method, op.overloadIndex, categoryName, hook.maxFrames);
    } catch (err) {
      console.error(err);
      console.error(`Problem when overloading ${op.clazz}:${op.method}#${op.overloadIndex}`);
    }
  });
}

// Main execution: separate native hooks from Java hooks
(function() {
  // Separate hooks into native and Java categories
  let nativeHooks = [];
  let javaHooks = [];
  target.hooks.forEach(function(hook) {
    if (isNativeHook(hook)) {
      nativeHooks.push(hook);
    } else {
      javaHooks.push(hook);
    }
  });

  // Prepare native summary upfront without attaching hooks yet
  let nativeHooksSummary = [];
  let nativeErrors = [];
  nativeHooks.forEach(function(hook) {
    try {
      // Attempt to resolve symbol to surface errors early, but do not attach
      let addr = resolveNativeSymbol(hook);
      if (!addr) {
        nativeErrors.push("Failed to resolve native symbol '" + hook.symbol + "'" + (hook.module ? " in module '" + hook.module + "'" : ""));
      }
      nativeHooksSummary.push({
        module: hook.module || "<global>",
        symbol: hook.symbol
      });
    } catch (e) {
      let errMsg = "Failed to resolve native hook for symbol '" + hook.symbol + "': " + e;
      console.error(errMsg);
      nativeErrors.push(errMsg);
    }
  });

  // Register hooks inside Java.perform, but only after emitting both summaries
  // Enter Java.perform to allow Java stack augmentation (even if only native hooks)
  Java.perform(function() {
      // Pre-compute hook operations once to avoid redundant processing
      let hookOperationsCache = [];
      javaHooks.forEach(function(hook) {
        hookOperationsCache.push({
          hook: hook,
          built: buildHookOperations(hook)
        });
      });

      // 1) Emit native summary
      if (nativeHooks.length > 0) {
        let nativeSummary = {
          type: "native-summary",
          hooks: nativeHooksSummary,
          totalHooks: nativeHooksSummary.length,
          errors: nativeErrors,
          totalErrors: nativeErrors.length
        };
        console.log(JSON.stringify(nativeSummary, null, 2));
      }

      // 2) Emit an initial summary of all overloads that will be hooked
      try {
        // Aggregate map nested by class then method
        let aggregate = {};
        let totalHooks = 0;
        let errors = [];
        let totalErrors = 0;
        hookOperationsCache.forEach(function(cached) {
          totalHooks += cached.built.count;
          if (cached.built.errors && cached.built.errors.length) {
            Array.prototype.push.apply(errors, cached.built.errors);
            totalErrors += cached.built.errors.length;
          }
          cached.built.operations.forEach(function(op) {
            if (!aggregate[op.clazz]) {
              aggregate[op.clazz] = {};
            }
            if (!aggregate[op.clazz][op.method]) {
              aggregate[op.clazz][op.method] = [];
            }
            aggregate[op.clazz][op.method].push(op.args);
          });
        });

        let hooks = [];
        for (let clazz in aggregate) {
          if (!aggregate.hasOwnProperty(clazz)) continue;
          let methodsMap = aggregate[clazz];
          for (let methodName in methodsMap) {
            if (!methodsMap.hasOwnProperty(methodName)) continue;
            let entries = methodsMap[methodName].map(function(argsArr) {
              return {args: argsArr};
            });
            hooks.push({class: clazz, method: methodName, overloads: entries});
          }
        }

        let summary = {type: "summary", hooks: hooks, totalHooks: totalHooks, errors: errors, totalErrors: totalErrors};
        console.log(JSON.stringify(summary, null, 2));
      } catch (e) {
        // If summary fails, don't block hooking
        console.error("Summary generation failed, but hooking will continue. Error:", e);
      }

      // 3) Now that both summaries were emitted, attach native hooks
      if (nativeHooks.length > 0) {
        nativeHooks.forEach(function(hook) {
          try {
            registerNativeHook(hook, target.category);
          } catch (e) {
            console.error("Failed to register native hook after summary for symbol '" + hook.symbol + "': " + e);
          }
        });
      }

      // 4) Register Java hooks using cached operations
      hookOperationsCache.forEach(function(cached) {
        registerAllHooks(cached.hook, target.category, cached.built);
      });
    });
})();
