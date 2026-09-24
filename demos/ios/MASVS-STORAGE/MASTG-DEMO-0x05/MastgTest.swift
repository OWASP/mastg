// SUMMARY: This sample embeds third-party credentials as Swift string literals. String literals
// end up in the __TEXT.__cstring section of the compiled binary, so anyone who obtains the IPA
// can recover them without running the app, without a jailbreak, and without any tooling beyond
// a strings dump.

import Foundation

struct MastgTest {
    // INSECURE: third-party API key embedded in the app binary.
    static let mapsAPIKey = "AIzaSyDFakeMastgDemoKeyNotARealKey12345"

    // INSECURE: cloud provider credential embedded in the app binary.
    static let awsAccessKeyId = "AKIAIOSFODNN7EXAMPLE"

    // INSECURE: secret assigned to a credential-named constant.
    static let clientSecret = "s3cr3t-not-a-real-value-9f2b"

    static let endpoint = "https://api.example.com/v1/report"

    static func mastgTest(completion: @escaping (String) -> Void) {
        let value = """
            Endpoint          : \(endpoint)
            Maps API key      : \(mapsAPIKey)
            AWS access key id : \(awsAccessKeyId)
            Client secret     : \(clientSecret)

            All three credentials above are compiled into the app binary as string literals.
            """
        completion(value)
    }
}
