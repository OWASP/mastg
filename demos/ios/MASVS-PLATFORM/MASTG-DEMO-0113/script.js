const classes = ["UITextField", "UITextView", "UISearchBar"];

classes.forEach(inputClass => hook(inputClass));

function hook(inputClass) {
  const cls = ObjC.classes[inputClass];
  if (!cls || !cls["- resignFirstResponder"]) {
    return;
  }
  Interceptor.attach(cls["- resignFirstResponder"].implementation, {
    onEnter(args) {
      const self = new ObjC.Object(args[0]);
      const isSecure = self.isSecureTextEntry && self.isSecureTextEntry();

      let aid = null;
      if (self.$methods.indexOf("- accessibilityIdentifier") !== -1) {
        aid = self.accessibilityIdentifier();
      }

      let placeholder = null;
      if (self.$methods.indexOf("- placeholder") !== -1) {
        try {
          placeholder = self.placeholder();
        } catch (e) {
          placeholder = null;
        }
      }

      const masked = isSecure ? "Masked" : "Exposed";

      console.log(
        `${masked} [isSecureTextEntry=${isSecure}, class=${self.$className}, aid=${aid}, placeholder=${placeholder}]: "${self.text()}"`
      );
    }
  });
}
