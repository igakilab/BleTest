package jp.ac.igakilab.bletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements BluetoothAdapter.LeScanCallback{
    private BleDetector detector;
    private TextView tvStatus;
    private final Handler handler = new Handler();
    private boolean isScanning = false;

    private TextView tvBeaconDetail;
    private TextView tvDetectLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detector = new BleDetector(this, this);
        tvStatus = (TextView)this.findViewById(R.id.blestatus);
        tvDetectLog = (TextView)this.findViewById(R.id.output);
        tvBeaconDetail = (TextView)this.findViewById(R.id.detail);

        addLogText(tvDetectLog, detector.getBluetoothManager().toString(), true);
        addLogText(tvDetectLog, detector.getBluetoothAdapter().toString(), false);

        final Button bsw = (Button)findViewById(R.id.bleswitch);
        bsw.setText("START");
        bsw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if( isScanning ){
                    scanStop();
                    bsw.setText("START");
                }else{
                    if( scanStart() ) {
                        bsw.setText("STOP");
                    }
                }
            }
        });
    }

    public boolean scanStart(){
        if( detector.isBluetoothEnabled() ){
            isScanning = true;
            detector.startLeScan();
            tvStatus.setText("スキャン中...");
            return true;
        }else{
            tvStatus.setText("Bluetoothを有効にして下さい");
            return false;
        }
    }

    public void scanStop(){
        isScanning = false;
        detector.stopLeScan();
        tvStatus.setText("停止中");
    }

    public void detectBeacon(BluetoothDevice device, int rssi, byte[] record){
        String log = "ADDRESS:" + device.getAddress() + " RSSI:" + rssi;
        addLogText(tvDetectLog, log, false);

        if( IbeaconFrame.isIbeaconData(record) ){
            IbeaconFrame frame= IbeaconFrame.parseIbeaconData(record);
            StringBuffer buf = new StringBuffer();
            buf.append("UUID:" + IbeaconFrame.byteToString(frame.getUuid(), IbeaconFrame.UUID_FORMAT) + "\n");
            buf.append("Major: " + frame.getMajor() + " Minor: " + frame.getMinor() + "\n");
            buf.append(frame.toString());
            tvBeaconDetail.setText(buf.toString());
        }else {
            tvBeaconDetail.setText("cannot parse ibeacon data");
        }
    }

    public void addLogText(TextView tvl, String str, boolean refresh){
        String pre = tvl.getText().toString();
        if( refresh || pre.equals("") ){
            tvl.setText(str);
        }else{
            tvl.setText(pre + "\n" + str);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord){
        handler.post(new ViewBluetoothDeviceInfo(this, device, rssi, scanRecord));
    }
}

class ViewBluetoothDeviceInfo implements Runnable{
    private MainActivity mainAct;
    private BluetoothDevice device;
    private int rssi;
    byte[] scanRecord;
    public ViewBluetoothDeviceInfo(MainActivity a0, BluetoothDevice d0, int r0, byte[] s0){
        mainAct = a0;
        device = d0;
        rssi = r0;
        scanRecord = s0;
    }
    @Override
    public void run() {
        mainAct.detectBeacon(device, rssi, scanRecord);
    }
}

