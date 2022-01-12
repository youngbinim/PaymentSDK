package org.techtown.ifmmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import device.sdk.MsrManager;

public class FWDown extends AsyncTask<Void, Void, Void> {

    private MsrManager mMsrManager;
    private File mUpdateFile = null;
    private Context mContext = null;
    private Handler mResultHandler;
    private boolean mResult = false;
    private ProgressDialog mProgressDialog = null;

    private PowerManager mPm = null;
    private PowerManager.WakeLock mWakeLock;

    public FWDown(Context context, File file, Handler resultHandler){
        mContext = context;
        mMsrManager = new MsrManager();
        mUpdateFile = file;
        mResultHandler = resultHandler;

        mPm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ":UpdateFirmware");
        mWakeLock.setReferenceCounted(false);
    }

    public void deviceMsrClose(){
        if(mMsrManager != null){
            mMsrManager.DeviceMsrClose();
            mMsrManager = null;
        }
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mContext, null, "Please Wait...", true, false);
        mWakeLock.acquire();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if(mMsrManager != null){

            mMsrManager.DeviceMsrOpen(null);

            try {
                mResult = mMsrManager.updateFirmware(mUpdateFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        mWakeLock.release();

        Toast.makeText(mContext,mResult ? "Success to Update Firmware." : "Faile to Update Firmware.", Toast.LENGTH_SHORT).show();
        sendFWUpdateCompleteMessage();

        mMsrManager.DeviceMsrClose();
    }

    private void sendFWUpdateCompleteMessage(){
        if(mResultHandler == null)
        {
            Toast.makeText(mContext, "result handler is null !", Toast.LENGTH_SHORT).show();
            return ;
        }

        Message msg = mResultHandler.obtainMessage();
        msg.what = mResult ? 1 : 0;
        mResultHandler.sendMessage(msg);
        mResultHandler = null;
        return ;
    }
}
