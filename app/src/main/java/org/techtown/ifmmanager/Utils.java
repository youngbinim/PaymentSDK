package org.techtown.ifmmanager;

import android.os.RemoteException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final int SDK_KCP  = 1;

    public static int SDK_VAN = SDK_KCP;

    public static final int IFM_RESP_RECEIVE_OK         = 10;
    public static final int IFM_RESP_RECEIVE_FAIL       = 11;
    public static final int IFM_RESP_RECEIVE_TIMEOUT    = 12;

    public static String getDeviceSerialNumber(){
        device.sdk.Information deviceInfo = new device.sdk.Information();
        String serialNum = null;

        try{
            serialNum = deviceInfo.getSerialNumber();
        }catch (RemoteException e)
        {
            e.printStackTrace();
        }

        return serialNum;
    }

    public static String convertCurrentDateToString() {

        long    now = System.currentTimeMillis();
        Date    date = new Date(now);

        SimpleDateFormat    sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String              strDate = sdf.format(date);

        return strDate;
    }

    public static String getCurrentSystemTimeToString() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        long    crntTime = System.currentTimeMillis();
        Date date = new Date(crntTime);
        String  strDate = sdf.format(date);

        return strDate;
    }
}
