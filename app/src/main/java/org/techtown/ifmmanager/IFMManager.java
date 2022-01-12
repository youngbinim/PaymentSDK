package org.techtown.ifmmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.LinkedHashMap;

public class IFMManager implements IFMControllerInterface.OnIFMControllerInterface {

    private final String TAG = IFMManager.class.getSimpleName();

    private Context mContext = null;
    public static RdiManager mRdiManager;
    private boolean     m_fRdiOn=false;
    LinkedHashMap<String, String> mInputMap = null;

    private boolean onRdi(){
        if(mRdiManager == null)
            mRdiManager = new RdiManager(mContext);

        if(mRdiManager != null){
            mRdiManager.rdiSetEnable(mRdiManager.DISABLE);
            mRdiManager.powerDown();
            mRdiManager.powerOn();
            m_fRdiOn = true;
        }

        return mRdiManager.mIsRdiOpened;
    }

    private void offRdi(){
        if(mRdiManager != null){
            if(mRdiManager.isEnabled() == mRdiManager.ENABLE){
                mRdiManager.setEnable(mRdiManager.DISABLE);
            }

            mRdiManager.powerDown();
        }

        return;
    }

    private void setupRdi() {

        onRdi();

        if (mRdiManager != null) {
            if (mRdiManager.isEnabled() != mRdiManager.ENABLE) {
                mRdiManager.setEnable(mRdiManager.ENABLE);
            }
        }

        return;
    }

    @Override
    public void PowerOnRdi() {
        onRdi();
    }

    public void PowerOffRdi(){
        offRdi();
    }

    public void reqIFM(LinkedHashMap<String , String> inmap, byte trdType){

        byte[] rdPktBuf = null;
        int     rcvTimeout = 3000;
        
        switch(Utils.SDK_VAN){
            case Utils.SDK_KCP:
                IfmMakePkt ifmMakePkt = new IfmMakePkt(mContext);
                rdPktBuf = ifmMakePkt.makeICReaderPacket(inmap,trdType);
                rcvTimeout = 7000;
                mInputMap = inmap;
                break;
        }

        sendDataToMSR(rdPktBuf,rdPktBuf.length,rcvTimeout);
    }

    private void sendDataToMSR(byte[] rdPkt, int size, int timeout) {

        setupRdi();
        mRdiManager.clear();

        int     result = mRdiManager.write(rdPkt, size);

        if (mRdiManager != null && mRdiManager.isEnabled() == 1) {
            mRdiManager.readData(ifmManagerHandler, timeout);
        }

        return;
    }

    @SuppressLint("HandlerLeak")
    private Handler ifmManagerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Utils.IFM_RESP_RECEIVE_OK:

                    byte[]      rcvIfmPktBuf = (byte[]) msg.obj;        // heap memory 의 address 를 mapping 한다.

                    if (rcvIfmPktBuf == null) {
                        sendCallback(Utils.IFM_RESP_RECEIVE_FAIL, null);
                        break;
                    }
                    sendCallback(Utils.IFM_RESP_RECEIVE_OK, rcvIfmPktBuf);
                    break;

                case Utils.IFM_RESP_RECEIVE_FAIL:

                    sendCallback(Utils.IFM_RESP_RECEIVE_FAIL, null);
                    break;

                case Utils.IFM_RESP_RECEIVE_TIMEOUT:

                    sendCallback(Utils.IFM_RESP_RECEIVE_TIMEOUT, null);
                    break;
            }
        }
    };
}
