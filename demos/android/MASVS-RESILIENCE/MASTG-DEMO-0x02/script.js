Java.perform(() => {

    const GLES20 = Java.use("android.opengl.GLES20");
    const glGetString = GLES20.glGetString.overload("int");

    glGetString.implementation = function (name) {
      if (name === 0x1f01) { // GL_RENDERER
          console.log("[!] Bypassing GPU check!")
          return Java.use("java.lang.String").$new("Adreno (TM) 650");
        }
        return glGetString.call(GLES20, name);
    };
});
