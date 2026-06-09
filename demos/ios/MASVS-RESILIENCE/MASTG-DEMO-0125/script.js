'use strict';

const MAX_LEN = 16384;

function out(kind, msg) {
  if (msg === null || msg === undefined) return;

  msg = String(msg).replace(/\0+$/g, '').trimEnd();
  if (msg.length === 0) return;

  console.log('[' + kind + '] ' + msg);
}

function readUtf8(ptrValue, len) {
  try {
    if (ptrValue.isNull()) return null;

    if (len !== undefined) {
      if (len <= 0 || len > MAX_LEN) return null;
      return ptrValue.readUtf8String(len);
    }

    return ptrValue.readUtf8String();
  } catch (_) {
    return null;
  }
}

function findExport(name) {
  try {
    return Module.findGlobalExportByName(name);
  } catch (_) {
    return null;
  }
}

function hookExport(name, callbacks) {
  const addr = findExport(name);

  if (addr === null) {
    console.log('[missing] ' + name);
    return false;
  }

  Interceptor.attach(addr, callbacks);
  console.log('[hooked] ' + name + ' at ' + addr);
  return true;
}

function isLikelyText(s) {
  if (!s) return false;

  let printable = 0;
  let total = Math.min(s.length, 256);

  for (let i = 0; i < total; i++) {
    const c = s.charCodeAt(i);
    if (c === 9 || c === 10 || c === 13 || c >= 32) printable++;
  }

  return total > 0 && printable / total > 0.85;
}

/*
 * stdout and stderr.
 * Swift print and debugPrint usually reach fwrite or write.
 */
hookExport('write', {
  onEnter(args) {
    const fd = args[0].toInt32();
    if (fd !== 1 && fd !== 2) return;

    const len = args[2].toInt32();
    const data = readUtf8(args[1], len);

    if (isLikelyText(data)) {
      out(fd === 1 ? 'stdout.write' : 'stderr.write', data);
    }
  }
});

hookExport('fwrite', {
  onEnter(args) {
    const size = args[1].toInt32();
    const nmemb = args[2].toInt32();
    const len = size * nmemb;

    const data = readUtf8(args[0], len);

    if (isLikelyText(data)) {
      out('fwrite', data);
    }
  }
});

hookExport('puts', {
  onEnter(args) {
    const data = readUtf8(args[0]);
    if (isLikelyText(data)) out('puts', data);
  }
});

hookExport('printf', {
  onEnter(args) {
    const fmt = readUtf8(args[0]);
    if (isLikelyText(fmt)) out('printf.format', fmt);
  }
});

/*
 * NSLog.
 * This captures the format string. Fully resolving variadic arguments is not generic.
 */
hookExport('NSLog', {
  onEnter(args) {
    try {
      const fmt = new ObjC.Object(args[0]).toString();
      out('NSLog.format', fmt);
    } catch (_) {}
  }
});

/*
 * Objective C NSLogv gives a format object too.
 */
hookExport('NSLogv', {
  onEnter(args) {
    try {
      const fmt = new ObjC.Object(args[0]).toString();
      out('NSLogv.format', fmt);
    } catch (_) {}
  }
});

/*
 * os_log family.
 * The complete rendered message is often not directly available here because
 * Apple logging stores format strings and arguments separately.
 * idevicesyslog is still better for final rendered os.Logger lines.
 */
[
  '_os_log_impl',
  '_os_log_debug_impl',
  '_os_log_error_impl',
  '_os_log_fault_impl'
].forEach(function (name) {
  hookExport(name, {
    onEnter(args) {
      const fmt = readUtf8(args[3]);

      if (!fmt) return;

      /*
       * Drop common UIKit noise.
       */
      if (
        fmt.indexOf('UIEvent') !== -1 ||
        fmt.indexOf('window') !== -1 ||
        fmt.indexOf('gesture') !== -1 ||
        fmt.indexOf('contextId') !== -1
      ) {
        return;
      }

      out(name + '.format', fmt);
    }
  });
});

console.log('[ready] Frida log capture installed');