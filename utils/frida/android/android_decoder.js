/**
 * Makes a hex dump of a byte array. The dump is limited by the length parameter.
 * @param {Uint8Array} bytes - Byte array to be decoded to hexadecimal.
 * @param {number} length - Number of bytes which will be decoded.
 * @returns {string} The hexadecimal decoded bytes (e.g., "0x22aa3482ef...")
 */
function byteArrayHexDump(bytes, length) {
  var appendix = "...";
  if (bytes.length < length) {
    length = bytes.length;
    appendix = "";
  }

  var hexString = "0x";
  for (var i = 0; i < length; i++) {
    hexString =
      hexString + ("0" + (bytes[i] & 0xff).toString(16)).slice(-2);
  }

  return hexString + appendix;
}

/**
 * Converts a byte value to its uri-encoded representation
 * @param {number} byte - The byte value to encode (0-255)
 * @returns {string} The uri-encoded string (e.g., "%20", "%0A")
 */
function getUriCode(byte) {
  const text = byte.toString(16);
  if (byte < 16) {
    return "%0" + text;
  }
  return "%" + text;
}

/**
 * Tries to decode a byte array to either a string or a hex dump depending on the content of the array.
 * @param {Uint8Array} bytes - Byte array to be decoded to hexadecimal.
 * @param {number} length - Number of bytes which will be decoded.
 * @returns {string} The decoded bytes (e.g., "This is some decoded string." or "0x22aa3482ef...")
 */
function byteToString(bytes, length) {
  if (bytes.length < length) {
    length = bytes.length;
  }

  try {
    // try to decode strings
    var result = "";
    for (var i = 0; i < length; ++i) {
      result += getUriCode(bytes[i]);
    }
    return decodeURIComponent(result).replace(/\0.*$/g, "");
  } catch (e) {
    // make a hex dump in case, the byte array contains raw binary data
    return byteArrayHexDump(bytes, length);
  }
}

/**
 * Generates a simple hash from a string.
 * @param {string} str - String to hash.
 * @returns {number} Hash value as a 32-bit integer.
 */
function simpleHash(str) {
  let h = 0;
  for (let i = 0; i < str.length; i++) {
    h = (h << 5) - h + str.charCodeAt(i);
    h = h | 0;
  }
  return h;
}

/**
 * Decodes an RSA key (public or private) and extracts key parameters.
 * @param {Object} value - Reference to the Java key object.
 * @returns {Object} Object containing key parameters (modulusHex, modulusBitLength, publicExponentDec, privateExponentDec, keyHash).
 */
function decodeRSAKey(value) {
  var out = {};

  try {
    // Load RSA interfaces
    var RSAKey = Java.use('java.security.interfaces.RSAKey');
    var RSAPub = Java.use('java.security.interfaces.RSAPublicKey');
    var RSAPriv = Java.use('java.security.interfaces.RSAPrivateKey');
    var RSAPrivateCrt = null;
    try {
      RSAPrivateCrt = Java.use('java.security.interfaces.RSAPrivateCrtKey');
    } catch (_) {
      RSAPrivateCrt = null;
    }

    // Any RSA key, public or private, for modulus
    try {
      var anyRsa = Java.cast(value, RSAKey);
      var modBI = anyRsa.getModulus();
      out.modulusHex = modBI.toString(16);
      out.modulusBitLength = modBI.bitLength();
    } catch (_) {
      // not an RSAKey or keystore backend hides it, ignore
    }

    // Public key exponent
    try {
      var vpub = Java.cast(value, RSAPub);
      var expBI = vpub.getPublicExponent();
      if (expBI) {
        out.publicExponentDec = expBI.toString(10);
      }
    } catch (_) {
      // not an RSAPublicKey
    }

    // Private key exponents, may be unavailable for keystore backed keys
    if (RSAPrivateCrt !== null) {
      try {
        var vprivCrt = Java.cast(value, RSAPrivateCrt);
        var dBI = vprivCrt.getPrivateExponent();
        var eBI = vprivCrt.getPublicExponent();
        if (dBI) {
          out.privateExponentDec = dBI.toString(10);
        }
        if (eBI) {
          out.publicExponentDec = eBI.toString(10);
        }
      } catch (_) {
        // not an RSAPrivateCrtKey
      }
    } else {
      try {
        var vpriv = Java.cast(value, RSAPriv);
        var dBI2 = vpriv.getPrivateExponent();
        if (dBI2) {
          out.privateExponentDec = dBI2.toString(10);
        }
      } catch (_) {
        // not an RSAPrivateKey
      }
    }
  } catch (_) {
    // key interface logic failed, out remains minimal
  }

  if (out.modulusHex != null) {
    out.keyHash = simpleHash(out.modulusHex);
  }

  return out;
}

/**
 * Decodes a Java object according to its type.
 * @param {string} type - Java type of the value (e.g., "java.util.Set", "java.lang.String" or "int")
 * @param {Object} value - Reference to the object.
 * @returns {string} The type-appropriate decoded string (e.g., "[1,50,21]", "Hello World" or "-12")
 */
function decodeValue(type, value) {
  var readableValue = "";

  try {
    if (value == null) {
      readableValue = "void";
    } else {
      switch (type) {
        case "java.util.Set":
          readableValue = value.toArray().toString();
          break;

        case "java.util.Map":
          var entrySet = value.entrySet();
          readableValue = entrySet.toArray().toString();
          break;

        case "[B":
          // for performance reasons only decode the first 256 bytes of the full byte array
          readableValue = byteToString(value, 256);
          break;

        case "[C":
          readableValue = "";
          for (var i in value) {
            readableValue = readableValue + value[i];
          }
          break;

        case "java.io.File":
          readableValue = value.getAbsolutePath();
          break;

        case "java.util.Date":
          var DateFormat = Java.use('java.text.DateFormat');
          var formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM.value, DateFormat.SHORT.value);
          readableValue = formatter.format(value);
          break;

        case "androidx.sqlite.db.SupportSQLiteQuery":
          readableValue = value.getSql();
          break;

        case "android.content.ClipData$Item":
          readableValue = value.getText().toString();
          break;

        case "androidx.datastore.preferences.core.Preferences$Key":
        case "java.lang.Object":
        case "android.net.Uri":
        case "java.lang.CharSequence":
          readableValue = value.toString();
          break;

        /*
        1. No `RSAKey.class.isAssignableFrom` or `Java.isInstanceOf` for the RSA interfaces, instead each RSA interface is tried with `Java.cast` inside a `try` block. If the object does not implement that interface, the cast throws and is ignored. If it does, the cast succeeds and you can call the RSA methods.
        2. Modulus is obtained through `RSAKey.getModulus()`, which should cover both `OpenSSLRSAPublicKey` and `AndroidKeyStoreRSAPrivateKey` as long as the backend exposes the modulus.
        3. Exponents come from `RSAPublicKey.getPublicExponent()` and `RSAPrivateKey` or `RSAPrivateCrtKey` for the private exponent, but note that keystore backed private keys may refuse to expose the private exponent, so missing private exponent is expected in that case, while the public key parameters should still be visible.
        */

        case "java.security.PrivateKey":
        case "java.security.PublicKey":
        case "java.security.Key":
          try {
            readableValue = decodeRSAKey(value);
          } catch (e) {
            readableValue = value;
          }
          break;

        case "[Ljava.lang.Object;":
          var out = "";
          for (var i in value) {
            out = out + value[i] + ", ";
          }
          readableValue = out;
          break;

        case "java.util.Enumeration":
          var elements = [];
          while (value.hasMoreElements()) {
            elements.push(value.nextElement().toString());
          }
          readableValue = JSON.stringify(elements);
          break;

        case "android.database.Cursor":
          readableValue = decodeCursor(value);
          break;

        default:
          readableValue = value;
          break;
      }
    }
  } catch (e) {
    console.error("Value decoding exception: " + e);
    readableValue = value;
  }

  return readableValue;
}

/**
 * Decodes a `android.database.Cursor` object.
 * @param {object} value - Reference to the object.
 * @returns {string} The decoded rows and columns.
 */
function decodeCursor(value){
  var out = "";
  var cursor = value;
  var originalCursorPosition = cursor.getPosition();

  // rows
  for (var i = 0; i < cursor.getColumnCount(); i++) {
    var columnName = cursor.getColumnName(i);
    out = out + columnName + " | ";
  }

  out = out + "\n----------------------\n";

  // columns
  if (cursor.moveToFirst()) {
    do {
      for (var i = 0; i < cursor.getColumnCount(); i++) {
        try {
          var columnValue = cursor.getString(i);
          out = out + columnValue + " | ";
        } catch (e) {
          out = out + " | ";
        }
      }
      out = out + "\n";
    } while (cursor.moveToNext());

    cursor.moveToPosition(originalCursorPosition);
  }
  return out;
}

/**
 * Decodes Java values according to their types.
 * @param {[string]} types - Java types of the value (e.g., ["java.util.Set", "java.lang.String", "int"])
 * @param {[string]]} value - Reference to the objects.
 * @returns {[string]} The type-appropriate decoded strings (e.g., ["java.util.Set":"[1,50,21]", "java.lang.String":"Hello World", "int":"-12"])
 */

// Module-level cached references for performance
var _toStringMethod = null;
var _toStringMethodInitialized = false;
var _SystemCls = null;
var _SystemClsInitialized = false;

function getToStringMethod() {
  if (!_toStringMethodInitialized) {
    try {
      var ObjCls = Java.use('java.lang.Object');
      _toStringMethod = ObjCls.class.getDeclaredMethod('toString', []);
      _toStringMethod.setAccessible(true);
    } catch (_) {
      _toStringMethod = null;
    }
    _toStringMethodInitialized = true;
  }
  return _toStringMethod;
}

function getSystemCls() {
  if (!_SystemClsInitialized) {
    try {
      _SystemCls = Java.use('java.lang.System');
    } catch (_) {
      _SystemCls = null;
    }
    _SystemClsInitialized = true;
  }
  return _SystemCls;
}

function decodeArguments(types, args) {
  var parameters = [];
  var toStringMethod = getToStringMethod();
  var SystemCls = getSystemCls();

  for (var i in types) {
    var declaredType = types[i];
    var argVal = args[i];
    var entry = { declaredType: declaredType, value: decodeValue(declaredType, argVal) };

    // Attach runtime info if this is a Java object
    if (argVal && typeof argVal === 'object') {
      var runtimeType = null;
      try { runtimeType = argVal.$className || (argVal.getClass ? argVal.getClass().getName() : null); } catch (_) {}
      if (runtimeType) {
        entry.runtimeType = runtimeType;
        if (SystemCls) {
          try {
            entry.instanceId = '' + SystemCls.identityHashCode(argVal);
          } catch (_) {}
        }
        // Robust toString retrieval: prefer reflected method, fallback to direct call
        try {
          if (toStringMethod) {
            entry.instanceToString = '' + toStringMethod.invoke(argVal, []);
          } else {
            entry.instanceToString = '' + argVal.toString();
          }
        } catch (e1) {
          try { entry.instanceToString = '' + argVal.toString(); } catch (e2) { entry.instanceToString = '<toString-unavailable>'; }
        }
      }
    }

    parameters.push(entry);
  }
  return parameters;
}

