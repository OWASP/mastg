// SUMMARY: This sample uses App Attest and signs a request payload with an assertion, but takes
// the challenge from a constant embedded in the app instead of a one-time challenge issued by the
// server. The clientDataHash values are therefore the same on every run and on every install, so
// the server has no fresh value of its own to match an attestation or an assertion against.

import Foundation
import CryptoKit
import DeviceCheck

struct MastgTest {
    // The challenge ships inside the app and never changes.
    static let staticChallenge = "mastg-app-attest-challenge"

    // The request the assertion protects. The challenge travels inside the payload, as in
    // Apple's example, so that the signature covers both the request and the challenge.
    struct GameLevelRequest: Codable {
        let action: String
        let levelId: String
        let challenge: String
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        let service = DCAppAttestService.shared

        guard service.isSupported else {
            completion("App Attest is not supported on this device.")
            return
        }

        // FAIL: [MASTG-TEST-0x02] The attestation clientDataHash is a hash over a hardcoded
        // constant rather than over a challenge fetched from the server for this attestation.
        let attestationHash = Data(SHA256.hash(data: Data(staticChallenge.utf8)))

        service.generateKey { keyId, error in
            guard let keyId, error == nil else {
                completion("generateKey failed: \(error?.localizedDescription ?? "unknown")")
                return
            }

            service.attestKey(keyId, clientDataHash: attestationHash) { attestation, error in
                guard let attestation, error == nil else {
                    completion("attestKey failed: \(error?.localizedDescription ?? "unknown")")
                    return
                }

                // FAIL: [MASTG-TEST-0x02] The request payload is bound to the assertion, but the
                // challenge inside it is the same hardcoded constant, so the server cannot tell a
                // new assertion for this request from a recorded one.
                let request = GameLevelRequest(action: "getGameLevel",
                                               levelId: "1234",
                                               challenge: staticChallenge)
                let encoder = JSONEncoder()
                encoder.outputFormatting = .sortedKeys
                guard let clientData = try? encoder.encode(request) else {
                    completion("Could not encode the request payload.")
                    return
                }
                let assertionHash = Data(SHA256.hash(data: clientData))

                service.generateAssertion(keyId, clientDataHash: assertionHash) { assertion, error in
                    guard let assertion, error == nil else {
                        completion("generateAssertion failed: \(error?.localizedDescription ?? "unknown")")
                        return
                    }

                    let value = """
                        Key ID                      : \(keyId)
                        Attestation                 : \(attestation.count) bytes
                        Assertion                   : \(assertion.count) bytes
                        Request payload             : \(String(decoding: clientData, as: UTF8.self))
                        Attestation clientDataHash  : \(hex(attestationHash))
                        Assertion clientDataHash    : \(hex(assertionHash))

                        Both hashes are derived from a challenge compiled into the app, not from
                        one issued by the server, so they are identical on every run.
                        """
                    completion(value)
                }
            }
        }
    }

    private static func hex(_ data: Data) -> String {
        data.map { String(format: "%02x", $0) }.joined()
    }
}
