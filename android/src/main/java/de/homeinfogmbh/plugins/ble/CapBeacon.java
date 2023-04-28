package de.homeinfogmbh.plugins.ble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

public class CapBeacon {

  double distance;
  int serviceUuid;
  Identifier id1;
  Identifier id2;
  Identifier id3;
  List<Long> dataFields;
  int rssi;
  int txPower;
  String bluetoothAddress;
  String bluetoothName;


    CapBeacon(@NonNull Beacon beacon) {
        this.distance = beacon.getDistance();
        this.serviceUuid = beacon.getServiceUuid();
        this.id1 = beacon.getId1();
        this.id2 = beacon.getId2();
        this.id3 = beacon.getId3();
        this.dataFields = beacon.getDataFields();
        this.rssi = beacon.getRssi();
        this.txPower = beacon.getTxPower();
        this.bluetoothAddress = beacon.getBluetoothAddress();
        this.bluetoothName = beacon.getBluetoothName();
    }

    @Override
  public boolean equals(Object o){
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CapBeacon newBeacon = (CapBeacon) o;
    return (
      distance >= ((CapBeacon) o).distance*0.8 &&
      distance <= ((CapBeacon) o).distance*1.2 &&
      serviceUuid == newBeacon.serviceUuid &&
      id1 == newBeacon.id1 && id2 == newBeacon.id2 &&
      id3 == newBeacon.id3 && Objects.equals(bluetoothAddress, newBeacon.bluetoothAddress) &&
      Objects.equals(bluetoothName, newBeacon.bluetoothName)
    );

    }
}
