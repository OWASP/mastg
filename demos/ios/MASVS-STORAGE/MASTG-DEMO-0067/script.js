const CONTAINER_PATH_LEN = "/var/mobile/Containers/Data/Application/XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX/".length

Interceptor.attach(Module.getGlobalExportByName("open"), {
  onEnter(args) {
      let path = args[0].readCString()
      if(path.indexOf("/var/mobile/Containers/Data/Application") != -1 &&
        ((path.indexOf("tmp") != CONTAINER_PATH_LEN && path.indexOf("Library/Caches") != CONTAINER_PATH_LEN ))){
        console.log(`\nopen(${path})`);
        // Use an arrow function so that `this` remains the same as in onEnter
        const printBacktrace = (maxLines = 8) => {
            console.log("Backtrace:");
            let backtrace = Thread.backtrace(this.context, Backtracer.ACCURATE)
                .map(DebugSymbol.fromAddress);

            for (let i = 0; i < Math.min(maxLines, backtrace.length); i++) {
                console.log(backtrace[i]);
            }
        }
        printBacktrace();  
      }
  }
});
