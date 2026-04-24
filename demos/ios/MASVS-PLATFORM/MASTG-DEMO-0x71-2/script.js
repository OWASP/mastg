if (ObjC.available) {
    const UIActivityViewController = ObjC.classes.UIActivityViewController;

    if (!UIActivityViewController) {
        console.log("[!] UIActivityViewController class not found.");
    } else {
        // Hook UIActivityViewController initWithActivityItems:applicationActivities:
        const initMethod = UIActivityViewController["- initWithActivityItems:applicationActivities:"];

        if (!initMethod || !initMethod.implementation) {
            console.log("[!] initWithActivityItems:applicationActivities: method not found.");
        } else {
            const oldInit = initMethod.implementation;

            initMethod.implementation = ObjC.implement(initMethod, function (self, sel, activityItemsPtr, applicationActivitiesPtr) {
                console.log("[UIActivityViewController] initWithActivityItems:applicationActivities:");

                try {
                    const activityItems = new ObjC.Object(activityItemsPtr);
                    console.log("  activityItems: " + activityItems.toString());
                } catch (e) {
                    console.log("  activityItems: (error reading: " + e + ")");
                }

                try {
                    const applicationActivities = new ObjC.Object(applicationActivitiesPtr);
                    console.log("  applicationActivities: " + applicationActivities.toString());
                } catch (e) {
                    console.log("  applicationActivities: (error reading: " + e + ")");
                }

                return oldInit(self, sel, activityItemsPtr, applicationActivitiesPtr);
            });
        }

        // Hook the excludedActivityTypes setter
        const setExcludedMethod = UIActivityViewController["- setExcludedActivityTypes:"];

        if (!setExcludedMethod || !setExcludedMethod.implementation) {
            console.log("[!] setExcludedActivityTypes: method not found.");
        } else {
            const oldSetExcluded = setExcludedMethod.implementation;

            setExcludedMethod.implementation = ObjC.implement(setExcludedMethod, function (self, sel, typesPtr) {
                try {
                    const types = new ObjC.Object(typesPtr);
                    console.log("[UIActivityViewController] excludedActivityTypes: " + types.toString());
                } catch (e) {
                    console.log("[UIActivityViewController] excludedActivityTypes: (error reading: " + e + ")");
                }

                return oldSetExcluded(self, sel, typesPtr);
            });
        }
    }

    console.log("[+] Hooks deployed successfully.");

} else {
    console.log("Objective-C runtime is not available.");
}
