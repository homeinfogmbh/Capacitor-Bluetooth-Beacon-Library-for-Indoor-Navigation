package de.homeinfogmbh.plugins.ble;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Monitoring extends Service implements BeaconConsumer {

    /*
    …
    …
    …
    */

    //Points from the final chart
    //Notice: axes here are inverted
    private final int numPoints = 6;
    private final double[] beaconRSSI = {-35, -48.4, -55.8, -61.12, -62.56, -69};
    private final double[] beaconDistance = {0, 65, 99, 149, 209, 238};
    private BeaconManager beaconManager;
    protected Optional<Beacon> nearestBeacon;
    protected Collection<Beacon> allBeacons;


    private String calculateDistance(double averageRSSI, double x1, double y1, double x2, double y2) {
        double slope = (y2 - y1) / (x2 - x1);
        double b = y2 - slope * x2;
        double distance = slope * averageRSSI + b;
        return Double.toString(distance);
    }

    /*
    …
    …
    …
    */

    @Override
    public void onBeaconServiceConnect() {
        final int[] currCalculation = {0};
        final int[] sumRSSI = {0};


        beaconManager.addRangeNotifier((collection, region) -> {

            if (collection.size() > 0) {
                currCalculation[0]++;
                sumRSSI[0] = collection.iterator().next().getRssi();
                int maxCalculations = 100;
                if(maxCalculations == currCalculation[0]){
                    Log.d("Tag", "Distance: " + Math.exp((double) (sumRSSI[0] / maxCalculations)));
                    currCalculation[0] = 0;
                    sumRSSI[0] = 0;
                    this.nearestBeacon = collection.stream().min((a,b) -> b.getRssi()-a.getRssi());
                    this.allBeacons = collection;
                }
            }
        });
        Region beaconRegion = new Region("de.homeinfogmbh.plugins.ble", null, null, null);
        beaconManager.startRangingBeacons(beaconRegion);
    }

    @NonNull
    private String getDistance(double averageRSSI) {
        if(averageRSSI > beaconRSSI[0]) return "0";
        for(int i = 0; i < numPoints - 1; i++) {
            if(averageRSSI <= beaconRSSI[i] && averageRSSI >= beaconRSSI[i + 1])
                return calculateDistance(averageRSSI, beaconRSSI[i], beaconDistance[i], beaconRSSI[i + 1], beaconDistance[i + 1]);
        }
        //if averageRSSI is less than -69
        return "Too far";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        return null;
    }
}
