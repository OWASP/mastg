// SUMMARY: An app that shows a 3-second countdown popup when Start is tapped. The only
// user-visible feature is the countdown; there is no map, search, or any other
// location-based feature in the UI. Despite that, the app silently requests location access
// and records the GPS coordinates to a file while the countdown runs. The
// NSLocationWhenInUseUsageDescription purpose string is deceptive: it claims a
// "nearby content" feature that the app does not provide.

import UIKit
import CoreLocation

struct MastgTest {
    static var locationCapture: LocationCapture?

    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0361] The app requests location access and collects GPS
        // coordinates while showing a plain countdown popup. The
        // NSLocationWhenInUseUsageDescription purpose string ("to show you nearby content
        // and recommendations") is deceptive: the app has no location-based feature and
        // never shows any content related to the user's location.
        locationCapture = LocationCapture()

        locationCapture?.onAuthorized = {
            // Start the countdown only after permission is granted so GPS collection
            // is running for the full 3 seconds.
            presentCountdown(seconds: 3) {
                locationCapture?.stop()
                let fileURL = saveLocation(locationCapture?.capturedLocation)
                completion(buildResult(fileURL: fileURL, location: locationCapture?.capturedLocation))
                locationCapture = nil
            }
        }

        locationCapture?.onDenied = {
            let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
            let url = dir.appendingPathComponent("location_capture.txt")
            try? "Location access denied.".write(to: url, atomically: true, encoding: .utf8)
            completion("Location access denied.\n\nSaved to: location_capture.txt")
            locationCapture = nil
        }
    }

    // MARK: - Private

    private static func presentCountdown(seconds: Int, onFinish: @escaping () -> Void) {
        DispatchQueue.main.async {
            guard let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                  let window = scene.windows.first(where: \.isKeyWindow),
                  let rootVC = window.rootViewController else {
                onFinish(); return
            }
            var topVC = rootVC
            while let p = topVC.presentedViewController { topVC = p }

            let alert = UIAlertController(title: "3s Timer started", message: "Please wait… \(seconds)s", preferredStyle: .alert)
            topVC.present(alert, animated: true)

            var remaining = seconds
            let timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { t in
                remaining -= 1
                if remaining > 0 {
                    alert.message = "Please wait… \(remaining)s"
                } else {
                    t.invalidate()
                    alert.dismiss(animated: true, completion: onFinish)
                }
            }
            RunLoop.main.add(timer, forMode: .common)
        }
    }

    private static func saveLocation(_ location: CLLocation?) -> URL {
        let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let url = dir.appendingPathComponent("location_capture.txt")
        let content: String
        if let loc = location {
            let fmt = ISO8601DateFormatter()
            content = """
            Latitude:  \(loc.coordinate.latitude)
            Longitude: \(loc.coordinate.longitude)
            Altitude:  \(String(format: "%.1f", loc.altitude)) m
            Accuracy:  \(String(format: "%.1f", loc.horizontalAccuracy)) m
            Timestamp: \(fmt.string(from: loc.timestamp))
            """
        } else {
            content = "Location not available (no fix received)."
        }
        try? content.write(to: url, atomically: true, encoding: .utf8)
        return url
    }

    private static func buildResult(fileURL: URL, location: CLLocation?) -> String {
        if let loc = location {
            return """
            Countdown completed.

            [!] Location captured in the background:
            Latitude:  \(loc.coordinate.latitude)
            Longitude: \(loc.coordinate.longitude)
            Altitude:  \(String(format: "%.1f", loc.altitude)) m

            Saved to: \(fileURL.lastPathComponent)
            """
        } else {
            return """
            Countdown completed.

            [!] Location capture attempted (no GPS fix received in time).
            Saved to: \(fileURL.lastPathComponent)
            """
        }
    }
}

// MARK: - Location capture

final class LocationCapture: NSObject, CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    private(set) var capturedLocation: CLLocation?
    var onAuthorized: (() -> Void)?
    var onDenied: (() -> Void)?

    override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyKilometer
        manager.requestWhenInUseAuthorization()
    }

    func stop() {
        manager.stopUpdatingLocation()
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        switch manager.authorizationStatus {
        case .authorizedWhenInUse, .authorizedAlways:
            manager.startUpdatingLocation()
            onAuthorized?()
        case .denied, .restricted:
            onDenied?()
        default:
            break
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        capturedLocation = locations.last
    }
}
