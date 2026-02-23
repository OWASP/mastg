'use strict';

var Colors = {
    RESET: "\x1b[0m",
    GREEN: "\x1b[32m",
    RED: "\x1b[31m",
    YELLOW: "\x1b[33m",
    CYAN: "\x1b[36m",
    BOLD: "\x1b[1m"
};

function printStatus(permission, status, isGranted) {
    var color = isGranted ? Colors.GREEN : Colors.RED;
    var icon = isGranted ? "GRANTED" : "DENIED ";
    console.log(Colors.CYAN + permission.padEnd(20) + Colors.RESET + " | " + color + icon + Colors.RESET + " | " + status);
}

// LOCATION

function traceLocationPermission() {
    var CLLocationManager = ObjC.classes.CLLocationManager;
    if (!CLLocationManager) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "AuthorizedAlways", 4: "AuthorizedWhenInUse"};

    try {
        Interceptor.attach(CLLocationManager['- requestWhenInUseAuthorization'].implementation, {
            onEnter: function(args) {
                printStatus("Location", "Requesting...", false);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(CLLocationManager['- requestAlwaysAuthorization'].implementation, {
            onEnter: function(args) {
                printStatus("Location", "Requesting Always...", false);
            }
        });
    } catch(e) {}

    setTimeout(function() {
        try {
            var resolver = new ApiResolver('objc');

            resolver.enumerateMatches('-[* locationManager:didChangeAuthorizationStatus:]').forEach(function(match) {
                Interceptor.attach(match.address, {
                    onEnter: function(args) {
                        var status = new NativePointer(args[3]).toInt32();
                        var statusStr = statusMap[status] || "Unknown(" + status + ")";
                        var granted = status === 3 || status === 4;
                        printStatus("Location", statusStr, granted);
                    }
                });
            });

            resolver.enumerateMatches('-[* locationManagerDidChangeAuthorization:]').forEach(function(match) {
                Interceptor.attach(match.address, {
                    onEnter: function(args) {
                        var manager = ObjC.Object(args[2]);
                        var s = Number(manager.authorizationStatus());
                        var statusStr = statusMap[s] || "Unknown(" + s + ")";
                        var granted = s === 3 || s === 4;
                        printStatus("Location", statusStr, granted);
                    }
                });
            });
        } catch(e) {
            console.log("  [!] Location delegate hook error: " + e);
        }
    }, 500);
}

// CAMERA 

function traceCameraPermission() {
    var AVCaptureDevice = ObjC.classes.AVCaptureDevice;
    if (!AVCaptureDevice) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "Authorized"};

    try {
        Interceptor.attach(AVCaptureDevice['+ authorizationStatusForMediaType:'].implementation, {
            onEnter: function(args) {
                this.mediaType = ObjC.Object(args[2]).toString();
            },
            onLeave: function(retval) {
                var status = retval.toInt32();
                var statusStr = statusMap[status] || "Unknown(" + status + ")";
                var granted = status === 3;
                var name = this.mediaType === "vide" ? "Camera" : "Microphone";
                printStatus(name, statusStr, granted);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(AVCaptureDevice['+ requestAccessForMediaType:completionHandler:'].implementation, {
            onEnter: function(args) {
                var mediaType = ObjC.Object(args[2]).toString();
                var name = mediaType === "vide" ? "Camera" : "Microphone";
                var block = new ObjC.Block(args[3]);
                var origImpl = block.implementation;

                block.implementation = function(granted) {
                    printStatus(name, granted ? "Authorized" : "Denied", granted);
                    origImpl(granted);
                };
            }
        });
    } catch(e) {
        console.log("  [!] Camera request hook error: " + e);
    }
}

// MICROPHONE

function traceMicrophonePermission() {
    var AVAudioSession = ObjC.classes.AVAudioSession;
    if (!AVAudioSession) return;

    var statusMap = {0: "Undetermined", 1: "Denied", 2: "Granted"};

    try {
        Interceptor.attach(AVAudioSession['- recordPermission'].implementation, {
            onLeave: function(retval) {
                var status = retval.toInt32();
                var statusStr = statusMap[status] || "Unknown(" + status + ")";
                var granted = status === 2;
                printStatus("Microphone", statusStr, granted);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(AVAudioSession['- requestRecordPermission:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[2]);
                var origImpl = block.implementation;

                block.implementation = function(granted) {
                    printStatus("Microphone", granted ? "Granted" : "Denied", granted);
                    origImpl(granted);
                };
            }
        });
    } catch(e) {
        console.log("  [!] Microphone request hook error: " + e);
    }
}

// CONTACTS - Uses completion handler

function traceContactsPermission() {
    var CNContactStore = ObjC.classes.CNContactStore;
    if (!CNContactStore) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "Authorized"};

    // Hook authorizationStatusForEntityType:
    try {
        Interceptor.attach(CNContactStore['+ authorizationStatusForEntityType:'].implementation, {
            onLeave: function(retval) {
                var status = retval.toInt32();
                var statusStr = statusMap[status] || "Unknown(" + status + ")";
                var granted = status === 3;
                printStatus("Contacts", statusStr, granted);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(CNContactStore['- requestAccessForEntityType:completionHandler:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[3]);
                var origImpl = block.implementation;

                block.implementation = function(granted, error) {
                    printStatus("Contacts", granted ? "Authorized" : "Denied", granted);
                    origImpl(granted, error);
                };
            }
        });
    } catch(e) {
        console.log("  [!] Contacts request hook error: " + e);
    }
}

// CALENDAR

function traceCalendarPermission() {
    var EKEventStore = ObjC.classes.EKEventStore;
    if (!EKEventStore) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "Authorized", 4: "WriteOnly", 5: "FullAccess"};

    try {
        Interceptor.attach(EKEventStore['+ authorizationStatusForEntityType:'].implementation, {
            onEnter: function(args) {
                this.entityType = args[2].toInt32();
            },
            onLeave: function(retval) {
                var status = retval.toInt32();
                var statusStr = statusMap[status] || "Unknown(" + status + ")";
                var granted = status === 3 || status === 5;
                var name = this.entityType === 0 ? "Calendar" : "Reminders";
                printStatus(name, statusStr, granted);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(EKEventStore['- requestAccessToEntityType:completion:'].implementation, {
            onEnter: function(args) {
                var entityType = args[2].toInt32();
                var name = entityType === 0 ? "Calendar" : "Reminders";
                var block = new ObjC.Block(args[3]);
                var origImpl = block.implementation;

                block.implementation = function(granted, error) {
                    printStatus(name, granted ? "Authorized" : "Denied", granted);
                    origImpl(granted, error);
                };
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(EKEventStore['- requestFullAccessToEventsWithCompletion:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[2]);
                var origImpl = block.implementation;

                block.implementation = function(granted, error) {
                    printStatus("Calendar", granted ? "FullAccess" : "Denied", granted);
                    origImpl(granted, error);
                };
            }
        });
    } catch(e) {}
}

// PHOTO LIBRARY

function tracePhotoLibraryPermission() {
    var PHPhotoLibrary = ObjC.classes.PHPhotoLibrary;
    if (!PHPhotoLibrary) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "Authorized", 4: "Limited"};

    try {
        Interceptor.attach(PHPhotoLibrary['+ authorizationStatusForAccessLevel:'].implementation, {
            onLeave: function(retval) {
                var status = retval.toInt32();
                var statusStr = statusMap[status] || "Unknown(" + status + ")";
                var granted = status === 3 || status === 4;
                printStatus("PhotoLibrary", statusStr, granted);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(PHPhotoLibrary['+ requestAuthorizationForAccessLevel:handler:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[3]);
                var origImpl = block.implementation;

                block.implementation = function(status) {
                    var s = Number(status);
                    var statusStr = statusMap[s] || "Unknown(" + s + ")";
                    var granted = s === 3 || s === 4;
                    printStatus("PhotoLibrary", statusStr, granted);
                    origImpl(status);
                };
            }
        });
    } catch(e) {
        console.log("  [!] PhotoLibrary request hook error: " + e);
    }
}

// NOTIFICATIONS - Uses completion handler=

function traceNotificationsPermission() {
    var UNUserNotificationCenter = ObjC.classes.UNUserNotificationCenter;
    if (!UNUserNotificationCenter) return;

    try {
        Interceptor.attach(UNUserNotificationCenter['- requestAuthorizationWithOptions:completionHandler:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[3]);
                var origImpl = block.implementation;

                block.implementation = function(granted, error) {
                    printStatus("Notifications", granted ? "Authorized" : "Denied", granted);
                    origImpl(granted, error);
                };
            }
        });
    } catch(e) {
        console.log("  [!] Notifications request hook error: " + e);
    }

    try {
        Interceptor.attach(UNUserNotificationCenter['- getNotificationSettingsWithCompletionHandler:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[2]);
                var origImpl = block.implementation;

                block.implementation = function(settings) {
                    var s = Number(settings.authorizationStatus());
                    var statusMap = {0: "NotDetermined", 1: "Denied", 2: "Authorized", 3: "Provisional", 4: "Ephemeral"};
                    var statusStr = statusMap[s] || "Unknown(" + s + ")";
                    var granted = s === 2 || s === 3 || s === 4;
                    printStatus("Notifications", statusStr, granted);
                    origImpl(settings);
                };
            }
        });
    } catch(e) {}
}

// MOTION & FITNESS - Uses activity updates

function traceMotionPermission() {
    var CMMotionActivityManager = ObjC.classes.CMMotionActivityManager;
    if (!CMMotionActivityManager) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "Authorized"};


    try {
        Interceptor.attach(CMMotionActivityManager['+ authorizationStatus'].implementation, {
            onLeave: function(retval) {
                var status = retval.toInt32();
                var statusStr = statusMap[status] || "Unknown(" + status + ")";
                var granted = status === 3;
                printStatus("Motion", statusStr, granted);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(CMMotionActivityManager['- startActivityUpdatesToQueue:withHandler:'].implementation, {
            onEnter: function(args) {
                printStatus("Motion", "Requesting...", false);
                var block = new ObjC.Block(args[3]);
                var origImpl = block.implementation;
                var reported = false;

                block.implementation = function(activity) {
                    if (!reported) {
                        reported = true;
                        if (activity) {
                            printStatus("Motion", "Authorized", true);
                        }
                    }
                    origImpl(activity);
                };
            }
        });
    } catch(e) {
        console.log("  [!] Motion request hook error: " + e);
    }
}

// BLUETOOTH

function traceBluetoothPermission() {
    var CBCentralManager = ObjC.classes.CBCentralManager;
    if (!CBCentralManager) return;

    var authMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "AllowedAlways"};
    var stateMap = {0: "Unknown", 1: "Resetting", 2: "Unsupported", 3: "Unauthorized", 4: "PoweredOff", 5: "PoweredOn"};

    setTimeout(function() {
        try {
            var resolver = new ApiResolver('objc');
            resolver.enumerateMatches('-[* centralManagerDidUpdateState:]').forEach(function(match) {
                Interceptor.attach(match.address, {
                    onEnter: function(args) {
                        var manager = ObjC.Object(args[2]);
                        var s = Number(manager.state());
                        if (s === 0 || s === 1) return; // skip Unknown and Resetting (transient states)
                        var a = Number(manager.authorization());
                        var stateStr = stateMap[s] || "Unknown(" + s + ")";
                        var authStr = authMap[a] || "Unknown(" + a + ")";
                        var granted = a === 3;
                        printStatus("Bluetooth", stateStr + " | " + authStr, granted);
                    }
                });
            });
        } catch(e) {}
    }, 500);
}

// HEALTHKIT

function traceHealthKitPermission() {
    var HKHealthStore = ObjC.classes.HKHealthStore;
    if (!HKHealthStore) return;

    try {
        Interceptor.attach(HKHealthStore['- requestAuthorizationToShareTypes:readTypes:completion:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[4]);
                var origImpl = block.implementation;

                block.implementation = function(success, error) {
                    printStatus("HealthKit", success ? "Authorized" : "Denied", success);
                    origImpl(success, error);
                };
            }
        });
    } catch(e) {
        console.log("  [!] HealthKit request hook error: " + e);
    }
}
// HOMEKIT

function traceHomeKitPermission() {
    var HMHomeManager = ObjC.classes.HMHomeManager;
    if (!HMHomeManager) return;

    function describeHomeKitStatus(s) {
        if (s === 0) return "NotDetermined";
        var flags = [];
        if (s & 1) flags.push("Determined");
        if (s & 2) flags.push("Restricted");
        if (s & 4) flags.push("Authorized");
        return flags.length > 0 ? flags.join("|") : "Unknown(" + s + ")";
    }

    var homeKitReported = false;

    setTimeout(function() {
        try {
            var resolver = new ApiResolver('objc');
            resolver.enumerateMatches('-[* homeManagerDidUpdateHomes:]').forEach(function(match) {
                Interceptor.attach(match.address, {
                    onEnter: function(args) {
                        if (homeKitReported) return;
                        var manager = ObjC.Object(args[2]);
                        try {
                            var s = Number(manager.authorizationStatus());
                            if (s !== 0) {
                                homeKitReported = true;
                                printStatus("HomeKit", describeHomeKitStatus(s), (s & 4) !== 0);
                            }
                        } catch(e) {}
                    }
                });
            });
        } catch(e) {}
    }, 500);

    // Fallback: poll authorizationStatus directly after each HMHomeManager init.
    // On iOS versions where homeManagerDidUpdateHomes: does not fire for denial,
    // the poll detects the NotDetermined → Determined transition by reading the
    // live value. Runs every 500ms until a non-zero status is observed.
    try {
        Interceptor.attach(HMHomeManager['- init'].implementation, {
            onLeave: function(retval) {
                homeKitReported = false;
                var manager = ObjC.Object(retval);
                function poll() {
                    if (homeKitReported) return;
                    try {
                        var s = Number(manager.authorizationStatus());
                        if (s !== 0) {
                            homeKitReported = true;
                            printStatus("HomeKit", describeHomeKitStatus(s), (s & 4) !== 0);
                            return;
                        }
                    } catch(e) { return; }
                    setTimeout(poll, 500);
                }
                setTimeout(poll, 500);
            }
        });
    } catch(e) {}
}
// SIRI

function traceSiriPermission() {
    var INPreferences = ObjC.classes.INPreferences;
    if (!INPreferences) return;

    var statusMap = {0: "NotDetermined", 1: "Restricted", 2: "Denied", 3: "Authorized"};

    try {
        Interceptor.attach(INPreferences['+ siriAuthorizationStatus'].implementation, {
            onLeave: function(retval) {
                var status = retval.toInt32();
                var statusStr = statusMap[status] || "Unknown(" + status + ")";
                var granted = status === 3;
                printStatus("Siri", statusStr, granted);
            }
        });
    } catch(e) {}

    try {
        Interceptor.attach(INPreferences['+ requestSiriAuthorization:'].implementation, {
            onEnter: function(args) {
                var block = new ObjC.Block(args[2]);
                var origImpl = block.implementation;

                block.implementation = function(status) {
                    var s = Number(status);
                    var statusStr = statusMap[s] || "Unknown(" + s + ")";
                    var granted = s === 3;
                    printStatus("Siri", statusStr, granted);
                    origImpl(status);
                };
            }
        });
    } catch(e) {
        console.log("  [!] Siri request hook error: " + e);
    }
}
// NFC

function traceNFCPermission() {
    var NFCNDEFReaderSession = ObjC.classes.NFCNDEFReaderSession;
    if (!NFCNDEFReaderSession) return;

    try {
        Interceptor.attach(NFCNDEFReaderSession['+ readingAvailable'].implementation, {
            onLeave: function(retval) {
                var available = retval.toInt32() !== 0;
                printStatus("NFC", available ? "Available" : "NotAvailable", available);
            }
        });
    } catch(e) {}
}

// PASS LIBRARY

function tracePassLibraryPermission() {
    var PKPassLibrary = ObjC.classes.PKPassLibrary;
    if (!PKPassLibrary) return;

    try {
        Interceptor.attach(PKPassLibrary['+ isPassLibraryAvailable'].implementation, {
            onLeave: function(retval) {
                var available = retval.toInt32() !== 0;
                printStatus("PassLibrary", available ? "Available" : "NotAvailable", available);
            }
        });
    } catch(e) {}
}

// MAIN

function installAllHooks() {
    console.log("");
    console.log(Colors.BOLD + "  Permission            | Status  | Details" + Colors.RESET);
    console.log("  " + "-".repeat(46));

    traceLocationPermission();
    traceCameraPermission();
    traceMicrophonePermission();
    traceContactsPermission();
    traceCalendarPermission();
    tracePhotoLibraryPermission();
    traceBluetoothPermission();
    traceMotionPermission();
    traceNotificationsPermission();
    traceHealthKitPermission();
    traceHomeKitPermission();
    traceSiriPermission();
    traceNFCPermission();
    tracePassLibraryPermission();

    console.log("");
    console.log(Colors.GREEN + "  Hooks installed. Tap 'Request All Permissions'..." + Colors.RESET);
    console.log(Colors.CYAN + "=".repeat(50) + Colors.RESET + "\n");
}

if (ObjC.available) {
    installAllHooks();
} else {
    console.log(Colors.RED + "[ERROR] Objective-C runtime not available" + Colors.RESET);
}
