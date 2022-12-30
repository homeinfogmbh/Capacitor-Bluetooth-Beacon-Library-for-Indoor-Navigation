# bleindoorlocation

get the indoor position by use of BLE beacons

## Install

```bash
npm install bleindoorlocation
npx cap sync
```

## API

<docgen-index>

* [`getNearestBeacon()`](#getnearestbeacon)
* [`getAllBeacons()`](#getallbeacons)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getNearestBeacon()

```typescript
getNearestBeacon() => Promise<Beacon>
```

**Returns:** <code>Promise&lt;<a href="#beacon">Beacon</a>&gt;</code>

--------------------


### getAllBeacons()

```typescript
getAllBeacons() => Promise<[Beacon]>
```

**Returns:** <code>Promise&lt;[Beacon]&gt;</code>

--------------------


### Interfaces


#### Beacon

| Prop              | Type                  |
| ----------------- | --------------------- |
| **`distance`**    | <code>number</code>   |
| **`serviceUuid`** | <code>number</code>   |
| **`id1`**         | <code>number</code>   |
| **`id2`**         | <code>number</code>   |
| **`id3`**         | <code>number</code>   |
| **`dataFields`**  | <code>[number]</code> |

</docgen-api>
