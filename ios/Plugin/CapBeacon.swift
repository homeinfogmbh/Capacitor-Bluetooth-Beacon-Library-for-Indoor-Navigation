//
//  CapBeacon.swift
//  Bleindoorlocation
//
//  Created by sebastian on 24.03.23.
//

import Foundation
import CoreLocation

public class CAPBeacon: Encodable {
    var distance: Double
    var serviceUuid: Int
    var id1: String
    var id2: String
    var id3: String
    var dataFields: [UInt64]
    var rssi: Int
    var txPower: Int
    var bluetoothAddress: String
    var bluetoothName: String


    init(beacon: CLBeacon!) {
        self.distance = beacon.accuracy
        //not supported by ios
        self.serviceUuid = 0;
        self.id1 = beacon.uuid.uuidString
        self.id2 = beacon.major.stringValue
        self.id3 = beacon.minor.stringValue
        //not supported by ios
        self.dataFields = []
        self.rssi = beacon.rssi
        //not supported by ios
        self.txPower = 0;
        //not supported by ios
        self.bluetoothAddress = ""
        //not supported by ios
        self.bluetoothName = ""
    }
    
    public func equals(o: CAPBeacon?)-> Bool{
        if (o == nil) {return false}
        if (self === o) {return true};
        return (
            distance >= (o!.distance*0.8) &&
            distance <= (o!.distance*1.2) &&
            id1 == o!.id1 && id2 == o!.id2 &&
            id3 == o!.id3 && bluetoothAddress == o!.bluetoothAddress &&
            bluetoothName == o!.bluetoothName
        );
    
    }
}
