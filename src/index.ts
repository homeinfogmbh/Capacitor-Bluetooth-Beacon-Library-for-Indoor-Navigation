import { registerPlugin } from '@capacitor/core';

import type { BleIndoorPositioningPlugin } from './definitions';

const BleIndoorPositioning = registerPlugin<BleIndoorPositioningPlugin>(
  'BleIndoorPositioning',
  {
    web: () => import('./web').then(m => new m.BleIndoorPositioningWeb())
  },
);

export * from './definitions';
export { BleIndoorPositioning };
