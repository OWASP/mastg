/**
 * Lists the first method matching the given class and method name.
 * @param {string} clazz - Java class name
 * @param {string} method - Java class method name
 */
function enumerateFirstMethod(clazz, method) {
  return Java.enumerateMethods(clazz + '!' + method)[0]
}

/**
 * Decodes the parameter types of a Java method.
 * @param {string} methodHeader - Java method (e.g., `function setBlockModes([Ljava.lang.String;): android.security.keystore.KeyGenParameterSpec$Builder`)
 * @returns {[string]} The decoded parameter types (e.g., "['[Ljava.lang.String;']")
 */
function parseParameterTypes(methodHeader) {
  const regex = /\((.*?)\)/;
  const parameterString = regex.exec(methodHeader)[1];
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
  let d2 = (typeof performance !== "undefined" && performance.now && performance.now() * 1000) || 0;
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
function registerHook(clazz, method, overloadIndex, categoryName, maxFrames = 8) {
  const methodToHook = Java.use(clazz)[method];
  const methodHeader = methodToHook.overloads[overloadIndex].toString();

  methodToHook.overloads[overloadIndex].implementation = function () {

    const stackTrace = [];
    const Exception = Java.use("java.lang.Exception");
    Exception.$new().getStackTrace().forEach((stElement, index) => {
      if (maxFrames === -1 || index < maxFrames) {
        const stLine = stElement.toString();
        stackTrace.push(stLine);
      }
    });

    const parameterTypes = parseParameterTypes(methodHeader);
    const returnType = parseReturnValue(methodHeader);

    let instanceId;
    if (this && this.$className && typeof this.$h === 'undefined') {
      instanceId = 'static';
    } else {
      // call Java’s identityHashCode on the real object
      try {
        const System = Java.use('java.lang.System');
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
      const returnValue = this[method].apply(this, arguments);
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
  methodHandle.overloads.forEach((overload, index) => {
    const parameterTypes = parseParameterTypes(overload.toString());

    if (parameterTypes.length === argTypes.length) {
      argTypes.forEach((argType, j) => {
        if (parameterTypes[j] === argType) {
          return index;
        }
      })
    }

  })
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

  function callPrerequisiteMethod(clazz, method) {
    try {
      Java.use(clazz)[method]();
    } catch (e) {
      console.warn("Warning: " + e)
      errors.push(e)
    }
  }

  function loadPrerequisites(prerequisite) {
    if (prerequisite.methods) {
      prerequisite.methods.forEach(method => {
        callPrerequisiteMethod(prerequisite.class, method)
      })
    }
    if (prerequisite.method) {
      callPrerequisiteMethod(prerequisite.class, prerequisite.method)
    }
  }

  function resolveClass(inputClass, method) {
    if (enumerateFirstMethod(inputClass, method) === undefined) {
      if (hook.prerequisites) {
        hook.prerequisites.forEach(prerequisite => {
          loadPrerequisites(prerequisite)
        })
      }
      if (hook.prerequisite) {
        loadPrerequisites(hook.prerequisite)
      }
    }

    const firstFinding = enumerateFirstMethod(hook.class, method);
    const foundClass = firstFinding.classes[0].name

    if (hook.changeClassLoader) {
      Java.classFactory.loader = firstFinding.loader;
    }
    return foundClass;
  }

  function buildOperationsForMethod(method) {
    try {
      const clazz = resolveClass(hook.class, method);
      Java.use(clazz)[method].overloads.forEach((overload, overloadIndex) => operations.push({
        clazz, method, overloadIndex, args: parseParameterTypes(overload.toString())
      }))
    } catch (e) {
      const errMsg = "Failed to process method '" + method + "' in class '" + hook.class + "': " + e;
      console.warn("Warning: " + errMsg);
      errors.push(errMsg);
    }
  }

  function buildOperationsForMethodWithOverloads(method) {
    try {
      const clazz = resolveClass(hook.class, method);
      const handle = Java.use(clazz)[method];
      hook.overloads.forEach(overload => {
        const argsExplicit = Array.isArray(overload.args) ? overload.args : [];
        const overloadIndex = findOverloadIndex(handle, argsExplicit);
        if (overloadIndex !== -1) {
          const args = parseParameterTypes(handle.overloads[overloadIndex].toString());
          operations.push({clazz, method, overloadIndex, args});
        } else {
          console.warn("[frida-android] Warning: Overload not found for class '" + clazz + "', method '" + method + "', args [" + argsExplicit.join(", ") + "]. This hook will be skipped.");
          errors.push("Overload not found for " + hook.class + ":" + hook.method + " with args [" + argsExplicit.join(", ") + "]");
        }
      })
    } catch (e) {
      const errMsg = "Failed to process method '" + hook.method + "' in class '" + hook.class + "': " + e;
      console.warn("Warning: " + errMsg);
      errors.push(errMsg);
    }
  }

  try {
    if (hook.methods) {
      if (hook.overloads && hook.overloads.length > 0) {
        // Invalid configuration: methods + overloads (logged elsewhere)
        let errInvalid = "Invalid hook configuration for " + hook.class + ": 'overloads' is only supported with a singular 'method', not with 'methods'.";
        console.error(errInvalid);
        errors.push(errInvalid);
        return {operations, count: 0, errors, errorCount: errors.length};
      } else {
        // Multiple methods: all overloads for each
        hook.methods.forEach(method => buildOperationsForMethod(method))
      }
    }
    if (hook.method) {
      const method = hook.method;

      // Explicit overload list for a single method
      if (hook.overloads && hook.overloads.length > 0) {
        buildOperationsForMethodWithOverloads(method);
      }

      // Single method without explicit overloads: all overloads
      if (!hook.overloads || hook.overloads.length === 0) {
        buildOperationsForMethod(method)
      }
    }
  } catch (e) {
    // Log the error to aid debugging; returning partial results
    const errMsg = "Error in buildHookOperations for hook: " + (hook && hook.class ? hook.class : "<unknown>") + ": " + e;
    console.error(errMsg);
    errors.push(errMsg);
  }

  return {operations, count: operations.length, errors, errorCount: errors.length};
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
  const built = cachedOperations || buildHookOperations(hook);
  built.operations.forEach(op => {
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
Java.perform(() => {
  const delay = target.delay ?? 0

  setTimeout(() => {
    // Pre-compute hook operations once to avoid redundant processing
    let hookOperationsCache = [];
    target.hooks.forEach(hook => {
      hookOperationsCache.push({
        hook, built: buildHookOperations(hook)
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

      // 2)Emit an initial summary of all overloads that will be hooked
    try {
      // Aggregate map nested by class then method
      let aggregate = {};
      let totalHooks = 0;
      let errors = [];
      let totalErrors = 0;
      hookOperationsCache.forEach(cached => {
        totalHooks += cached.built.count;
        if (cached.built.errors && cached.built.errors.length) {
          Array.prototype.push.apply(errors, cached.built.errors);
          totalErrors += cached.built.errors.length;
        }
        cached.built.operations.forEach(op => {
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
        const methodsMap = aggregate[clazz];
        for (let method in methodsMap) {
          if (!methodsMap.hasOwnProperty(method)) continue;
          const overloads = methodsMap[method]
              .filter(argsArr => argsArr.length > 0)
              .map(argsArr => ({args: argsArr}));
              hooks.push({class: clazz, method, overloads});
        }
      }

      const summary = {type: "summary", hooks, totalHooks, errors, totalErrors};
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

      // 4) Register Javahooks using cached operations
    hookOperationsCache.forEach(cached => {
      registerAllHooks(cached.hook, target.category, cached.built);
    });
  }, delay);
    });
})();
