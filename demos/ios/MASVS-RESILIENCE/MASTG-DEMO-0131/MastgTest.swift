import Foundation
import Metal
import Darwin

struct MastgTest {
    // SUMMARY: This sample demonstrates iOS virtual device detection by executing virtual device indicator checks and reporting a final verdict in the UI.

    static func mastgTest(completion: @escaping (String) -> Void) {
        completion(VirtualDeviceDetector().run())
    }
}

private enum IndicatorLevel {
    case expected
    case suspicious
    case confirmed
    case inconclusive

    var label: String {
        switch self {
        case .expected:
            return "expected"
        case .suspicious:
            return "suspicious"
        case .confirmed:
            return "confirmed"
        case .inconclusive:
            return "inconclusive"
        }
    }
}

private struct IndicatorResult {
    let order: Int
    let name: String
    let observed: String
    let level: IndicatorLevel
    let reason: String
}

private struct VirtualDeviceDetector {
    func run() -> String {
        let machineIdentifier = currentMachineIdentifier()
        let results = [
            machineIndicator(for: machineIdentifier),
            metalIndicator(),
            corelliumIndicator()
        ]

        return buildReport(from: results)
    }

    private func machineIndicator(for machineIdentifier: String) -> IndicatorResult {
        let level: IndicatorLevel = machineIdentifier == "unknown" ? .inconclusive : .expected
        let reason: String

        if machineIdentifier == "unknown" {
            reason = "The app could not resolve the device model."
        } else {
            reason = "The reported model can be cross-checked against the hardware capabilities observed at runtime."
        }

        // PASS: [MASTG-TEST-0367] The app queries hw.machine through sysctlbyname as a virtual-device indicator.
        return IndicatorResult(
            order: 1,
            name: "hw.machine",
            observed: machineIdentifier,
            level: level,
            reason: reason
        )
    }

    private func metalIndicator() -> IndicatorResult {
        // PASS: [MASTG-TEST-0367] The app checks whether a Metal GPU is available.
        if MTLCreateSystemDefaultDevice() != nil {
            return IndicatorResult(
                order: 2,
                name: "Metal GPU",
                observed: "available",
                level: .expected,
                reason: "A Metal device is available, which is consistent with a physical iOS device."
            )
        }

        return IndicatorResult(
            order: 2,
            name: "Metal GPU",
            observed: "missing",
            level: .suspicious,
            reason: "No Metal device is available, which is unusual for a supported physical iOS device."
        )
    }

    private func corelliumIndicator() -> IndicatorResult {
        // PASS: [MASTG-TEST-0367] The app checks for the Corellium daemon file.
        if corelliumDaemonExists() {
            return IndicatorResult(
                order: 3,
                name: "Corellium daemon",
                observed: "present",
                level: .confirmed,
                reason: "The file /usr/libexec/corelliumd exists, which is a strong Corellium indicator."
            )
        }

        return IndicatorResult(
            order: 3,
            name: "Corellium daemon",
            observed: "not found",
            level: .expected,
            reason: "The file /usr/libexec/corelliumd was not found."
        )
    }

    private func buildReport(from results: [IndicatorResult]) -> String {
        let orderedResults = results.sorted { lhs, rhs in
            lhs.order < rhs.order
        }

        let confirmedResults = orderedResults.filter { $0.level == .confirmed }
        let suspiciousResults = orderedResults.filter { $0.level == .suspicious }

        var lines = [
            "Virtual device detection results:",
            "",
            "Indicators:"
        ]

        for result in orderedResults {
            lines.append("- \(result.name): \(result.observed) [\(result.level.label)] - \(result.reason)")
        }

        lines.append("")
        lines.append("Verdict:")

        if !confirmedResults.isEmpty {
            lines.append("- Likely virtual device.")
            for result in confirmedResults {
                lines.append("  - \(result.name): \(result.reason)")
            }
        } else if !suspiciousResults.isEmpty {
            lines.append("- No strong virtual-device verdict yet. Review the suspicious indicator below.")
            for result in suspiciousResults {
                lines.append("  - \(result.name): \(result.reason)")
            }
        } else {
            lines.append("- Likely physical device. No strong virtual-device indicators were observed.")
        }

        return lines.joined(separator: "\n")
    }

    private func currentMachineIdentifier() -> String {
        var size: size_t = 0

        guard sysctlbyname("hw.machine", nil, &size, nil, 0) == 0, size > 1 else {
            return "unknown"
        }

        var buffer = [CChar](repeating: 0, count: Int(size))
        let result = buffer.withUnsafeMutableBufferPointer { pointer in
            sysctlbyname("hw.machine", pointer.baseAddress, &size, nil, 0)
        }

        guard result == 0 else {
            return "unknown"
        }

        return String(cString: buffer)
    }

    private func corelliumDaemonExists() -> Bool {
        var fileInfo = stat()
        return "/usr/libexec/corelliumd".withCString { path in
            stat(path, &fileInfo) == 0
        }
    }
}
