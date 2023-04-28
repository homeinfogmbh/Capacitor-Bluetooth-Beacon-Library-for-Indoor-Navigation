package de.homeinfogmbh.plugins.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.getcapacitor.PluginMethod;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;


public class BleIndoorPositioning extends Service {

  public static final String CHANNEL_ID = "BleForegroundServiceChannel";

  BleIndoorPositioning.MyBinder binder = new BleIndoorPositioning.MyBinder();
  public BeaconManager beaconManager;



  /**
   * create notification channel to inform user of foreground service
   * */
  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel serviceChannel = new NotificationChannel(
        CHANNEL_ID,
        "Foreground Service Channel",
        NotificationManager.IMPORTANCE_DEFAULT
      );
      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(serviceChannel);
    }
  }


  /**
   * start foreground service and start scanning
   * */
  @Override
  public int onStartCommand(@NonNull Intent intent, int flags, int startId) {

    String input = intent.getStringExtra("inputExtra");
    createNotificationChannel();
    Intent notificationIntent = new Intent(this, BleIndoorPositioningPlugin.class);
    @SuppressLint("UnspecifiedImmutableFlag")
    PendingIntent pendingIntent = PendingIntent.getActivity(this,
      0, notificationIntent, 0);
    //set notification data
    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Foreground Service")
      .setContentText(input)
      .setSmallIcon(R.drawable.ic_launcher_foreground)
      .setContentIntent(pendingIntent)
      .build();

    //start foreground service
    this.startForeground(456, notification);


    // create beaconManager for periodically scanning for bluetooth beacons
    beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(getApplicationContext());

    beaconManager.enableForegroundServiceScanning(notification, 456);
    beaconManager.setEnableScheduledScanJobs(false);
    beaconManager.setBackgroundBetweenScanPeriod(2000);
    beaconManager.setBackgroundScanPeriod(2000);
    beaconManager.getBeaconParsers().clear();

    //set beacon layout
    beaconManager.getBeaconParsers().add(new BeaconParser().
      setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
    beaconManager.setBackgroundBetweenScanPeriod(2000);
    beaconManager.setForegroundBetweenScanPeriod(2000);
    Region beaconRegion = new Region(BleIndoorPositioningPlugin.UUID, Identifier.parse(BleIndoorPositioningPlugin.UUID), null, null);


    beaconManager.startRangingBeacons(beaconRegion);

    // range listener, on beacons in range change if there are changes and notify
    beaconManager.addRangeNotifier((beacons, region) -> {
      if(beacons.size()==0){
        BleIndoorPositioningPlugin.allBeacons = null;
        BleIndoorPositioningPlugin.nearestBeacon = null;

        //notify class via broadcast that new data is available
        Intent intent1 = new Intent("newData");
        intent1.setPackage(getPackageName());
        intent1.putExtra("newData","newDataAvailable");
        getApplicationContext().sendBroadcast(intent1);
      }
      else {
        //if number of beacons is equal but there may be changes
        if (detectChanges(beacons, BleIndoorPositioningPlugin.allBeacons)) {
          BleIndoorPositioningPlugin.allBeacons = beacons.toArray(new Beacon[0]);
          BleIndoorPositioningPlugin.nearestBeacon = beacons.stream().min(Comparator.comparing(Beacon::getDistance)).isPresent()?beacons.stream().min(Comparator.comparing(Beacon::getDistance)).get():null;

          //notify class via broadcast that new data is available
          Intent intent1 = new Intent("newData");
          intent1.setPackage(getPackageName());
          intent1.putExtra("newData","newDataAvailable");
          getApplicationContext().sendBroadcast(intent1);
        }
      }
    });
    return START_NOT_STICKY;
  }


  /**
   * detect changes in Beacons
   * @return true if changes are found
   * */
  private boolean detectChanges(@NonNull Collection<Beacon> beaconsCollection, Beacon[] oldBeacons){
    //check if first scan
    if (BleIndoorPositioningPlugin.allBeacons == null) {
      return true;
    }
    // check if beacons length is different
    Beacon[] newBeacons = beaconsCollection.toArray(new Beacon[0]);
    if(newBeacons.length != oldBeacons.length){
      return true;
    }
    // compare all beacons on changes
    for(int i = 0; i < newBeacons.length;i++){
      if(!areEqual(newBeacons[i],oldBeacons[i])){
        return true;
      }
    }
    return false;
  }

  /**
   * compare beacons
   * detected changes: distance change +/- 20%, serviceUUid, id's, bluetooth name and address
   * @param a new beacon
   * @param b old beacon
   * @return true if beacon a and b are equal, otherwise false
   * */
  private boolean areEqual(@NonNull Beacon a, @NonNull Beacon b){
    return a.getDistance() >= b.getDistance()*0.8 &&
      a.getDistance() <= b.getDistance()*1.2 &&
      a.getServiceUuid() == b.getServiceUuid() &&
      a.getId1() == b.getId1() && a.getId2() == b.getId2() &&
      a.getId3() == a.getId3() && Objects.equals(a.getBluetoothAddress(), b.getBluetoothAddress()) &&
      Objects.equals(a.getBluetoothName(), b.getBluetoothName());
  }


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  public class MyBinder extends Binder {
    public BleIndoorPositioning getService() {
      return BleIndoorPositioning.this;
    }
  }
}

