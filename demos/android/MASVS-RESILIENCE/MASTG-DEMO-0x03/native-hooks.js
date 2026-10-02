// Load alongside bypass.js to see the ARM64 prologue before and after Frida hooks it.
Process.attachModuleObserver({
    onAdded(module) {
        if (module.name !== "libnativekey.so") return;
        for (const name of ["storeNativeSecret", "recoverNativeSecret"]) {
            const address = module.getExportByName("Java_org_owasp_mastestapp_MastgTest_" + name);
            const bytes = () => Array.from(new Uint8Array(address.readByteArray(8)), x => x.toString(16).padStart(2, "0")).join(" ");
            console.log(name + " before: " + bytes());
            Interceptor.attach(address, { onEnter() { console.log(name + " invoked"); } });
            Interceptor.flush();
            console.log(name + " after:  " + bytes());
        }
    }
});
