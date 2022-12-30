import { WebPlugin } from '@capacitor/core';

import type { BleIndoorPositioningPlugin } from './definitions';

export class BleIndoorPositioningWeb extends WebPlugin implements BleIndoorPositioningPlugin {
  getCoords():{ prototype: GeolocationPositionError; new(): GeolocationPositionError; readonly PERMISSION_DENIED: number; readonly POSITION_UNAVAILABLE: number; readonly TIMEOUT: number }{
    return GeolocationPositionError
  }

  getAllBeacons(): Promise<[any]> {
    throw Error('not implemented')
  }

  getNearestBeacon(): Promise<any> {
    throw Error('not implemented')
  }
}
