import SwiftUI
import CoreLocation
import AVFoundation
import Contacts
import EventKit
import Photos
import CoreBluetooth
import CoreMotion
import UserNotifications
import HealthKit
import HomeKit
import Intents
import MapKit
import CoreNFC
import NetworkExtension
import PassKit

class PermissionManager: NSObject, CLLocationManagerDelegate, CBCentralManagerDelegate, HMHomeManagerDelegate, NFCNDEFReaderSessionDelegate {
    
    private let locationManager = CLLocationManager()
    private let motionManager = CMMotionActivityManager()
    private var bluetoothManager: CBCentralManager?
    private let healthStore = HKHealthStore()
    private let homeManager = HMHomeManager()
    private var nfcSession: NFCNDEFReaderSession?
    private var hasLoggedNFCOutcome = false
    private var hasRequestedAlwaysAuthorization = false

    private var permissionStatus: [String: Bool] = [
        "Location": false,
        "Camera": false,
        "Microphone": false,
        "Contacts": false,
        "Calendar": false,
        "PhotoLibrary": false,
        "Bluetooth": false,
        "Motion": false,
        "Notifications": false,
        "HealthKit": false,
        "HomeKit": false,
        "Siri": false,
        "Maps": false,
        "NFC": false,
        "WiFiInfo": false,
        "PassLibrary": false
    ]

    static let shared = PermissionManager()
    
    private var completionHandler: ((String) -> Void)?
    private var results: String = "--- Starting All Permission Requests ---\n" {
        didSet {
            DispatchQueue.main.async { [weak self] in
                guard let self else { return }
                self.completionHandler?(self.results)
            }
        }
    }
    
    private override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = kCLDistanceFilterNone
        bluetoothManager = CBCentralManager(delegate: self, queue: nil)
        homeManager.delegate = self
    }

    private func resetState() {
        results = "--- Starting All Permission Requests ---\n"
        for key in permissionStatus.keys {
            permissionStatus[key] = false
        }
        locationManager.stopUpdatingLocation()
        motionManager.stopActivityUpdates()
        bluetoothManager = CBCentralManager(delegate: self, queue: nil)
        nfcSession?.invalidate()
        nfcSession = nil
        hasLoggedNFCOutcome = false
        hasRequestedAlwaysAuthorization = false
    }

    func requestAllPermissionsSequentially(completion: @escaping (String) -> Void) {
        self.completionHandler = completion
        resetState()
        requestLocationPermission()
        // Subsequent requests are triggered within delegates or callbacks
    }
    
    private func requestLocationPermission() {
        locationManager.requestWhenInUseAuthorization()
    }

    private func requestCameraPermission() {
        AVCaptureDevice.requestAccess(for: .video) { granted in
            DispatchQueue.main.async {
                self.permissionStatus["Camera"] = granted
                self.results += "Requested Camera access... \(granted ? "✅" : "❌")\n"
                self.requestMicrophonePermission()
            }
        }
    }

    private func requestMicrophonePermission() {
        AVAudioSession.sharedInstance().requestRecordPermission { granted in
            DispatchQueue.main.async {
                self.permissionStatus["Microphone"] = granted
                self.results += "Requested Microphone access... \(granted ? "✅" : "❌")\n"
                self.requestContactsPermission()
            }
        }
    }
    
    private func requestContactsPermission() {
        CNContactStore().requestAccess(for: .contacts) { granted, _ in
            DispatchQueue.main.async {
                self.permissionStatus["Contacts"] = granted
                self.results += "Requested Contacts access... \(granted ? "✅" : "❌")\n"
                self.requestCalendarPermission()
            }
        }
    }

    private func requestCalendarPermission() {
        if #available(iOS 17.0, *) {
            EKEventStore().requestFullAccessToEvents { granted, _ in
                DispatchQueue.main.async {
                    self.permissionStatus["Calendar"] = granted
                    self.results += "Requested Calendar access... \(granted ? "✅" : "❌")\n"
                    self.requestPhotoLibraryPermission()
                }
            }
        } else {
            EKEventStore().requestAccess(to: .event) { granted, _ in
                DispatchQueue.main.async {
                    self.permissionStatus["Calendar"] = granted
                    self.results += "Requested Calendar access... \(granted ? "✅" : "❌")\n"
                    self.requestPhotoLibraryPermission()
                }
            }
        }
    }
    
    private func requestPhotoLibraryPermission() {
        PHPhotoLibrary.requestAuthorization(for: .readWrite) { status in
            DispatchQueue.main.async {
                let granted = (status == .authorized || status == .limited)
                self.permissionStatus["PhotoLibrary"] = granted
                self.results += "Requested Photo Library access... \(granted ? "✅" : "❌")\n"
                self.results += "Bluetooth was requested on init...\n"
                self.permissionStatus["Bluetooth"] = true
                self.requestNotificationPermission()
            }
        }
    }

    private func requestNotificationPermission() {
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            DispatchQueue.main.async {
                self.permissionStatus["Notifications"] = granted
                if let error = error {
                    self.results += "Requested Notifications... ❌ (\(error.localizedDescription))\n"
                } else {
                    self.results += "Requested Notifications... \(granted ? "✅" : "❌")\n"
                }
                self.requestMotionPermission()
            }
        }
    }

    private func requestMotionPermission() {
        motionManager.startActivityUpdates(to: .main) { _ in 
            self.motionManager.stopActivityUpdates()
            self.permissionStatus["Motion"] = true
            self.results += "Requested Motion & Fitness access... ✅\n"
            self.requestHealthKitPermission()
        }
    }

    private func requestHealthKitPermission() {
        guard HKHealthStore.isHealthDataAvailable(), let stepCount = HKObjectType.quantityType(forIdentifier: .stepCount) else {
            permissionStatus["HealthKit"] = false
            results += "Health data unavailable on this device... ❌\n"
            requestHomeKitPermission()
            return
        }

        let readTypes: Set = [stepCount]
        healthStore.requestAuthorization(toShare: [], read: readTypes) { granted, error in
            DispatchQueue.main.async {
                self.permissionStatus["HealthKit"] = granted
                if let error = error {
                    self.results += "Requested HealthKit read access... ❌ (\(error.localizedDescription))\n"
                } else {
                    self.results += "Requested HealthKit read access... \(granted ? "✅" : "❌")\n"
                }
                self.requestHomeKitPermission()
            }
        }
    }

    private func requestHomeKitPermission() {
        let status = homeManager.authorizationStatus
        let authorized = status == .authorized
        permissionStatus["HomeKit"] = authorized
        switch status {
        case .authorized:
            results += "HomeKit authorization status: Authorized ✅\n"
        case .restricted:
            results += "HomeKit authorization status: Restricted ❌\n"
        default:
            results += "HomeKit authorization status: Other (rawValue: \(status.rawValue)) ❌\n"
        }
        requestSiriPermission()
    }

    private func requestSiriPermission() {
        INPreferences.requestSiriAuthorization { status in
            DispatchQueue.main.async {
                let authorized = status == .authorized
                self.permissionStatus["Siri"] = authorized
                switch status {
                case .authorized:
                    self.results += "Requested Siri authorization... ✅\n"
                case .denied:
                    self.results += "Requested Siri authorization... ❌ (Denied)\n"
                case .restricted:
                    self.results += "Requested Siri authorization... ❌ (Restricted)\n"
                case .notDetermined:
                    self.results += "Requested Siri authorization... ❌ (Not Determined)\n"
                @unknown default:
                    self.results += "Requested Siri authorization... ❌ (Unknown)\n"
                }
                self.performMapsDemoRequest()
            }
        }
    }

    private func performMapsDemoRequest() {
        let request = MKLocalSearch.Request()
        request.naturalLanguageQuery = "OWASP"
        let search = MKLocalSearch(request: request)
        search.start { response, error in
            DispatchQueue.main.async {
                let success = (response?.mapItems.isEmpty == false)
                self.permissionStatus["Maps"] = success && error == nil
                if let error = error {
                    self.results += "Performed Maps local search... ❌ (\(error.localizedDescription))\n"
                } else if success {
                    self.results += "Performed Maps local search... ✅ Found \(response?.mapItems.count ?? 0) item(s)\n"
                } else {
                    self.results += "Performed Maps local search... ❌ (No results)\n"
                }
                self.requestNFCPermission()
            }
        }
    }

    private func requestNFCPermission() {
        guard NFCNDEFReaderSession.readingAvailable else {
            permissionStatus["NFC"] = false
            results += "NFC reader unavailable on this device... ❌\n"
            requestWiFiInfoAccess()
            return
        }

        permissionStatus["NFC"] = true
        results += "Attempting to start NFC reader session...\n"
        hasLoggedNFCOutcome = false
        nfcSession = NFCNDEFReaderSession(delegate: self, queue: nil, invalidateAfterFirstRead: true)
        nfcSession?.alertMessage = "DEMO: Hold a compatible NFC tag near your device."
        nfcSession?.begin()

        DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
            if !(self.hasLoggedNFCOutcome) {
                self.results += "NFC session timed out without tag interaction... ❌\n"
                self.hasLoggedNFCOutcome = true
                self.nfcSession?.invalidate()
                self.requestWiFiInfoAccess()
            }
        }
    }

    private func requestWiFiInfoAccess() {
        if #available(iOS 14.0, *) {
            NEHotspotNetwork.fetchCurrent { network in
                DispatchQueue.main.async {
                    if let network {
                        self.permissionStatus["WiFiInfo"] = true
                        self.results += "Fetched Wi-Fi information (SSID: \(network.ssid))... ✅\n"
                    } else {
                        self.permissionStatus["WiFiInfo"] = false
                        self.results += "Fetched Wi-Fi information... ❌ (No network details available)\n"
                    }
                    self.queryPassLibraryAccess()
                }
            }
        } else {
            permissionStatus["WiFiInfo"] = false
            results += "Access Wi-Fi Information requires iOS 14+... ❌\n"
            queryPassLibraryAccess()
        }
    }

    private func queryPassLibraryAccess() {
        if PKPassLibrary.isPassLibraryAvailable() {
            let passLibrary = PKPassLibrary()
            let passes = passLibrary.passes()
            permissionStatus["PassLibrary"] = true
            results += "Queried Pass Library... ✅ Found \(passes.count) pass(es)\n"
        } else {
            permissionStatus["PassLibrary"] = false
            results += "Pass Library unavailable on this device... ❌\n"
        }
        completePermissionFlow()
    }

    private func completePermissionFlow() {
        results += "\n--- All permissions and entitlement-backed APIs have been exercised. Review device prompts or logs for detailed results. ---\n"
        completionHandler?(results)
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        switch manager.authorizationStatus {
        case .notDetermined:
            return
        case .authorizedWhenInUse:
            if !hasRequestedAlwaysAuthorization {
                hasRequestedAlwaysAuthorization = true
                results += "Requested Location (When In Use)... ✅\n"
                manager.startUpdatingLocation()
                manager.requestAlwaysAuthorization()
                return
            }
        case .authorizedAlways:
            permissionStatus["Location"] = true
            results += "Requested Location (Always)... ✅\n"
            manager.startUpdatingLocation()
        default:
            permissionStatus["Location"] = false
            results += "Requested Location services... ❌\n"
        }
        DispatchQueue.main.async {
            self.requestCameraPermission()
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        let formatted = String(format: "%.4f, %.4f", location.coordinate.latitude, location.coordinate.longitude)
        results += "Received Location update: \(formatted)\n"
        manager.stopUpdatingLocation()
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        results += "Location updates failed: \(error.localizedDescription)\n"
    }
    func centralManagerDidUpdateState(_ central: CBCentralManager) {}

    func homeManagerDidUpdateHomes(_ manager: HMHomeManager) {
        if permissionStatus["HomeKit"] == false && homeManager.authorizationStatus == .authorized {
            permissionStatus["HomeKit"] = true
            results += "HomeKit manager updated homes indicating authorization... ✅\n"
        }
    }

    func readerSession(_ session: NFCNDEFReaderSession, didDetectNDEFs messages: [NFCNDEFMessage]) {
        DispatchQueue.main.async {
            guard !self.hasLoggedNFCOutcome else { return }
            self.hasLoggedNFCOutcome = true
            self.results += "NFC session detected \(messages.count) NDEF message(s)... ✅\n"
            session.invalidate()
            self.requestWiFiInfoAccess()
        }
    }

    func readerSession(_ session: NFCNDEFReaderSession, didInvalidateWithError error: Error) {
        DispatchQueue.main.async {
            guard !self.hasLoggedNFCOutcome else { return }
            self.hasLoggedNFCOutcome = true
            if (error as NSError).code == NFCReaderError.readerSessionInvalidationErrorUserCanceled.rawValue {
                self.results += "NFC session canceled by user... ❌\n"
            } else {
                self.results += "NFC session invalidated... ❌ (\(error.localizedDescription))\n"
            }
            self.requestWiFiInfoAccess()
        }
    }
}


struct MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        PermissionManager.shared.requestAllPermissionsSequentially(completion: completion)
    }
}
