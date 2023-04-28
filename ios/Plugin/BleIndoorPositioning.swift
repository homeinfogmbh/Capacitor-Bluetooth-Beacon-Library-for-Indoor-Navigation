import Foundation
import CoreLocation

@objc public class BleIndoorPositioning: NSObject, CLLocationManagerDelegate {
    
    let locationManger: CLLocationManager
    let uuId: UUID
    let constraint: CLBeaconIdentityConstraint
    
    
    init(l: CLLocationManager, beaonUuid: String?){
        self.locationManger = l
        locationManger.activityType = .otherNavigation
        self.locationManger.requestAlwaysAuthorization()
        let tempUuid: String! = beaonUuid ?? "e93bc627-b399-4d43-853d-76d79d65039f"
        self.uuId = UUID(uuidString: tempUuid)!
        self.constraint = CLBeaconIdentityConstraint(uuid:  self.uuId)
        super.init()
        self.locationManger.delegate = self
        self.monitorBeacons()
    }
    
    func monitorBeacons(){
        if CLLocationManager.isMonitoringAvailable(for: CLBeaconRegion.self) {
            let beaconRegion = CLBeaconRegion(beaconIdentityConstraint: self.constraint, identifier: self.uuId.uuidString)
            self.locationManger.startMonitoring(for: beaconRegion)
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didEnterRegion region: CLRegion){
        if region is CLBeaconRegion {
            if CLLocationManager.isRangingAvailable() {
                manager.startRangingBeacons(satisfying: self.constraint)
            }
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didDetermineState state: CLRegionState, for region: CLRegion) {
        if(state.rawValue == 1){
            if region is CLBeaconRegion {
                if CLLocationManager.isRangingAvailable() {
                    manager.startRangingBeacons(satisfying: self.constraint)
                }
            }
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didExitRegion region: CLRegion) {
        BleIndoorPositioningPlugin.beacons = []
        BleIndoorPositioningPlugin.beacon = nil
        NotificationCenter.default.post(name: Notification.Name("newDataAvailable"), object: nil)
    }

    
    public func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion){
        if(beacons.count > 0){
            let oldBeacons = BleIndoorPositioningPlugin.beacons
            
            var cBeacons: [CAPBeacon] = []
            for beacon in beacons {
                cBeacons.append(CAPBeacon(beacon: beacon))
            }
            
            
            BleIndoorPositioningPlugin.beacons = cBeacons
            BleIndoorPositioningPlugin.beacon = CAPBeacon(beacon: beacons.first)
            if(newData(oldBeacons: oldBeacons, newBeacons: cBeacons)){
                NotificationCenter.default.post(name: Notification.Name("newDataAvailable"), object: nil)
            }
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didRange beacons: [CLBeacon], satisfying beaconConstraint: CLBeaconIdentityConstraint){
        if(beacons.count > 0){
            let oldBeacons = BleIndoorPositioningPlugin.beacons
            
            var cBeacons: [CAPBeacon] = []
            for beacon in beacons {
                cBeacons.append(CAPBeacon(beacon: beacon))
            }
            
            
            BleIndoorPositioningPlugin.beacons = cBeacons
            BleIndoorPositioningPlugin.beacon = CAPBeacon(beacon: beacons.first)
            if(newData(oldBeacons: oldBeacons, newBeacons: cBeacons)){
                NotificationCenter.default.post(name: Notification.Name("newDataAvailable"), object: nil)
            }
        }
    }
    /**
     @returns true when new Data is available
     */
    private func newData(oldBeacons: [CAPBeacon], newBeacons: [CAPBeacon]) -> Bool {
        if(oldBeacons.count != newBeacons.count){
            return true;
        }
        for(index, beacon) in newBeacons.enumerated() {
            if(!compareBeacon(a: oldBeacons[index], b: beacon)){
                return true;
            }
        }
        return false;
    }
    
    /**
            @returns true when beacons are equal
     */
    private func compareBeacon(a: CAPBeacon, b: CAPBeacon) -> Bool{
        return (a.distance >= (b.distance*0.8) &&
                a.distance <= (b.distance*1.2) &&
                a.id2 == b.id2 &&
                a.id3 == b.id3)
    }
}
