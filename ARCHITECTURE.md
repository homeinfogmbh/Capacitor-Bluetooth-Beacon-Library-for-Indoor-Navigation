# Capacitor-Bluetooth-Beacon-Library-for-Indoor-Navigation
Stand: 2026-07-31, geprüft gegen Commit bfdcbc5

## Zweck
Capacitor-Plugin/Bibliothek („bleindoorlocation") zur Innenraum-Positionierung
mittels BLE-Beacons — ermittelt die Indoor-Position für mobile Apps.

## Stack & Einstiegspunkte
TypeScript + Capacitor-Native-Plugin (Android/iOS). Build via Rollup
(`rollup.config.js`), Podspec `Bleindoorlocation.podspec`, native Anteile unter
`android/` und `ios/`, TS-Quellen unter `src/`. Package `bleindoorlocation`.

## Schnittstellen
### Konsumiert
- **BLE-Beacons** (Bluetooth Low Energy) über die native Plattform (Android/iOS).
### Bietet an
- **Capacitor-Plugin-API** zur Abfrage der Indoor-Position; von den mobilen Apps
  (`webAppIonic`/`mobilapp`) einbindbar.

## Deployment / Laufzeit
Als npm-/Capacitor-Plugin in eine App eingebunden und mit dieser gebaut.
Verwandt mit dem (privaten) Repo `BLE_indoor_cap`.

## Ersetzbarkeit
Kopplungsgrad: **niedrig–mittel**. Abgegrenztes Plugin; an Capacitor und die
BLE-Hardware gebunden.

## Weitere Doku
- `README.md`, `CONTRIBUTING.md`.
- Verwandt: `webAppIonic`, `BLE_indoor_cap` (privat).
- ⚠️ ANNAHME: Zentrales Repo `homeinfo-architektur` noch nicht geprüft.
