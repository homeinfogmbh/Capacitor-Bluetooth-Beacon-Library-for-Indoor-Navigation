import { WebPlugin } from '@capacitor/core';

import type {BleIndoorPositioningPlugin} from './definitions';

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

  findShortestPath(options: {start: string, end: string}): Promise<{data:string}> {
    const a = options.start;
    const b = options.end;
    const c = a.concat(b);
    throw Error('not implemented' + c);
  }

  getCurrentRoom(): any | null {
    throw Error('not implemented');
  }

  loadMap(options: {jsonMap: string}):Promise<void>{
    const a = options.jsonMap;
    throw Error('not implemented' + a);
  }
}
