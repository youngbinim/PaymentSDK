package org.techtown.ifmmanager;

import android.content.Context;
import android.os.Handler;

import device.sdk.MsrManager;

// on / off // read / write
public class RdiManager extends MsrManager {

    private final String TAG = RdiManager.class.getSimpleName();

    private static Context mContext;
    public static int ENABLE = 1;
    public static int DISABLE = 0;

    public boolean  mIsRdiOpened = false;
    private boolean mfRcvThread = false;
    private boolean threadCheckFlag = true;
    private Handler mIfmManagerHandler = null;  //read thread callback



    public RdiManager(Context context){
        mContext = context;
    }

    public int setEnable(int enable){
        return rdiSetEnable(enable);
    }

    public int isEnabled(){
        return rdiIsEnabled();
    }

    public void powerDown(){
        threadCheckFlag = false;

        if(rdiClose() == 0)
            mIsRdiOpened = false;
    }

    public boolean powerOn(){
        int opend = -1;

        try{
            opend = rdiOpen();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(opend == 0){
            mIsRdiOpened = true;
        }else if(opend == -1){
            mIsRdiOpened = false;
        }

        return mIsRdiOpened;
    }

    public int clear() {

        return rdiClear();
    }

    public byte[]   backupCmd;

    public int write(byte[] data, int length) {

        byte[]      padingData = setPaddingData(data);

        // firmware reboot에 대비해 backup을 해둔다.
        backupCmd = new byte[padingData.length];
        System.arraycopy(padingData, 0, backupCmd, 0, backupCmd.length);

        mfRcvThread = false;
        return rdiWrite(padingData, padingData.length);
    }

    public byte[] setPaddingData(byte[] data)
    {
        int     length = data.length;

        if ((length % 8) == 7) {
            length++;
        }

        length += (8 - (length % 8)) + 11;

        byte[]      returnData = new byte[length];

        for (int i = 0; i < length; i++) {
            if (i >= data.length)
                returnData[i] = (byte) 0xFF;
            else
                returnData[i] = data[i];
        }

        return returnData;
    }
    public void readData(Handler msrHandler, int timeout)
    {
        mIfmManagerHandler = msrHandler;

        new Thread(new ReadThread(timeout)).start();

        return;
    }

    private class ReadThread implements Runnable{

        int mTimeout = 0;

        ReadThread(int timeout){
            mfRcvThread = true;
            mTimeout = timeout;
        }
        @Override
        public void run() {

        }
    }
}
