// SUMMARY: This sample uses App Attest, but derives the clientDataHash from a constant embedded
// in the app instead of a one-time challenge issued by the server. Both the attestation object
// and every assertion are therefore replayable: an attacker who captures one can present it
// again later, because nothing binds it to a specific server request.

import Foundation
import CryptoKit
import DeviceCheck

struct MastgTest {
    // The value the clientDataHash is built from. It ships inside the app and never changes,
    // so every attestation and assertion produced by every install is identical.
    static let staticChallenge = "mastg-app-attest-challenge"

    static func mastgTest(completion: @escaping (String) -> Void) {
        let service = DCAppAttestService.shared

        guard service.isSupported else {
            completion("App Attest is not supported on this device.")
            return
        }

        // FAIL: the clientDataHash is a hash over a hardcoded constant rather than over a
        // nonce fetched from the server for this specific attestation request.
        let clientDataHash = Data(SHA256.hash(data: Data(staticChallenge.utf8)))

        service.generateKey { keyId, error in
            guard let keyId, error == nil else {
                completion("generateKey failed: \(error?.localizedDescription ?? "unknown")")
                return
            }

            service.attestKey(keyId, clientDataHash: clientDataHash) { attestation, error in
                guard let attestation, error == nil else {
                    completion("attestKey failed: \(error?.localizedDescription ?? "unknown")")
                    return
                }

                // FAIL: the assertion reuses the same constant-derived hash, so it is not bound
                // to the request being made and can be replayed against any later request.
                service.generateAssertion(keyId, clientDataHash: clientDataHash) { assertion, error in
                    guard let assertion, error == nil else {
                        completion("generateAssertion failed: \(error?.localizedDescription ?? "unknown")")
                        return
                    }

                    let value = """
                        Key ID          : \(keyId)
                        Attestation     : \(attestation.count) bytes
                        Assertion       : \(assertion.count) bytes
                        clientDataHash  : \(clientDataHash.map { String(format: "%02x", $0) }.joined())

                        The clientDataHash above is derived from a constant compiled into the app,
                        not from a server-issued challenge, so it is identical on every run.
                        """
                    completion(value)
                }
            }
        }
    }
}
