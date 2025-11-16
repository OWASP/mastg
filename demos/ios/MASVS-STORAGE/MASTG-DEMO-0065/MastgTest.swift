import SwiftUI
import os
import Darwin

struct MastgTest {
  private static let logger = Logger(
    subsystem: Bundle.main.bundleIdentifier ?? "org.owasp.mas.MastgTest",
    category: "MastgTest"
  )

  static func mastgTest(completion: @escaping (String) -> Void) {
    let token = "TOKEN=123"

    print("print: Leaking \(token) from print")
    debugPrint("debugPrint: Leaking \(token) from debugPrint")
    dump(token, name: "dump: Leaking token from dump")
    NSLog("NSLog: Leaking \(token) from NSLog")
    os_log("os_log: Leaking %{public}@ from os_log",
           log: .default, type: .info, token)
    
    logger.debug("logger.debug: Leaking \(token)")
    logger.debug("logger.debug (privacy: .public): Leaking \(token, privacy: .public)")
    logger.debug("logger.debug (privacy: .private): Leaking \(token, privacy: .private)")
    logger.debug("logger.debug (privacy: .sensitive): Leaking \(token, privacy: .sensitive)")
    
    
    logger.info("logger.info: Leaking \(token)")
    logger.info("logger.info (privacy: .public): Leaking \(token, privacy: .public)")
    logger.info("logger.info (privacy: .private): Leaking \(token, privacy: .private)")
    logger.info("logger.info (privacy: .sensitive): Leaking \(token, privacy: .sensitive)")
    
    logger.warning("logger.warning: Leaking \(token)")
    logger.warning("logger.warning (privacy: .public): Leaking \(token, privacy: .public)")
    logger.warning("logger.warning (privacy: .private): Leaking \(token, privacy: .private)")
    logger.warning("logger.warning (privacy: .sensitive): Leaking \(token, privacy: .sensitive)")
    
    logger.error("logger.error: Leaking \(token)")
    logger.error("logger.error (privacy: .public): Leaking \(token, privacy: .public)")
    logger.error("logger.error (privacy: .private): Leaking \(token, privacy: .private)")
    logger.error("logger.error (privacy: .sensitive): Leaking \(token, privacy: .sensitive)")
    
    logger.fault("logger.fault: Leaking \(token)")
    logger.fault("logger.fault (privacy: .public): Leaking \(token, privacy: .public)")
    logger.fault("logger.fault (privacy: .private): Leaking \(token, privacy: .private)")
    logger.fault("logger.fault (privacy: .sensitive): Leaking \(token, privacy: .sensitive)")
    

    fputs("fputs: Leaking \(token) from fputs\n", stderr)
    _ = swiftVfprintf(
      stream: stderr,
      format: "vfprintf: Leaking %s from vfprintf\n",
      token
    )
    _ = swiftVdprintf(
      filedes: fileno(stderr),
      format: "vdprintf: Leaking %s from vdprintf\n",
      token
    )
    swiftVsyslog(
      priority: LOG_USER | LOG_INFO,
      format: "vsyslog: Leaking %s from vsyslog\n",
      token
    )

    // To enable ASL logging, add <asl.h> to your bridging header
    // and uncomment the following:
    /*
    let ASL_LEVEL_NOTICE: Int32 = 5
    swiftVaslLog(
      client: nil,
      msg: nil,
      level: ASL_LEVEL_NOTICE,
      format: "asl_vlog: Leaking %s from asl_vlog\n",
      token
    )
    */

    if let data = "FileHandle.standardError: Leaking \(token) from FileHandle.standardError\n"
                  .data(using: .utf8) {
      FileHandle.standardError.write(data)
    }

    completion("Successfully logged a token: \(token)")
  }

  @discardableResult
  static func swiftVfprintf(
    stream: UnsafeMutablePointer<FILE>!,
    format: String,
    _ args: CVarArg...
  ) -> Int32 {
    return withVaList(args) { vfprintf(stream, format, $0) }
  }

  @discardableResult
  static func swiftVdprintf(
    filedes: Int32,
    format: String,
    _ args: CVarArg...
  ) -> Int32 {
    return withVaList(args) { vdprintf(filedes, format, $0) }
  }

  static func swiftVsyslog(
    priority: Int32,
    format: String,
    _ args: CVarArg...
  ) {
    _ = withVaList(args) { vsyslog(priority, format, $0) }
  }

  // ASL logging wrapper (requires <asl.h> in bridging header)
  /*
  static func swiftVaslLog(
    client: aslclient?,
    msg: aslmsg?,
    level: Int32,
    format: String,
    _ args: CVarArg...
  ) {
    _ = withVaList(args) { asl_vlog(client, msg, level, format, $0) }
  }
  */
}
