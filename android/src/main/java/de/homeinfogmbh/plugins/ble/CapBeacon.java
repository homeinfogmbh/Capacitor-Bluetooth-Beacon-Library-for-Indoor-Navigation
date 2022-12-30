package de.homeinfogmbh.plugins.ble;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import java.util.List;

public class CapBeacon {

    CapBeacon(Beacon beacon) {
        double distance = beacon.getDistance();
        int serviceUuid = beacon.getServiceUuid();
        Identifier id1 = beacon.getId1();
        Identifier id2 = beacon.getId2();
        Identifier id3 = beacon.getId3();
        List<Long> dataFields = beacon.getDataFields();
        int rssi= beacon.getRssi();
        int	txPower = beacon.getTxPower();
        String bluetoothAddress = beacon.getBluetoothAddress();
        String	bluetoothName = beacon.getBluetoothName();
    }
}
