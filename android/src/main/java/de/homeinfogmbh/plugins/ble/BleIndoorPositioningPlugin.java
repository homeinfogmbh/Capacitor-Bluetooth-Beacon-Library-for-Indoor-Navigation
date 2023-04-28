package de.homeinfogmbh.plugins.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import org.altbeacon.beacon.Beacon;

import java.util.Arrays;

@CapacitorPlugin(name = "BleIndoorPositioning")
public class BleIndoorPositioningPlugin extends Plugin {

  private static final int REQUEST_ENABLE_BT = 24;
  static Beacon nearestBeacon;
  static Beacon[] allBeacons;
  static String UUID = "e93bc627-b399-4d43-853d-76d79d65039f";
  BroadcastReceiver mBroadcastReceiver;

//@Permission
  private final String[] permissions = new String[]{
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_PRIVILEGED,
    };

  @RequiresApi(api = Build.VERSION_CODES.S)
  private final String[] permissions31 = new String[]{
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.FOREGROUND_SERVICE,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION


  };

  @Override
  public void load() {
    this.getPermissions();
  }

  private void startWatch(){
    BluetoothManager bluetoothManager = this.getContext().getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    if (bluetoothAdapter == null) {
      Toast.makeText(this.getContext(), "Ihr Gerät unterstützt kein Bluetooth. Somit können wir Ihnen leider keine Navigation anbieten", Toast.LENGTH_SHORT).show();
      return;
    }else{
      if (!bluetoothAdapter.isEnabled()) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(null, enableBtIntent, REQUEST_ENABLE_BT);
      }
    }

    Intent serviceIntent = new Intent(getContext(), BleIndoorPositioning.class);
    Log.d("start", "startIntent");
    serviceIntent.putExtra("inputExtra", "Via Bluetooth wird periodisch ihr Standort im Gebäude ermittelt, lokal auf Ihrem Gerät. ");
    ContextCompat.startForegroundService(getContext(), serviceIntent);
    updateNearestBeacon();

    mBroadcastReceiver = new BroadcastReceiver(){

      @Override
      public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if ("newData".equals(action)) {
          updateNearestBeacon();
        }
      }

    };

    IntentFilter filter = new IntentFilter("newData");
    getContext().registerReceiver(mBroadcastReceiver, filter);
  }

  void updateNearestBeacon(){
    if(nearestBeacon == null){
      notifyListeners("updateBeaconsData", null);
    } else {
    notifyListeners("updateBeaconsData", this.formatReturnedData(new CapBeacon(nearestBeacon)));
      notifyListeners("updateNearestBeaconData", this.formatReturnedData(new CapBeacon(nearestBeacon)));
    }
  }

  @Override
  protected void handleOnPause() {
    super.handleOnPause();
  }

  @Override
  protected void handleOnResume() {
    super.handleOnResume();
  }

  @PluginMethod
  public void startListening(@NonNull PluginCall call){
    //start call of
    startWatch();
    Log.d("startL", "aaa");
    call.resolve();
  }

  @PluginMethod
  public void setUUID(@NonNull PluginCall call){
    String uuid = call.getString("UUID");
    Log.d("setUUID", uuid);
    if(!call.getData().has("UUID")){
      call.resolve();
      return;
    }
    BleIndoorPositioningPlugin.UUID = uuid;
    call.resolve();
  }




  /**
   *
   * @param call Plugin call
   */
  @PluginMethod
  public void getNearestBeacon(final PluginCall call) {
    JSObject data = getNearestBeaconData();
    call.resolve(data);
  }

  /**
   *
   * @param call Plugin call
   */
  @PluginMethod
  public void getAllBeacons(final PluginCall call) {
    JSObject data = getAllBeaconsData();
    call.resolve(data);
  }

  private JSObject getAllBeaconsData() {
    if (allBeacons != null) {
      JSObject ret = new JSObject();
      ret.put("data", formatReturnedData(allBeacons));
      return ret;
    }
    return null;
  }

  /**
   * ask for permissions if not granted
   */
  public void getPermissions(){
    for(String el:permissions){
      if(ContextCompat.checkSelfPermission(this.getContext(), el) != PackageManager.PERMISSION_GRANTED){
        Toast.makeText(this.getContext(), "Der Zugriff auf Bluetooth und Ihren Standort wird für die Navigation im Gebäude benötigt, diese Daten bleiben lokal auf Ihrem Gerät.", Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(this.getActivity(), new String[] { el }, 782);
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        for(String el:permissions31){
          if(ContextCompat.checkSelfPermission(this.getContext(), el) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this.getContext(), "Der Zugriff auf Bluetooth und Ihren Standort wird für die Navigation im Gebäude benötigt, diese Daten bleiben lokal auf Ihrem Gerät.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this.getActivity(), new String[] { el }, 782);
          }
        }
    }
  }

  @Nullable
  private JSObject getNearestBeaconData() {
    if (nearestBeacon != null) {
      JSObject ret = new JSObject();
      ret.put("data", formatReturnedData(new CapBeacon(nearestBeacon)));
      return ret;
    }
    return null;
  }

  /**
   * format data to be returned
   * can return null if no data is available
   * @return JSONObject for TypeScript site containing a CapBeacon
   * */
  @Nullable
  private JSObject formatReturnedData(@Nullable CapBeacon beacon) {
    JSObject beaconData = new JSObject();
    if(beacon == null){
      return null;
    }
      beaconData.put("distance", beacon.distance);
      beaconData.put("serviceUuid", beacon.serviceUuid);
      beaconData.put("id1", beacon.id1);
      beaconData.put("id2", beacon.id2);
      beaconData.put("id3", beacon.id3);
      beaconData.put("dataFields", beacon.dataFields);
      beaconData.put("rssi", beacon.rssi);
      beaconData.put("txPower", beacon.txPower);
      beaconData.put("bluetoothAddress", beacon.bluetoothAddress);
      beaconData.put("bluetoothName", beacon.bluetoothName);
      return beaconData;
  }


  /**
   * format data to be returned
   * can return null if no data is available
   * @return JSONObject for TypeScript site containing an array of CapBeacons
   * */
  @Nullable
  private JSObject formatReturnedData(@Nullable Beacon[] beacons) {
    if(beacons == null){
      return null;
    }
    CapBeacon[] capBeacons = new CapBeacon[beacons.length];
    for(int i = 0; i < beacons.length; i++){
      capBeacons[i] = new CapBeacon(beacons[i]);
    }
    JSObject beaconsData = new JSObject();
    Arrays.stream(capBeacons).forEach(beacon -> beaconsData.put(beacon.id1.toString(), formatReturnedData(beacon)));
    return beaconsData;
  }

}
