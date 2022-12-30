package de.homeinfogmbh.plugins.ble;

import android.content.Intent;

import org.altbeacon.beacon.Beacon;

public class BleIndoorPositioning {
    public Monitoring monitoring;

    public BleIndoorPositioning() {
        this.monitoring = new Monitoring();
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("de.homeinfogmbh.plugins.ble.Monitoring");
        monitoring.startService(serviceIntent);
    }

    //TODO enable bluetooth
    public void enableBluetooth(){

    }

    public void startScanning(){
    }

    public void returnBeacon(){

        // return UUid of beacon
    }

    public CapBeacon getNearestBeacon(){
        Beacon beacon = null;
        if(monitoring.nearestBeacon.isPresent()){
            beacon = monitoring.nearestBeacon.get();
            return new CapBeacon(beacon);
        }
        return null;
    }

    public CapBeacon[] getAllBeacons(){
        CapBeacon[] beacons = new CapBeacon[monitoring.allBeacons.size()];
        return monitoring.allBeacons.stream().map(CapBeacon::new).toArray(CapBeacon[]::new);
    }

    public String echo(String value) {
        return value;
    }
}
