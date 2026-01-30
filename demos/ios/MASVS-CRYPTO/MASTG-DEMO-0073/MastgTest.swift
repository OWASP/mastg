import Foundation
import Security
import Darwin
import CommonCrypto

// Unsafe bindings to libc srand and rand
@_silgen_name("srand")
func c_srand(_ seed: UInt32)

@_silgen_name("rand")
func c_rand() -> Int32

struct MastgTest {

    // Insecure: libc rand seeded with time, predictable and not suitable for cryptography
    static func generateRandomTokenRand() -> String {
        var token = ""

        for _ in 0..<16 {
            let value = c_rand() % 256
            token += String(format: "%02x", value)
        }
        return token
    }

    // Cryptographically secure on Apple platforms
    // Swift random APIs use SystemRandomNumberGenerator backed by the system CSPRNG via arc4random_buf
    // Shown here as a secure source that is not a dedicated crypto token API
    static func generateRandomTokenSwiftRandom() -> String {
        var token = ""
        for _ in 0..<16 {
            let b = UInt8.random(in: 0...255)
            token += String(format: "%02x", b)
        }
        return token
    }
    
    // Cryptographically secure: direct read from /dev/random on Apple platforms
    // However, this is a low level interface and is discouraged in favor of SecRandomCopyBytes
    static func generateRandomTokenDevRandom() -> String {
        let count = 16

        let fd = open("/dev/random", O_RDONLY)
        if fd < 0 {
            return "Error opening /dev/random"
        }

        var buffer = [UInt8](repeating: 0, count: count)
        let readCount = read(fd, &buffer, count)
        close(fd)

        if readCount != count {
            return "Error reading /dev/random"
        }

        return buffer.map { String(format: "%02x", $0) }.joined()
    }
    
    // Cryptographically secure but discouraged as a direct token API in Swift code 
    // because uses legacy C style interfaces that are easier to misuse
    // On Apple platforms arc4random_uniform is strong, but SecRandomCopyBytes or CryptoKit are preferred
    static func generateRandomTokenArc4RandomUniform() -> String {
        var token = ""
        for _ in 0..<16 {
            let value = arc4random_uniform(256)
            token += String(format: "%02x", value)
        }
        return token
    }

    // Cryptographically secure but discouraged as a direct token API
    // On Apple platforms arc4random is strong, but it is not the recommended crypto API
    static func generateRandomTokenArc4Random() -> String {
        var token = ""
        for _ in 0..<16 {
            let value = arc4random() % 256
            token += String(format: "%02x", value)
        }
        return token
    }
    
    // Cryptographically secure: SystemRandomNumberGenerator uses the system CSPRNG
    // It is suitable for cryptographic use, and CryptoKit builds on it
    // Included here to contrast secure generators with insecure ones
    static func generateRandomTokenSystemRNG() -> String {
        var token = ""
        var rng = SystemRandomNumberGenerator()

        for _ in 0..<16 {
            let b = UInt8.random(in: 0...255, using: &rng)
            token += String(format: "%02x", b)
        }
        return token
    }
    
    // Insecure: drand48 uses a 48 bit linear congruential generator
    // Not thread safe and not suitable for cryptographic purposes
    static func generateRandomTokenDrand48() -> String {
        var token = ""
        for _ in 0..<16 {
            let value = Int(drand48() * 256.0) % 256
            token += String(format: "%02x", value)
        }
        return token
    }
    
    // Cryptographically secure: CCRandomGenerateBytes uses the system CSPRNG
    // Secure, but a lower level API that is generally discouraged in favor of SecRandomCopyBytes
    static func generateRandomTokenCC() -> String {
        var buffer = [UInt8](repeating: 0, count: 16)
        let status = CCRandomGenerateBytes(&buffer, buffer.count)

        if status != kCCSuccess {
            return "Error generating random bytes with CCRandomGenerateBytes"
        }

        return buffer.map { String(format: "%02x", $0) }.joined()
    }

    // Recommended: SecRandomCopyBytes is the high level, Apple recommended API for secure random bytes
    static func generateRandomTokenSecRandom() -> String {
        var randomBytes = [UInt8](repeating: 0, count: 16)
        let status = SecRandomCopyBytes(kSecRandomDefault, randomBytes.count, &randomBytes)

        guard status == errSecSuccess else {
            return "Error generating secure random bytes"
        }

        return randomBytes.map { String(format: "%02x", $0) }.joined()
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        // Seed libc rand with current time
        let now = UInt32(time(nil))
        c_srand(now)
        
        // Example of seeding drand48 with time, which also makes it predictable if the seed is known
        // srand48(time(nil))

        let value = """
        Using libc rand seeded with time
        Token: \(generateRandomTokenRand())

        Using Swift random API backed by SystemRandomNumberGenerator
        Token: \(generateRandomTokenSwiftRandom())
        
        Using /dev/random low level interface
        Token: \(generateRandomTokenDevRandom())

        Using arc4random_uniform as a direct token source
        Token: \(generateRandomTokenArc4RandomUniform())
        
        Using arc4random as a direct token source
        Token: \(generateRandomTokenArc4Random())

        Using SystemRandomNumberGenerator directly
        Token: \(generateRandomTokenSystemRNG())
        
        Using drand48 linear congruential generator
        Token: \(generateRandomTokenDrand48())

        Using CCRandomGenerateBytes lower level API
        Token: \(generateRandomTokenCC())

        Using SecRandomCopyBytes
        Token: \(generateRandomTokenSecRandom())
        """

        completion(value)
    }
}
