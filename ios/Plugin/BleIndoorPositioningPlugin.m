#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(BleIndoorPositioningPlugin, "BleIndoorPositioning",
           CAP_PLUGIN_METHOD(getAllBeacons, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getNearestBeacon, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(startListening, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(setUUID, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(loadMap, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getCurrentRoom, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(findShortestPath, CAPPluginReturnPromise);
)
