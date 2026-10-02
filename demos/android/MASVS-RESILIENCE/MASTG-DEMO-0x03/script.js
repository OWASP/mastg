const open = Process.getModuleByName("libc.so").getExportByName("open");

Interceptor.attach(open, {
    onEnter(args) {
        console.log(`open("${args[0].readUtf8String()}")`);
    }
});

console.log("Hook installed: libc.so!open");
