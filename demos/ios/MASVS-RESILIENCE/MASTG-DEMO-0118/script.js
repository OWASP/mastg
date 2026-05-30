function operationName(operation) {
  if (operation === 0) {
    return "kCCEncrypt";
  }
  if (operation === 1) {
    return "kCCDecrypt";
  }
  return "unknown(" + operation + ")";
}

function algorithmName(algorithm) {
  if (algorithm === 0) {
    return "kCCAlgorithmAES";
  }
  return "unknown(" + algorithm + ")";
}

function isPrintable(text) {
  return /^[\x09\x0a\x0d\x20-\x7e]+$/.test(text);
}

function readUtf8(pointer, length) {
  try {
    const value = pointer.readUtf8String(length);
    if (value !== null && isPrintable(value)) {
      return value;
    }
  } catch (_) {
  }
  return null;
}

function bytesToHex(pointer, length) {
  try {
    const bytes = new Uint8Array(pointer.readByteArray(length));
    return Array.prototype.map.call(bytes, function (byte) {
      return ("0" + byte.toString(16)).slice(-2);
    }).join("");
  } catch (error) {
    return "<unreadable: " + error.message + ">";
  }
}

function formatBuffer(pointer, length) {
  if (pointer.isNull() || length <= 0) {
    return "<empty>";
  }

  const text = readUtf8(pointer, length);
  if (text !== null) {
    return text;
  }

  const hex = bytesToHex(pointer, length);
  if (hex.length > 128) {
    return "0x" + hex.slice(0, 128) + "...";
  }
  return "0x" + hex;
}

function readSizeT(pointer) {
  if (pointer.isNull()) {
    return 0;
  }

  try {
    return pointer.readU64().toNumber();
  } catch (_) {
    return pointer.readU32();
  }
}

const cccrypt = Module.findGlobalExportByName("CCCrypt");

if (cccrypt === null) {
  console.log("[-] CCCrypt export not found.");
} else {
  Interceptor.attach(cccrypt, {
    onEnter(args) {
      this.operation = args[0].toInt32();
      this.algorithm = args[1].toInt32();
      this.dataIn = args[6];
      this.dataInLength = args[7].toInt32();
      this.dataOut = args[8];
      this.dataOutMoved = args[10];

      this.backtrace = Thread.backtrace(this.context, Backtracer.ACCURATE)
        .map(DebugSymbol.fromAddress)
        .slice(0, 8);

      console.log("\n[*] CCCrypt called");
      console.log("    Operation: " + operationName(this.operation));
      console.log("    Algorithm: " + algorithmName(this.algorithm));
      console.log("    Input: " + formatBuffer(this.dataIn, this.dataInLength));
    },

    onLeave(retval) {
      const status = retval.toInt32();
      const outputLength = readSizeT(this.dataOutMoved);

      console.log("    Return status: " + status);
      console.log("    Output: " + formatBuffer(this.dataOut, outputLength));

      console.log("\nBacktrace:");
      for (let i = 0; i < this.backtrace.length; i++) {
        console.log(this.backtrace[i]);
      }
    }
  });

  console.log("[+] CCCrypt hooked: extracting sensitive cryptographic data");
}
