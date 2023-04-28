import {PluginListenerHandle} from '@capacitor/core';

export interface BleIndoorPositioningPlugin {
  getNearestBeacon(): Promise<Beacon>;
  getAllBeacons(): Promise<[Beacon]>;
  setUUID(options: {UUID: String}): Promise<void>;
  startListening(): Promise<void>;
  addListener(
      eventName: 'updateBeaconsData',
      listenerFunc: (beaconsData: [Beacon]) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
  addListener(
      eventName: 'updateNearestBeaconData',
      listenerFunc: (beaconData: Beacon ) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}
export interface Beacon {
  distance: number;
  serviceUuid: number;
  id1: number;
  id2: number;
  id3: number;
  dataFields: [number];
}
