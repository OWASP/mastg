// SUMMARY: This sample uses App Attest and signs a request payload with an assertion. Every
// clientDataHash is derived from a one-time challenge fetched from the server for that specific
// attestation or request, so the server can match each one against a value it issued and has not
// yet seen used.

import Foundation
import CryptoKit
import DeviceCheck

struct MastgTest {
    // The endpoint that issues a new, single-use challenge. No challenge value is compiled into
    // the app.
    static let challengeEndpoint = URL(string: "https://mastg.example.com/attest/challenge")!

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

        // PASS: [MASTG-TEST-0x02] The challenge is requested from the server for this
        // attestation only.
        fetchChallenge { challenge in
            guard let challenge else {
                completion("Could not obtain a challenge from the server.")
                return
            }

            let attestationHash = Data(SHA256.hash(data: Data(challenge.utf8)))

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

                    // PASS: [MASTG-TEST-0x02] A separate challenge is requested for the assertion
                    // and placed inside the request payload, so the signature covers the request
                    // and a value the server issued for it.
                    fetchChallenge { assertionChallenge in
                        guard let assertionChallenge else {
                            completion("Could not obtain an assertion challenge from the server.")
                            return
                        }

                        let request = GameLevelRequest(action: "getGameLevel",
                                                       levelId: "1234",
                                                       challenge: assertionChallenge)
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
                                Key ID                  : \(keyId)
                                Attestation             : \(attestation.count) bytes
                                Assertion               : \(assertion.count) bytes
                                Request payload         : \(String(decoding: clientData, as: UTF8.self))
                                Attestation challenge   : \(challenge)
                                Assertion challenge     : \(assertionChallenge)

                                Both hashes are derived from single-use challenges issued by the
                                server, so they differ on every run.
                                """
                            completion(value)
                        }
                    }
                }
            }
        }
    }

    // Requests a new challenge from the backend. The server generates it with a CSPRNG, stores it
    // against the session, and rejects any attestation or assertion carrying a challenge it did
    // not issue or has already accepted.
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
