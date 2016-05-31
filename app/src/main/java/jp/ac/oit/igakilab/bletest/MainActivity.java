package jp.ac.oit.igakilab.bletest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements BluetoothAdapter.LeScanCallback{
    private BleDetector detector;
    private TextView tvStatus;
    private final Handler handler = new Handler();
    private boolean isScanning = false;

    private TextView tvDetectLog;
    private TextView tvBeaconInfo;
    private ImageView ivDroidIcon;

    private IbeaconFrame recentBeacon;

    private int[] icons = {R.drawable.droid_front, R.drawable.droid_left,
            R.drawable.droid_back, R.drawable.droid_right};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detector = new BleDetector(this, this);
        tvStatus = (TextView)this.findViewById(R.id.blestatus);
        tvDetectLog = (TextView)this.findViewById(R.id.output);
        tvBeaconInfo = (TextView)this.findViewById(R.id.beaconinfo);
        ivDroidIcon = (ImageView)this.findViewById(R.id.droidicon);

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

        final Button bbd = (Button)findViewById(R.id.detailbtn);
        bbd.setText("Detail");
        bbd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showBeaconDetail();
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
            recentBeacon = IbeaconFrame.parseIbeaconData(record);
            //showBeaconDetail();
            tvBeaconInfo.setText(
                    "UUID: " + IbeaconFrame.byteToString(recentBeacon.getUuid(), IbeaconFrame.UUID_FORMAT) + "\n" +
                            "Major: " + recentBeacon.getMajor() + ", Minor: " + recentBeacon.getMinor()
            );

            ivDroidIcon.setImageResource(icons[recentBeacon.getMinor() % icons.length]);
        }
    }

    public void showBeaconDetail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("BeaconDetail");
        if( recentBeacon != null ){
            builder.setMessage(recentBeacon.toString());
        }else{
            builder.setMessage("beacon not detected");
        }
        builder.setPositiveButton("OK", null);
        builder.show();
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