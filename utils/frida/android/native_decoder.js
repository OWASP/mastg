/**
 * Convert ArrayBuffer to hex string.
 */
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

/**
 * Decode up to `count` native arguments conservatively.
 * Tries C-string first, then 32-bit int, then 64 bytes as hex.
 */
function decodeNativeArgs(args, count) {
  var out = [];
  var max = typeof count === 'number' ? count : 5;
  for (var i = 0; i < max; i++) {
    var value = null;
    try {
      var p = args[i];
      if (p === undefined) {
        break;
      }
      // Try read as C-string
      try {
        value = p.readCString();
        if (typeof value === 'string') {
          out.push(value);
          continue;
        }
      } catch (e1) {}

      // Try 32-bit integer
      try {
        var i32 = p.toInt32();
        if (typeof i32 === 'number') {
          out.push(i32);
          continue;
        }
      } catch (e2) {}

      // Fallback: first 64 bytes as hex, if readable
      try {
        var buf = Memory.readByteArray(p, 64);
        if (buf) {
          out.push(_arrayBufferToHex(buf));
          continue;
        }
      } catch (e3) {}

      // Last resort: pointer string
      try {
        out.push(p.toString());
      } catch (e4) {
        out.push("<unreadable>");
      }
    } catch (outer) {
      out.push("<error: " + outer + ">");
    }
  }
  return out;
}

/**
 * Decode a single native argument according to a descriptor.
 * Supported types: string, int32, uint32, int64, pointer, bytes(length), bool, double
 * Returns { name, type, value } where value is a JS primitive or string.
 */
function decodeArgByDescriptor(ptr, index, desc) {
  var name = desc && desc.name ? desc.name : ("args[" + index + "]");
  var type = desc && desc.type ? desc.type : "string";
  var value = null;

  try {
    switch (type) {
      case "string":
        value = ptr.readCString();
        break;
      case "int32":
        value = ptr.toInt32();
        break;
      case "uint32":
        value = ptr.toUInt32();
        break;
      case "int64":
        // emit as string to avoid precision loss
        try { value = ptr.toInt64().toString(); } catch (e64) { value = ptr.toInt32(); }
        break;
      case "pointer":
        value = ptr.toString();
        break;
      case "bytes":
        var len = (desc && typeof desc.length === 'number') ? desc.length : 64;
        var buf = Memory.readByteArray(ptr, len);
        value = buf ? _arrayBufferToHex(buf) : "";
        break;
      case "bool":
        value = !!ptr.toInt32();
        break;
      case "double":
        try { value = ptr.readDouble(); } catch (ed) { value = Number(ptr.toInt32()); }
        break;
      default:
        // fallback like conservative decoder
        try { value = ptr.readCString(); } catch(e1) {
          try { value = ptr.toInt32(); } catch(e2) {
            try { var buf2 = Memory.readByteArray(ptr, 64); value = buf2 ? _arrayBufferToHex(buf2) : ptr.toString(); } catch(e3) { value = ptr.toString(); }
          }
        }
        break;
    }
  } catch (outer) {
    value = "<error: " + outer + ">";
  }

  return { name: name, type: type, value: value };
}

/**
 * Apply per-argument filters.
 * If any descriptor includes a filter array, require that decoded value matches at least one entry.
 * For multiple descriptors with filters, all must match.
 */
function filtersPass(decodedList, descriptors) {
  if (!descriptors || !descriptors.length) return true;
  var anyFilters = false;
  for (var i = 0; i < descriptors.length; i++) {
    var d = descriptors[i];
    if (d && Array.isArray(d.filter) && d.filter.length) {
      anyFilters = true;
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
          // pointer/bytes (string), other types - stringify
          if (String(val).indexOf(String(term)) !== -1) { matched = true; break; }
        }
      }
      if (!matched) return false; // one filtered arg failed
    }
  }
  return anyFilters ? true : true; // if no filters present, pass
}
