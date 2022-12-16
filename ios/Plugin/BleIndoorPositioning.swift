import Foundation

@objc public class BleIndoorPositioning: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
