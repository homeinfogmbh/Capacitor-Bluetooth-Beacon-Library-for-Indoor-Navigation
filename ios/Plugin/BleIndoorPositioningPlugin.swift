import Foundation
import Capacitor
import CoreLocation

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(BleIndoorPositioningPlugin)
public class BleIndoorPositioningPlugin: CAPPlugin {
    private var implementation: BleIndoorPositioning? = nil

    static var beacon: CAPBeacon? = nil

    static var beacons: [CAPBeacon] = []

    var beaconRegionUUID: String! = nil;

    var locationManger: CLLocationManager! = nil

    var toast = UIViewController()


    @objc public override func load() {
        if(getPermissions()){
            NotificationCenter.default.addObserver(self, selector: #selector(self.methodOfReceivedNotification(notification:)), name: Notification.Name("newDataAvailable"), object: nil)
        }
    }

    @objc func startListening(_ call: CAPPluginCall){
        implementation = BleIndoorPositioning(l: locationManger, beaonUuid: beaconRegionUUID)
    }



    @objc func setUUID(_ call: CAPPluginCall){
        let uuid = call.getString("UUID") ?? ""
        if(uuid == ""){
            beaconRegionUUID = nil;
            call.resolve()
            return

        }
        beaconRegionUUID = uuid;
        call.resolve()
    }

    @objc func methodOfReceivedNotification(notification: Notification) {
        if(notification.name.rawValue == "newDataAvailable"){
            if(BleIndoorPositioningPlugin.beacon == nil){
                notifyCAPListeners(notifierId: "updateBeaconsData", msg:"")
                notifyCAPListeners(notifierId: "updateNearestBeaconData", msg: "")
            }
            let jsonEncoder = JSONEncoder()
            do {
                let encodedBeacon = try jsonEncoder.encode(BleIndoorPositioningPlugin.beacon)
                let encodedBeaconData = String(data: encodedBeacon, encoding: .utf8)

                let encodedBeacons = try jsonEncoder.encode(BleIndoorPositioningPlugin.beacons)
                let encodedBeaconsData = String(data: encodedBeacons, encoding: .utf8)

                notifyCAPListeners(notifierId: "updateBeaconsData", msg: encodedBeaconsData)
                notifyCAPListeners(notifierId: "updateNearestBeaconData", msg: encodedBeaconData)
            }
            catch {
                print(error.localizedDescription)
            }


        }

    }

    @objc func getNearestBeacon(_ call: CAPPluginCall) {
        let jsonEncoder = JSONEncoder()
        var encodedBeaconData: String = ""
        do {
           let encodedBeacon = try jsonEncoder.encode(BleIndoorPositioningPlugin.beacon)
            encodedBeaconData = String(data: encodedBeacon, encoding: .utf8) ?? ""
        }
            catch {
                print(error.localizedDescription)
                call.reject(error.localizedDescription)
            }
        call.resolve(["data":encodedBeaconData])

    }

    @objc func getAllBeacons(_ call: CAPPluginCall){
        let jsonEncoder = JSONEncoder()
        var encodedBeaconData: String = ""
        do {
           let encodedBeacon = try jsonEncoder.encode(BleIndoorPositioningPlugin.beacons)
            encodedBeaconData = String(data: encodedBeacon, encoding: .utf8) ?? ""
        }
            catch {
                print(error.localizedDescription)
                call.reject(error.localizedDescription)
            }
        call.resolve(["data":encodedBeaconData])
    }

    private func getPermissions() -> Bool {
        locationManger = CLLocationManager()
        locationManger.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        CLLocationManager.locationServicesEnabled()
        var status: CLAuthorizationStatus
        if #available(iOS 14.0, *) {
            status = locationManger.authorizationStatus
        } else {
            status = CLLocationManager.authorizationStatus()
        };
        if(status == CLAuthorizationStatus.notDetermined || status == CLAuthorizationStatus.denied){
            toast.showToast(message: "Bitte erlauben Sie den Zugriff auf Bluetooth und Ihren Standort", font: .systemFont(ofSize: 12.0))
            locationManger.requestAlwaysAuthorization()
            print("permussiosn denied")
        }
        return true
    }

    public func notifyCAPListeners(notifierId: String!, msg: String!){
        self.notifyListeners(notifierId, data: ["data": msg as Any]);
    }
}

extension UIViewController {
    func showToast(message: String!, font: UIFont){
        let toastlabel = UILabel(frame: CGRect(x:self.view.frame.size.width/2 - 75,
                                               y: self.view.frame.size.height-100,
                                               width: 150, height: 35))
        toastlabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastlabel.textColor = UIColor.white
        toastlabel.font = font
        toastlabel.textAlignment = .center
        toastlabel.text = message
        toastlabel.alpha = 1.0
        toastlabel.layer.cornerRadius = 10;
        toastlabel.clipsToBounds = true
        self.view.addSubview(toastlabel)
        UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: {
            toastlabel.alpha = 0.0}, completion: {(isCompleted) in toastlabel.removeFromSuperview()
            })

    }

}
