export interface BleIndoorPositioningPlugin {

  getNearestBeacon():Promise<Beacon>;

  getAllBeacons():Promise<[Beacon]>

}

export interface Beacon {
  distance: number;
  serviceUuid: number;
  id1: number;
  id2: number;
  id3: number;
  dataFields: [number];
}
