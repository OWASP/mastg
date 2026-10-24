import SwiftUI
import UIKit

// SUMMARY: This sample demonstrates the use of both secure and insecure text input fields in an iOS app. It includes a login alert with a username field (insecure), a password field (insecure), and an OTP field (secure). Additionally, it presents a SwiftUI view for entering a second OTP, which is also secure. The test checks for the presence of text input fields and whether they are configured to mask sensitive information.

struct MastgTest {

    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            let alert = UIAlertController(
                title: "Login",
                message: "Please enter your credentials.",
                preferredStyle: .alert
            )

            alert.addTextField { tf in
                tf.placeholder = "Username"
                tf.autocorrectionType = .no
                tf.accessibilityIdentifier = "username_field"
            }

            // FAIL: [MASTG-TEST-0346] The password field has isSecureTextEntry set to false.
            alert.addTextField { tf in
                tf.placeholder = "Password"
                tf.isSecureTextEntry = false
                tf.accessibilityIdentifier = "password_field"
            }

            // PASS: [MASTG-TEST-0346] The OTP 1 field has isSecureTextEntry set to true.
            alert.addTextField { tf in
                tf.placeholder = "OTP 1"
                tf.isSecureTextEntry = true
                tf.keyboardType = .numberPad
                tf.accessibilityIdentifier = "otp_1_field"
            }

            alert.addAction(UIAlertAction(title: "Next", style: .default, handler: { _ in
                let username = alert.textFields?[0].text ?? ""
                let otp1 = alert.textFields?[2].text ?? ""
                let baseMessage = "Logged in as: \(username), OTP 1: \(otp1)"

                let otpView = OTPView { otp2 in
                    if let presenter = topViewController() {
                        presenter.dismiss(animated: true) {
                            completion("\(baseMessage), OTP 2: \(otp2)")
                        }
                    } else {
                        completion("\(baseMessage), OTP 2: \(otp2)")
                    }
                }

                let hosting = UIHostingController(rootView: otpView)

                if let presenter = topViewController() {
                    presenter.present(hosting, animated: true)
                }
            }))

            if let presenter = topViewController() {
                presenter.present(alert, animated: true)
            } else {
                completion("Failed to present alert.")
            }
        }
    }

    struct OTPView: View {
        @State private var otp2: String = ""
        var onSubmit: (String) -> Void

        var body: some View {
            VStack(spacing: 20) {
                Text("Enter OTP 2")

                SecureField("OTP 2", text: $otp2)
                    .keyboardType(.numberPad)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .accessibilityIdentifier("otp_2_field")
                    .padding()

                Button("Submit") {
                    onSubmit(otp2)
                }
            }
            .padding()
        }
    }

    private static func topViewController(
        base: UIViewController? = {
            let scenes = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
            let keyWindow = scenes
                .flatMap { $0.windows }
                .first { $0.isKeyWindow }
            return keyWindow?.rootViewController
        }()
    ) -> UIViewController? {
        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController {
            return topViewController(base: tab.selectedViewController)
        }
        if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base
    }
}
