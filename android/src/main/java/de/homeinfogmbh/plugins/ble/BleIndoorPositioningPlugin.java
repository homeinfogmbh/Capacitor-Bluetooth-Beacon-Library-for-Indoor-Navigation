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
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import static java.lang.Math.toIntExact;

import org.altbeacon.beacon.Beacon;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@CapacitorPlugin(name = "BleIndoorPositioning")
public class BleIndoorPositioningPlugin extends Plugin {


  MutableValueGraph<String, Integer> graph = null;
  HashSet<Node> roomSet = new HashSet<>();

  private static final int REQUEST_ENABLE_BT = 24;
  static Beacon nearestBeacon;
  static Beacon[] allBeacons;
  /**
   * default UUID when not defined different via setUUID
   */
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


  private void startWatch(PluginCall call) {
    //enable bluetooth if available
    BluetoothManager bluetoothManager = this.getContext().getSystemService(BluetoothManager.class);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    if (bluetoothAdapter == null) {
      Toast.makeText(this.getContext(), "Ihr Gerät unterstützt kein Bluetooth. Somit können wir Ihnen leider keine Navigation anbieten", Toast.LENGTH_SHORT).show();
      return;
    } else {
      if (!bluetoothAdapter.isEnabled()) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(call, enableBtIntent, REQUEST_ENABLE_BT);
        call.resolve();
      }
    }

    //start intent for Forgroundservice scanning for beacons
    Intent serviceIntent = new Intent(getContext(), BleIndoorPositioning.class);
    serviceIntent.putExtra("inputExtra", "Via Bluetooth wird periodisch ihr Standort im Gebäude ermittelt, lokal auf Ihrem Gerät. ");
    ContextCompat.startForegroundService(getContext(), serviceIntent);

    // create broadcastreveicer lisiting for new beaconsdata noptificaion
    mBroadcastReceiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //new new data avalabale notify JS via updateNearestBeacon()
        if ("newData".equals(action)) {
          updateNearestBeacon();
        }
      }

    };

    IntentFilter filter = new IntentFilter("newData");
    getContext().registerReceiver(mBroadcastReceiver, filter);
  }

  /**
   * notify JS that new beacondata ist available
   */
  void updateNearestBeacon() {
    if (nearestBeacon == null) {
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

  /**
   * start lisitening for beacons with given UUID
   */
  @PluginMethod
  public void startListening(@NonNull PluginCall call) {
    //start call of
    startWatch(call);
    call.resolve();
  }

  /**
   * set UUID identifier fpor found beacons
   */
  @PluginMethod
  public void setUUID(@NonNull PluginCall call) {
    String uuid = call.getString("UUID");
    if (!call.getData().has("UUID")) {
      call.resolve();
      return;
    }
    BleIndoorPositioningPlugin.UUID = uuid;
    call.resolve();
  }

  ////////////////////////////////////////////////////////////

  /**
   * return current room
   */
  @PluginMethod
  public void getCurrentRoom(final PluginCall call) {
    String ret = findRoom().isPresent() ? findRoom().get() : "unknown room";
    JSObject json = new JSObject();
    json.put("data", ret);
    call.resolve(json);
  }

  /**
   * find current room based on nearest beacon
   */
  private Optional<String> findRoom() {
    Beacon currentNearestBeacon = nearestBeacon;
    //find current room name/identifier
    Optional<String> opt = Optional.ofNullable(roomSet.stream().filter(node ->
            node.getId1().equals(currentNearestBeacon.getId1().toString()) &&
                    node.getId2().equals(currentNearestBeacon.getId2().toString()) &&
                    node.getId3().equals(currentNearestBeacon.getId3().toString())).findFirst().get().getName());
    return opt;
  }

  //load room map, create structure for finding rooms and shortest path available with Djisktra algorithm
  @PluginMethod
  public void loadMap(@NonNull PluginCall call) {
    org.json.JSONObject map = call.getData();
    this.graph = ValueGraphBuilder.undirected().build();
    try {
      org.json.simple.parser.JSONParser parser = new JSONParser();
      JSONObject obj = (JSONObject) parser.parse(map.toString());
      obj.keySet().forEach(b ->
      {
        JSONArray dataArray = (JSONArray) obj.get(b);
        dataArray.forEach(room -> {

          assert room != null;
          String roomIdentifier = Objects.requireNonNull(((JSONObject) room).get("roomIdentifier")).toString();
          AtomicReference<String> id1 = new AtomicReference<>();
          AtomicReference<String> id2 = new AtomicReference<>();
          AtomicReference<String> id3 = new AtomicReference<>();
          //add room to roomSet
          JSONObject beacon = (JSONObject) ((JSONObject) room).get("beacon");
          assert beacon != null;
          id1.set((String) beacon.get("id1"));
          id2.set((String) beacon.get("id2"));
          id3.set((String) beacon.get("id3"));
          roomSet.add(new Node(roomIdentifier, id1.get(), id2.get(), id3.get()));

          //add nodes to djisktra algorithm
          JSONArray neighbours = (JSONArray) ((JSONObject) room).get("nextRooms");
          neighbours.forEach(k ->
                  graph.putEdgeValue(roomIdentifier,
                          (String) Objects.requireNonNull(((JSONObject) k).get("neighbourIdentifier")),
                          toIntExact((long) Objects.requireNonNull(((JSONObject) k).get("distance")))
                  )
          );
        });
      });

    } catch (Throwable t) {
      Log.d("createMap", "Could not parse malformed JSON: \"" + map + "\"");
      call.reject("unable to create map");
    }
    call.resolve();
  }


  /**
   * return shortest path back to JS
   */
  @PluginMethod
  public void findShortestPath(@NonNull PluginCall call) {
    try {
      JSObject p = new JSObject();
      String start = call.getString("start");
      String end = call.getString("end");
      List<String> ret = Dijkstra.findShortestPath(this.graph, start, end);
      assert ret != null;
      p.put("path", ret.toArray(new String[0]));
      notifyListeners("pathUpdate", p);
      call.resolve(p);
    } catch (Exception e) {
      call.reject(e.toString());
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////

  /**
   * returns nearest beacon to JS
   *
   * @param call Plugin call
   */
  @PluginMethod
  public void getNearestBeacon(final PluginCall call) {
    JSObject data = getNearestBeaconData();
    call.resolve(data);
  }

  /**
   * returns all beacons to JS
   *
   * @param call Plugin call
   */
  @PluginMethod
  public void getAllBeacons(@NonNull final PluginCall call) {
    JSObject data = getAllBeaconsData();
    call.resolve(data);
  }

  @Nullable
  private JSObject getAllBeaconsData() {
    if (allBeacons != null) {
      JSObject ret = new JSObject();
      ret.put("data", formatReturnedData(allBeacons));
      return ret;
    }
    return null;
  }

  /**
   * ask for permissions if not already granted
   */
  public void getPermissions() {
    for (String el : permissions) {
      if (ContextCompat.checkSelfPermission(this.getContext(), el) != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this.getContext(), "Der Zugriff auf Bluetooth und Ihren Standort wird für die Navigation im Gebäude benötigt, diese Daten bleiben lokal auf Ihrem Gerät.", Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{el}, 782);
      }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      for (String el : permissions31) {
        if (ContextCompat.checkSelfPermission(this.getContext(), el) != PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(this.getContext(), "Der Zugriff auf Bluetooth und Ihren Standort wird für die Navigation im Gebäude benötigt, diese Daten bleiben lokal auf Ihrem Gerät.", Toast.LENGTH_LONG).show();
          ActivityCompat.requestPermissions(this.getActivity(), new String[]{el}, 782);
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
   *
   * @return JSONObject for TypeScript site containing a CapBeacon
   */
  @Nullable
  private JSObject formatReturnedData(@Nullable CapBeacon beacon) {
    JSObject beaconData = new JSObject();
    if (beacon == null) {
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
   *
   * @return JSONObject for TypeScript site containing an array of CapBeacons
   */
  @Nullable
  private JSObject formatReturnedData(@Nullable Beacon[] beacons) {
    if (beacons == null) {
      return null;
    }
    CapBeacon[] capBeacons = new CapBeacon[beacons.length];
    for (int i = 0; i < beacons.length; i++) {
      capBeacons[i] = new CapBeacon(beacons[i]);
    }
    JSObject beaconsData = new JSObject();
    Arrays.stream(capBeacons).forEach(beacon -> beaconsData.put(beacon.id1.toString(), formatReturnedData(beacon)));
    return beaconsData;
  }
}
