package jp.ac.igakilab.bletest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;

/**
 * Created by Ryokun on 2016/05/30.
 */
public class BleDetector {
    private BluetoothManager bmanager;
    private BluetoothAdapter badapter;
    private BluetoothAdapter.LeScanCallback callback;


    void initBluetoothAdapter(Activity act){
        bmanager = (BluetoothManager)act.getSystemService(Context.BLUETOOTH_SERVICE);
        badapter = bmanager.getAdapter();
    }

    public BleDetector(Activity act, BluetoothAdapter.LeScanCallback cb){
        initBluetoothAdapter(act);
        setLeScanCallback(cb);
    }

    public BleDetector(Activity act){
        this(act, null);
    }

    public void setLeScanCallback(BluetoothAdapter.LeScanCallback cb){
        if( cb != null ){
            callback = cb;
        }else{
            callback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord){}
            };
        }
    }

    public void startLeScan(){
        badapter.startLeScan(callback);
    }

    public void stopLeScan(){
        badapter.stopLeScan(callback);
    }

    public BluetoothManager getBluetoothManager(){
        return bmanager;
    }

    public BluetoothAdapter getBluetoothAdapter(){
        return badapter;
    }

    public boolean isBluetoothEnabled(){
        return badapter.isEnabled();
    }
}
