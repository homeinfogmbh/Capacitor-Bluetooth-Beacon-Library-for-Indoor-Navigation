export interface BleIndoorPositioningPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
