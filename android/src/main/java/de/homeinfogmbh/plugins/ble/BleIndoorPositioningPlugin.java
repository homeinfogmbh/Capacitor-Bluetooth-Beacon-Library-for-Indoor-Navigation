package de.homeinfogmbh.plugins.ble;


import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;


@CapacitorPlugin(name = "BleIndoorPositioning")
public class BleIndoorPositioningPlugin extends Plugin {

    private BleIndoorPositioning implementation;


    @Override
    public void load() {
        implementation = new BleIndoorPositioning();
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void getNearestBeacon(PluginCall call){
        implementation.getNearestBeacon();
        call.resolve();
    }

    @PluginMethod
    public void getAllBeacons(PluginCall call){
        implementation.getAllBeacons();
        call.resolve();
    }



    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void getCoords(PluginCall call){
        call.setKeepAlive(true);

    }

    public PluginCall getSavedCall(String callbackId){

        return null;
    }

    public void releaseCall(PluginCall call){

    }

    public void releaseCall(String callbackId){

    }


}
