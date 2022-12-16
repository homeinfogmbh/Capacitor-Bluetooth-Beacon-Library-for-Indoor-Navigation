import { WebPlugin } from '@capacitor/core';

import type { BleIndoorPositioningPlugin } from './definitions';

export class BleIndoorPositioningWeb extends WebPlugin implements BleIndoorPositioningPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
