// SUMMARY: This sample uses App Attest and derives the clientDataHash from a one-time challenge
// fetched from the server for this specific request. Neither the attestation object nor the
// assertion can be replayed, because the server only accepts a nonce it issued and has not yet
// seen used.

import Foundation
import CryptoKit
import DeviceCheck

struct MastgTest {
    // The endpoint that issues a fresh, single-use challenge. No challenge value is compiled
    // into the app, so there is nothing an attacker can extract from the binary and reuse.
    static let challengeEndpoint = URL(string: "https://mastg.example.com/attest/challenge")!

    static func mastgTest(completion: @escaping (String) -> Void) {
        let service = DCAppAttestService.shared

        guard service.isSupported else {
            completion("App Attest is not supported on this device.")
            return
        }

        // PASS: the challenge is requested from the server for this attestation only.
        fetchChallenge { challenge in
            guard let challenge else {
                completion("Could not obtain a challenge from the server.")
                return
            }

            // The clientDataHash is derived from the server-issued nonce, so it differs on
            // every run and binds this attestation to one specific server request.
            let clientDataHash = Data(SHA256.hash(data: Data(challenge.utf8)))

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

                    // PASS: a separate challenge is requested for the assertion, so the
                    // assertion is bound to the request being made rather than to the
                    // earlier attestation.
                    fetchChallenge { assertionChallenge in
                        guard let assertionChallenge else {
                            completion("Could not obtain an assertion challenge from the server.")
                            return
                        }

                        let assertionHash = Data(SHA256.hash(data: Data(assertionChallenge.utf8)))

                        service.generateAssertion(keyId, clientDataHash: assertionHash) { assertion, error in
                            guard let assertion, error == nil else {
                                completion("generateAssertion failed: \(error?.localizedDescription ?? "unknown")")
                                return
                            }

                            let value = """
                                Key ID              : \(keyId)
                                Attestation         : \(attestation.count) bytes
                                Assertion           : \(assertion.count) bytes
                                Attestation nonce   : \(challenge)
                                Assertion nonce     : \(assertionChallenge)

                                Both hashes are derived from single-use nonces issued by the server,
                                so they differ on every run and cannot be replayed.
                                """
                            completion(value)
                        }
                    }
                }
            }
        }
    }

    // Requests a fresh nonce from the backend. The server generates it with a CSPRNG, stores it
    // against the session, and rejects any attestation carrying a nonce it did not issue or has
    // already accepted.
    private static func fetchChallenge(completion: @escaping (String?) -> Void) {
        URLSession.shared.dataTask(with: challengeEndpoint) { data, _, _ in
            guard let data, let challenge = String(data: data, encoding: .utf8) else {
                completion(nil)
                return
            }
            completion(challenge.trimmingCharacters(in: .whitespacesAndNewlines))
        }.resume()
    }
}
