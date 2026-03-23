import Foundation
import UIKit
import os.log

// SUMMARY: This sample demonstrates verbose error logging and debugging messages that expose implementation details.

class MastgTest {
    
    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0318] Verbose logging exposes internal API endpoint and request details
        NSLog("[DEBUG] Attempting to connect to API endpoint: https://internal-api.example.com/v2/auth/login")
        
        let result = performLogin(username: "testuser", password: "testpass")
        completion(result)
    }
    
    static func performLogin(username: String, password: String) -> String {
        // FAIL: [MASTG-TEST-0318] Debug print exposes function execution flow and internal state
        print("[DEBUG] performLogin() called with username: \(username)")
        
        // Simulate network request
        let success = validateCredentials(username: username, password: password)
        
        if success {
            // FAIL: [MASTG-TEST-0318] Verbose success message exposes implementation details
            debugPrint("✅ [DEBUG] Authentication successful - Session token generated: \(generateMockToken())")
            debugPrint("[DEBUG] User profile loaded from cache, bypassing network call")
            return "Login successful"
        } else {
            // FAIL: [MASTG-TEST-0318] Detailed error logging exposes error handling logic
            NSLog("[ERROR] Authentication failed - Invalid credentials provided")
            NSLog("[DEBUG] Fallback to offline mode initiated")
            NSLog("[DEBUG] Error code: AUTH_001, Module: AuthenticationService.validateCredentials()")
            return "Login failed"
        }
    }
    
    static func validateCredentials(username: String, password: String) -> Bool {
        // FAIL: [MASTG-TEST-0318] os_log with .debug level exposes validation logic
        if #available(iOS 14.0, *) {
            let logger = Logger(subsystem: "com.example.mastg", category: "Authentication")
            logger.debug("Validating credentials against local database")
            logger.debug("Checking password hash: SHA256 algorithm")
        }
        
        // Simulate validation
        return username.count > 0 && password.count > 0
    }
    
    static func generateMockToken() -> String {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
    }
    
    static func performNetworkRequest() {
        // FAIL: [MASTG-TEST-0318] Verbose logging exposes network configuration
        print("[DEBUG] Network request configuration:")
        print("[DEBUG] - Timeout: 30s")
        print("[DEBUG] - Retry count: 3")
        print("[DEBUG] - SSL pinning: disabled")
        print("[DEBUG] - Certificate validation: relaxed for staging environment")
    }
    
    static func handleError(_ error: Error) {
        // FAIL: [MASTG-TEST-0318] Dumping error object exposes internal error structure
        dump(error)
        
        // FAIL: [MASTG-TEST-0318] Verbose error logging with stack trace information
        NSLog("[ERROR] Exception occurred in module: NetworkManager")
        NSLog("[ERROR] Stack trace: \(Thread.callStackSymbols)")
    }
    
    // PASS: [MASTG-TEST-0318] Properly guarded debug logging (would not appear in release builds if DEBUG flag is set)
    static func properlyGuardedLogging() {
        #if DEBUG
        print("[DEBUG] This message only appears in debug builds")
        NSLog("[DEBUG] Debug configuration active")
        #endif
    }
}
