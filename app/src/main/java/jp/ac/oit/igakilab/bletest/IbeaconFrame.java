package jp.ac.oit.igakilab.bletest;

import java.util.HashMap;

/**
 * Created by 崇晃 on 2016/05/30.
 */
public class IbeaconFrame {
    public static int[] UUID_FORMAT = {4, 6, 8, 10};

    private HashMap<String, byte[]> fields;
    private byte[] raw;

    public static boolean isIbeaconData(byte[] bytes){
        return bytes.length >= 29;
    }

    public static IbeaconFrame parseIbeaconData(byte[] bytes){
        IbeaconFrame frame = new IbeaconFrame();

        if( !isIbeaconData(bytes) ) return null;

        frame.raw = bytes;

        frame.fields.put("DataLength", trimByteArray(bytes, 0, 1));
        frame.fields.put("DataType", trimByteArray(bytes, 1, 1));
        frame.fields.put("LeAndBr", trimByteArray(bytes, 2, 1));
        frame.fields.put("DataLength2", trimByteArray(bytes, 3, 1));
        frame.fields.put("DataType2", trimByteArray(bytes, 4, 1));
        frame.fields.put("ManufacturerData", trimByteArray(bytes, 5, 1));
        frame.fields.put("ManufacturerData2", trimByteArray(bytes, 6, 1));
        frame.fields.put("ManufacturerData3", trimByteArray(bytes, 7, 1));
        frame.fields.put("ManufacturerData4", trimByteArray(bytes, 8, 1));
        frame.fields.put("ProximityUUID", trimByteArray(bytes, 9, 16));
        frame.fields.put("Major", trimByteArray(bytes, 25, 2));
        frame.fields.put("Minor", trimByteArray(bytes, 27, 2));
        frame.fields.put("SignalPower", trimByteArray(bytes, 29, 1));

        return frame;
    }

    public static byte[] trimByteArray(byte[] ary, int head, int len){
        byte[] trim = new byte[len];

        int cur = head;
        for(int i=0; i<len; i++){
            if( cur >= 0 && cur < ary.length ){
                trim[i] = ary[cur];
            }
            cur++;
        }

        return trim;
    }

    public static String byteToString(byte[] bytes, int[] sep) {
        StringBuffer buf = new StringBuffer();
        int si = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (sep != null && si < sep.length) {
                if (sep[si] == i) {
                    buf.append("-");
                    si++;
                }
            }
            buf.append(String.format("%02x", bytes[i] & 0xff));
        }

        return buf.toString();
    }

    public IbeaconFrame(){
        fields = new HashMap<String, byte[]>();
    }

    public String[] getKeyList(){
        return fields.keySet().toArray(new String[0]);
    }

    public byte[] getByte(String key){
        return fields.get(key);
    }

    public int getMajor(){
        byte[] bytes = fields.get("Major");
        if( bytes != null && bytes.length >= 2 ){
            return (bytes[0] * 256) + bytes[1];
        }else{
            return 0;
        }
    }

    public int getMinor(){
        byte[] bytes = fields.get("Minor");
        if( bytes != null && bytes.length >= 2 ){
            return (bytes[0] * 256) + bytes[1];
        }else{
            return 0;
        }
    }

    public byte[] getUuid(){
        return fields.get("ProximityUUID");
    }

    public String toString(){
        String[] keys = getKeyList();
        StringBuffer buf = new StringBuffer();
        buf.append("<<iBeacon Frame>>\n");
        for(int i=0; i<keys.length; i++){
            buf.append(keys[i] + ": " + byteToString(getByte(keys[i]), null));
            if( i != keys.length - 1 ) buf.append("\n");
        }
        return buf.toString();
    }
}