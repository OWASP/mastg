const UITextAutocorrectionType = {
  0: ".default",
  1: ".no",
  2: ".yes"
};

const UITextSpellCheckingType = {
  0: ".default",
  1: ".no",
  2: ".yes"
};

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
      const correctionType = UITextAutocorrectionType[self.autocorrectionType()];
      const isSecure = self.isSecureTextEntry && self.isSecureTextEntry();

      let aid = null;
      if (self.$methods.indexOf("- accessibilityIdentifier") !== -1) {
        aid = self.accessibilityIdentifier();
      }

      let spellType = null;
      try {
        spellType = UITextSpellCheckingType[self.spellCheckingType()];
      } catch (e) {
        spellType = null;
      }

      let placeholder = null;
      if (self.$methods.indexOf("- placeholder") !== -1) {
        try {
          placeholder = self.placeholder();
        } catch (e) {
          placeholder = null;
        }
      }

      let cacheable = "Eligible for caching";

      if (isSecure ||
        (correctionType === ".no" && spellType === ".no")) {
        cacheable = "Excluded from caching";
      }

      console.log(
        `${cacheable} [autocorrectionType=${correctionType}, spellCheckingType=${spellType}, isSecureTextEntry=${isSecure}, class=${self.$className}, aid=${aid}, placeholder=${placeholder}]: "${self.text()}"`
      );
    }
  });
}
