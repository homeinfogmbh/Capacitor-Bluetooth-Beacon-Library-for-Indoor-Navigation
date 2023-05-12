import { WebPlugin } from '@capacitor/core';

import type {BleIndoorPositioningPlugin, Floorplan} from './definitions';

export class BleIndoorPositioningWeb
  extends WebPlugin
  implements BleIndoorPositioningPlugin
{
  setUUID(options: { UUID: String }): Promise<void> {
    const a = options.UUID;
    throw Error('not implemented ' + a);
  }

  startListening(): Promise<void> {
    throw Error('not implemented');
  }
  getAllBeacons(): Promise<[any]> {
    throw Error('not implemented');
  }

  getNearestBeacon(): Promise<any> {
    throw Error('not implemented');
  }

  findShortestPath(): string[] {
    throw Error('not implemented');
  }

  getCurrentRoom(): Floorplan | null {
    throw Error('not implemented');
  }

  loadData(): Promise<void> {
    throw Error('not implemented');
  }
}
