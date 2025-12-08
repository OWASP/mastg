/**
 * Decodes the parameter types of a Java method.
 * @param {string} methodHeader - Java method (e.g., `function setBlockModes([Ljava.lang.String;): android.security.keystore.KeyGenParameterSpec$Builder`)
 * @returns {[string]} The decoded parameter types (e.g., "['[Ljava.lang.String;']")
 */
function parseParameterTypes(methodHeader) {
  var regex = /\((.*?)\)/;
  var parameterString = regex.exec(methodHeader)[1];
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


/**
 * Overloads a method. If the method is called, the parameters and the return value are decoded and together with a stack trace send back to the frida.re client.
 * @param {string} clazz - Java class (e.g., "android.security.keystore.KeyGenParameterSpec$Builder").
 * @param {string} method - Name of the method which should be overloaded (e.g., "setBlockModes").
 * @param {number} overloadIndex - If there are overloaded methods available, this number represents them (e.g., 0 for the first one)
 * @param {string} categoryName - OWASP MAS category for easier identification (e.g., "CRYPTO")
 * @param {function} callback - Callback function. The function takes the information gathered as JSON string.
 * @param {number} maxFrames - Maximum number of stack frames to capture (default is 8,  set to -1 for unlimited frames).
 */
function registerHook(
  clazz,
  method,
  overloadIndex,
  categoryName,
  callback,
  maxFrames = 8
) {

  var Exception = Java.use("java.lang.Exception");
  const System = Java.use('java.lang.System');

  const toHook = Java.use(clazz)[method];

  const methodHeader = toHook.overloads[overloadIndex].toString();

  toHook.overloads[overloadIndex].implementation = function () {

    var st = Exception.$new().getStackTrace();
    var stackTrace = [];
    st.forEach(function (stElement, index) {
      if (maxFrames === -1 || index < maxFrames) {
        var stLine = stElement.toString();
        stackTrace.push(stLine);
      }
    });

    var parameterTypes = parseParameterTypes(methodHeader);
    var returnType = parseReturnValue(methodHeader);

    let instanceId;
    if (this && this.$className && typeof this.$h === 'undefined') {
      instanceId = 'static';
    } else {
      // call Java’s identityHashCode on the real object
      instanceId = System.identityHashCode(this);
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
      var returnValue = this[method].apply(this, arguments);
      event.returnValue = decodeArguments([returnType], [returnValue]);
      callback(event);
      return returnValue;
    } catch (e) {
      event.exception = e.toString();
      callback(event);
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
  for (var i = 0; i < methodHandle.overloads.length; i++) {
    var overload = methodHandle.overloads[i];
    var parameterTypes = parseParameterTypes(overload.toString());
    
    if (parameterTypes.length !== argTypes.length) {
      continue;
    }
    
    var match = true;
    for (var j = 0; j < argTypes.length; j++) {
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
  var operations = [];
  var errors = [];

  try {
    // Invalid configuration: methods + overloads (logged elsewhere)
    if (hook.methods && hook.overloads && hook.overloads.length > 0) {
      var errInvalid = "Invalid hook configuration for " + hook.class + ": 'overloads' is only supported with a singular 'method', not with 'methods'.";
      console.error(errInvalid);
      errors.push(errInvalid);
      return { operations: operations, count: 0, errors: errors, errorCount: errors.length };
    }

    // Explicit overload list for single method
    if (hook.method && hook.overloads && hook.overloads.length > 0) {
      try {
        var handle = Java.use(hook.class)[hook.method];
        for (var o = 0; o < hook.overloads.length; o++) {
          var def = hook.overloads[o];
          var argsExplicit = Array.isArray(def.args) ? def.args : [];
          var idx = findOverloadIndex(handle, argsExplicit);
          if (idx !== -1) {
            var params = parseParameterTypes(handle.overloads[idx].toString());
            operations.push({ clazz: hook.class, method: hook.method, overloadIndex: idx, args: params });
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
        var errMsg = "Failed to process method '" + hook.method + "' in class '" + hook.class + "': " + e;
        console.warn("Warning: " + errMsg);
        errors.push(errMsg);
      }
      return { operations: operations, count: operations.length, errors: errors, errorCount: errors.length };
    }

    // Single method without explicit overloads: all overloads
    if (hook.method && (!hook.overloads || hook.overloads.length === 0)) {
      try {
        var handleAll = Java.use(hook.class)[hook.method];
        for (var i = 0; i < handleAll.overloads.length; i++) {
          var paramsAll = parseParameterTypes(handleAll.overloads[i].toString());
          operations.push({ clazz: hook.class, method: hook.method, overloadIndex: i, args: paramsAll });
        }
      } catch (e) {
        var errMsg2 = "Failed to process method '" + hook.method + "' in class '" + hook.class + "': " + e;
        console.warn("Warning: " + errMsg2);
        errors.push(errMsg2);
      }
      return { operations: operations, count: operations.length, errors: errors, errorCount: errors.length };
    }

    // Multiple methods: all overloads for each
    if (hook.methods) {
      for (var m = 0; m < hook.methods.length; m++) {
        var mName = hook.methods[m];
        try {
          var handleEach = Java.use(hook.class)[mName];
          for (var j = 0; j < handleEach.overloads.length; j++) {
            var paramsEach = parseParameterTypes(handleEach.overloads[j].toString());
            operations.push({ clazz: hook.class, method: mName, overloadIndex: j, args: paramsEach });
          }
        } catch (e) {
          var errMsg3 = "Failed to process method '" + mName + "' in class '" + hook.class + "': " + e;
          console.warn("Warning: " + errMsg3);
          errors.push(errMsg3);
        }
      }
      return { operations: operations, count: operations.length, errors: errors, errorCount: errors.length };
    }
  } catch (e) {
    // Log the error to aid debugging; returning partial results
    var errMsg4 = "Error in buildHookOperations for hook: " + (hook && hook.class ? hook.class : "<unknown>") + ": " + e;
    console.error(errMsg4);
    errors.push(errMsg4);
  }

  return { operations: operations, count: operations.length, errors: errors, errorCount: errors.length };
}

/**
 * Takes an array of objects usually defined in the `hooks.js` file of a DEMO and loads all classes and functions stated in there.
 * @param {[object]} hook - Contains a list of objects which contains all methods which will be overloaded.
 *   Basic format: {class: "android.security.keystore.KeyGenParameterSpec$Builder", methods: ["setBlockModes"]}
 *   With overloads: {class: "android.content.ContentResolver", method: "insert", overloads: [{args: ["android.net.Uri", "android.content.ContentValues"]}]}
 * @param {string} categoryName - OWASP MAS category for easier identification (e.g., "CRYPTO")
 * @param {function} callback - Callback function. The function takes the information gathered as JSON string.
 * @param {{operations: Array<{clazz:string, method:string, overloadIndex:number, args:string[]}>, count:number}} [cachedOperations] - Optional pre-computed hook operations to avoid redundant processing.
 */
function registerAllHooks(hook, categoryName, callback, cachedOperations) {
  if (hook.methods && hook.overloads && hook.overloads.length > 0) {
    console.error(`Invalid hook configuration for ${hook.class}: 'overloads' is only supported with a singular 'method', not with 'methods'.`);
    return;
  }
  var built = cachedOperations || buildHookOperations(hook);
  built.operations.forEach(function (op) {
    try {
      registerHook(op.clazz, op.method, op.overloadIndex, categoryName, callback, hook.maxFrames);
    } catch (err) {
      console.error(err);
      console.error(`Problem when overloading ${op.clazz}:${op.method}#${op.overloadIndex}`);
    }
  });
}

Java.perform(function () {

  function callback(event){
    console.log(JSON.stringify(event, null, 2))
  }

  // Pre-compute hook operations once to avoid redundant processing
  var hookOperationsCache = [];
  target.hooks.forEach(function (hook, _) {
    hookOperationsCache.push({
      hook: hook,
      built: buildHookOperations(hook)
    });
  });

  // Emit an initial summary of all overloads that will be hooked
  try {
    // Aggregate map nested by class then method
    var aggregate = {};
    var total = 0;
    var errors = [];
    var totalErrors = 0;
    hookOperationsCache.forEach(function (cached) {
      total += cached.built.count;
      if (cached.built.errors && cached.built.errors.length) {
        Array.prototype.push.apply(errors, cached.built.errors);
        totalErrors += cached.built.errors.length;
      }
      cached.built.operations.forEach(function (op) {
        if (!aggregate[op.clazz]) {
          aggregate[op.clazz] = {};
        }
        if (!aggregate[op.clazz][op.method]) {
          aggregate[op.clazz][op.method] = [];
        }
        aggregate[op.clazz][op.method].push(op.args);
      });
    });

    var overloadList = [];
    for (var clazz in aggregate) {
      if (!aggregate.hasOwnProperty(clazz)) continue;
      var methodsMap = aggregate[clazz];
      for (var methodName in methodsMap) {
        if (!methodsMap.hasOwnProperty(methodName)) continue;
        var entries = methodsMap[methodName].map(function (argsArr) {
          return { args: argsArr };
        });
        overloadList.push({ class: clazz, method: methodName, overloads: entries });
      }
    }

    var summary = { type: "summary", hooks: overloadList, totalHooks: total, errors: errors, totalErrors: totalErrors };
    console.log(JSON.stringify(summary, null, 2));
  } catch (e) {
    // If summary fails, don't block hooking
    console.error("Summary generation failed, but hooking will continue. Error:", e);
  }

  // Register hooks using cached operations
  hookOperationsCache.forEach(function (cached) {
    registerAllHooks(cached.hook, target.category, callback, cached.built);
  });

});
