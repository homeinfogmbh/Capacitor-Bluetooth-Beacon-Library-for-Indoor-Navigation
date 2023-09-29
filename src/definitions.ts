import {PluginListenerHandle} from '@capacitor/core';

export interface BleIndoorPositioningPlugin {
  getNearestBeacon(): Promise<Beacon>;
  getAllBeacons(): Promise<[Beacon]>;
  getCurrentRoom():Promise<{data : string}>;
  loadMap(options: {jsonMap: string}):Promise<void>;
  findShortestPath(options: {start: string, end: string}): Promise<{data : string}>;
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
