import Foundation
import Darwin
import MachO

class MastgTest {
    
    static func mastgTest() -> String {
        print("[MASTG-TEST-0091] Testing Frida Detection")
        
        var detections: [String] = []
        
        // Method 1: Check for Frida libraries in memory
        if isFridaLibraryLoaded() {
            detections.append("Frida library detected in memory")
        }
        
        // Method 2: Check for Frida's default port
        if isFridaPortOpen() {
            detections.append("Frida server port (27042) is open")
        }
        
        // Method 3: Check for suspicious thread count
        if hasSuspiciousThreadCount() {
            detections.append("Suspicious thread count detected")
        }
        
        // Method 4: Check for Frida-related named pipes/sockets
        if hasFridaArtifacts() {
            detections.append("Frida artifacts detected")
        }
        
        if detections.isEmpty {
            let result = "✅ No Frida detected - App is running normally"
            print(result)
            return result
        } else {
            let result = "🚨 SECURITY ALERT!\n\nFrida Detection Results:\n" +
                        detections.enumerated().map { "\($0.offset + 1). \($0.element)" }.joined(separator: "\n")
            print(result)
            return result
        }
    }
    
    // MARK: - Detection Method 1: Check for Frida Libraries
    
    private static func isFridaLibraryLoaded() -> Bool {
        let fridaStrings = [
            "frida",
            "gadget",
            "frida-agent",
            "libfrida"
        ]
        
        // Get the number of loaded images (dylibs)
        let imageCount = _dyld_image_count()
        
        for i in 0..<imageCount {
            // Get the name of each loaded image
            if let imageName = _dyld_get_image_name(i) {
                let name = String(cString: imageName).lowercased()
                
                for fridaString in fridaStrings {
                    if name.contains(fridaString) {
                        print("⚠️ Frida library found: \(name)")
                        return true
                    }
                }
            }
        }
        
        return false
    }
    
    // MARK: - Detection Method 2: Check Frida Port
    
    private static func isFridaPortOpen() -> Bool {
        let fridaPorts: [UInt16] = [27042, 27043] // Default and alternate Frida ports
        
        for port in fridaPorts {
            if isPortOpen(port: port) {
                print("⚠️ Frida port \(port) is open")
                return true
            }
        }
        
        return false
    }
    
    private static func isPortOpen(port: UInt16) -> Bool {
        let sockfd = socket(AF_INET, SOCK_STREAM, 0)
        guard sockfd != -1 else {
            return false
        }
        
        defer {
            close(sockfd)
        }
        
        var addr = sockaddr_in()
        addr.sin_family = sa_family_t(AF_INET)
        addr.sin_port = port.bigEndian
        addr.sin_addr.s_addr = inet_addr("127.0.0.1")
        
        let result = withUnsafePointer(to: &addr) {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
                connect(sockfd, $0, socklen_t(MemoryLayout<sockaddr_in>.size))
            }
        }
        
        return result == 0
    }
    
    // MARK: - Detection Method 3: Check Thread Count
    
    private static func hasSuspiciousThreadCount() -> Bool {
        var threadList: thread_act_array_t?
        var threadCount: mach_msg_type_number_t = 0
        
        let result = task_threads(mach_task_self_, &threadList, &threadCount)
        
        guard result == KERN_SUCCESS else {
            return false
        }
        
        defer {
            if let list = threadList {
                vm_deallocate(
                    mach_task_self_,
                    vm_address_t(bitPattern: list),
                    vm_size_t(threadCount) * vm_size_t(MemoryLayout<thread_t>.size)
                )
            }
        }
        
        print("ℹ️ Current thread count: \(threadCount)")
        
        // Normal iOS app typically has 4-8 threads
        // Frida injection usually adds 3-5+ threads
        if threadCount > 12 {
            print("⚠️ Suspicious thread count: \(threadCount) (expected < 12)")
            return true
        }
        
        return false
    }
    
    // MARK: - Detection Method 4: Check for Frida Artifacts
    
    private static func hasFridaArtifacts() -> Bool {
        // Check for Frida's named pipes/sockets in /tmp
        let fridaPaths = [
            "/tmp",
            "/var/tmp"
        ]
        
        for basePath in fridaPaths {
            if checkDirectoryForFrida(path: basePath) {
                print("⚠️ Frida artifact found in: \(basePath)")
                return true
            }
        }
        
        return false
    }
    
    private static func checkDirectoryForFrida(path: String) -> Bool {
        do {
            let contents = try FileManager.default.contentsOfDirectory(atPath: path)
            return contents.contains { $0.lowercased().contains("frida") }
        } catch {
            return false
        }
    }
}
