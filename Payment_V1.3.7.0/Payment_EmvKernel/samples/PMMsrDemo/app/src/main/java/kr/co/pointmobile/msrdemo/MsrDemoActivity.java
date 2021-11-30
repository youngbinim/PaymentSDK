package kr.co.pointmobile.msrdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import kr.co.pointmobile.msrdemo.utils.PmUtils;
import vpos.apipackage.Icc;
import vpos.apipackage.Mcr;
import vpos.messenger.MessengerClient;

import static vpos.apipackage.Icc.Lib_IccClose;
import static vpos.apipackage.Mcr.Lib_McrClose;
import static vpos.apipackage.Mcr.Lib_McrOpen;
import static vpos.apipackage.Mcr.Lib_McrRead;
import static vpos.emvkernel.EmvKernel.EmvLib_GetTLV;

public class MsrDemoActivity extends AppCompatActivity
{

    private static final String TAG = MsrDemoActivity.class.getSimpleName();
    private static final int SUCCESS = 0;

    private Button mStartReadButton = null;
    private TextView mResultTextView = null;
    private TextView mStatusTextView = null;
    private TextView mTrack1View = null;
    private TextView mTrack2View = null;
    private TextView mTrack3View = null;
    private CheckBox mAutoScanModeCheck = null;
    private CheckBox mBeepSoundCheck = null;

    private int mSuccessCount = 0;
    private int mPartCount = 0;
    private int mFailCount = 0;
    private int mTrack1Count = 0;
    private int mTrack2Count = 0;
    private int mTrack3Count = 0;
    private int mTotalCount = 0;

    boolean isQuit = true;
    int ret = -1;
    int checkCount = 0;
    private boolean mIsTriggered = false;

    private MediaPlayer mSuccessBeep = null;
    private MediaPlayer mFailBeep = null;

    private MessengerClient mClient = null;

    private ProgressDialog mProgress;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClient = MessengerClient.getInstance(getApplicationContext());
        mClient.init();

        mProgress = new ProgressDialog(MsrDemoActivity.this);

        ActionBar aBar = getSupportActionBar();
        aBar.setIcon(R.drawable.ic_launcher);
        aBar.setDisplayUseLogoEnabled(true);
        aBar.setDisplayShowHomeEnabled(true);

        res = getResources();

        initActivity();
        clearResult();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if(mIsTriggered)
        {
            onClickStartReading();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    protected void onDestroy()
    {
        Lib_McrClose();
        isQuit = true;
        Log.d(TAG, "Lib_McrClosed");
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        //Lib_McrClose();
        Log.d(TAG, "onBackPressed");
        super.onBackPressed();
    }

    private void initActivity()
    {
        mResultTextView = this.findViewById(R.id.textViewMsrResult);
        mStatusTextView = this.findViewById(R.id.textViewMsrStatus);
        mTrack1View = this.findViewById(R.id.textViewMsrTrack1);
        mTrack2View = this.findViewById(R.id.textViewMsrTrack2);
        mTrack3View = this.findViewById(R.id.textViewMsrTrack3);

        mAutoScanModeCheck = findViewById(R.id.check_auto_scan);
        mAutoScanModeCheck.setChecked(false);
        mBeepSoundCheck = findViewById(R.id.check_beep_sound);
        mBeepSoundCheck.setChecked(true);

        mStartReadButton = findViewById(R.id.button_card_reader);
        mStartReadButton.setOnClickListener(mOnClickListener);
        findViewById(R.id.button_count_clear).setOnClickListener(mOnClickListener);

        clearTextView();

        showProgress(MsrDemoActivity.this, true);
        AsyncMasterConnect asyncMaster = new AsyncMasterConnect();
        asyncMaster.execute();

        showProgress(MsrDemoActivity.this, true);
        AsyncInit async = new AsyncInit();
        async.execute();

    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v.getId() == R.id.button_card_reader)
            {
                onClickStartReading();
            }
            else if (v.getId() == R.id.button_count_clear)
            {
                onClickClearData();
            }
        }
    };

    private void initIccrDemo()
    {
        IccrDemo.initIccrDemo(getApplicationContext(), getFilesDir().getPath());
    }

    private void setBtnActive(final boolean isActive)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mStartReadButton == null)
                {
                    return;
                }

                if (isActive)
                {
                    mStartReadButton.setEnabled(true);
                }
                else
                {
                    mStartReadButton.setEnabled(false);
                }
            }
        });
    }

    public void clearResult()
    {
        mSuccessCount = 0;
        mPartCount = 0;
        mFailCount = 0;
        mTrack1Count = 0;
        mTrack2Count = 0;
        mTrack3Count = 0;
        mTotalCount = 0;
    }

    public void onClickClearData()
    {
        showClearDataDialog();
    }

    public void onClickStartReading()
    {
        mIsTriggered = !mIsTriggered;

        if (mIsTriggered)
        {
            clearTextView();

            // change the button face...
            mStartReadButton.setText(getString(R.string.btn_stop_reading));
            mStatusTextView.setText(getString(R.string.read_status));
            mTrack1View.setText("");
            mTrack2View.setText("");
            mTrack3View.setText("");

            if (!isQuit)
            {
                return;
            }

            msrThread = new MSRThread();
            msrThread.start();
        }
        else
        {
            // change the button face...
            mStartReadButton.setText(getString(R.string.btn_start_reading));
            mStatusTextView.setText(getString(R.string.stop_status));
            isQuit = true;
        }
    }

    private void initMediaPlayer()
    {
        if (mSuccessBeep == null)
        {
            mSuccessBeep = MediaPlayer.create(this.getApplicationContext(), R.raw.success_beep);
        }

        if (mFailBeep == null)
        {
            mFailBeep = MediaPlayer.create(this.getApplicationContext(), R.raw.fail_beep);
        }
    }

    public void playBeep(boolean result)
    {
        initMediaPlayer();

        try
        {
            MediaPlayer player = result ? mSuccessBeep : mFailBeep;
            player.seekTo(0);
            player.start();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    MSRThread msrThread = null;

    public class MSRThread extends Thread
    {

        private byte[] track1 = new byte[250];
        private byte[] track2 = new byte[250];
        private byte[] track3 = new byte[250];

        public void run()
        {

            synchronized (this)
            {
                Message msg_h = new Message();
                Bundle b_h = new Bundle();
                String toastMsg = "";

                // always mode 1
                ret = Mcr.Lib_McrOpen(1);

//                if(PmUtils.isPCIMode() == false)
//                {
//                    ret = Lib_McrOpen(0);
//                }
//                else
//                {
//                    byte[] version = new byte[3];
//                    if(Mcr.Lib_McrFWVersion(version) == 0)
//                    {
//                        if((version[0] == 0x32) && (version[1] >= 0x40))// new MCR.
//                        {
//                            if(PmUtils.isPCIMode() == false) // P/N[13] is '6'(KOR SKU).
//                            {
//                                ret = Mcr.Lib_McrOpen(0);
//                            }
//                            else
//                            {
//                                ret = Mcr.Lib_McrOpen(1);
//                            }
//                        }
//                        else  // the first byte isn't 0x32(or the second byte is smaller than 0x40. so, it is old MCR.
//                        {
//                            ret = Mcr.Lib_McrOpen(1);
//                        }
//                    }
//                    else // Lib_McrFWVersion() return failed. so, it is old MCR.
//                    {
//                        ret = Lib_McrOpen(1);
//                    }
//                }

                if (0 != ret)
                {
                    toastMsg = getString(R.string.msr_open_error);
                    isQuit = true;
                }
                else
                {
                    toastMsg = getString(R.string.swipe_ms_card);
                }

                showToast(toastMsg);

                isQuit = false;

                while (!isQuit)
                {

                    if (Mcr.Lib_McrCheck() == SUCCESS)
                    {
                        readMSC();
                        continue;
                    }

                    while (Mcr.Lib_McrCheck() != SUCCESS && !isQuit)
                    {
                        try
                        {
                            Thread.sleep(200);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        if (Icc.Lib_IccCheck((byte) 0) == SUCCESS)
                        {
                            setBtnActive(false);
                            readICC();

                            if (mAutoScanModeCheck.isChecked())
                            {
                                while (Icc.Lib_IccCheck((byte) 0) == SUCCESS)
                                {
                                    try
                                    {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            setBtnActive(true);
                        }
                    }
                }
                isQuit = true;
            }
        }

        private void clearTrackBuffer()
        {
            Arrays.fill(track1, (byte) 0x00);
            Arrays.fill(track2, (byte) 0x00);
            Arrays.fill(track3, (byte) 0x00);
        }

        private void readMSC()
        {
            checkCount++;
            clearTrackBuffer();
            ret = Lib_McrRead(track1, track2, track3);
            resultDisplay(ret, track1, track2, track3);
            //Lib_McrClose();
        }

        private void readICC()
        {
            checkCount++;
            clearTrackBuffer();
            byte[] val = new byte[256];
            int[] len = new int[1];
            int ret = 0;

            IccrDemo.startEMV();
            if ((EmvLib_GetTLV("57", track2, len)) == 0)
            {
                ret = 0x02;
                val = new byte[len[0]];
                System.arraycopy(track2, 0, val, 0, len[0]);

                track2 = PmUtils.hexToNibbleBytes(val);
            }
            else
            {
                ret = 0;
            }

            resultDisplay(ret, track1, track2, track3);
            Lib_IccClose((byte) 0);
        }
    }

    private void showToast(final String msg)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resultDisplay(final int result, final byte[] track1, final byte[] track2, final byte[] track3)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mTotalCount++;

                String str;

                if (result == SUCCESS)
                {
                    mFailCount++;
                    if (mBeepSoundCheck.isChecked())
                    {
                        playBeep(false);
                    }
                    clearTextView();

                    str = getString(R.string.read_fail_status);
                    if (mAutoScanModeCheck.isChecked())
                    {
                        str += getString(R.string.repeating_status);
                    }
                    mStatusTextView.setText(str);
                }
                else
                {
                    if ((result & 0x01) == 0x01)
                    {
                        mTrack1Count++;
                    }
                    if ((result & 0x02) == 0x02)
                    {
                        mTrack2Count++;
                    }
                    if ((result & 0x04) == 0x04)
                    {
                        mTrack3Count++;
                    }

                    if ((result & 0x07) == 0x07)
                    {
                        mSuccessCount++;
                    }
                    else
                    {
                        mPartCount++;
                    }

                    str = String.format(res.getString(R.string.result_read), (errorMsg(result)))
                            + getString(R.string.result_track1) + (((result & 0x01) == 0) ? getString(R.string.result_fail) : getString(R.string.result_success))
                            + getString(R.string.result_track2) + (((result & 0x02) == 0) ? getString(R.string.result_fail) : getString(R.string.result_success))
                            + getString(R.string.result_track3) + (((result & 0x04) == 0) ? getString(R.string.result_fail) : getString(R.string.result_success));

                    if (mAutoScanModeCheck.isChecked())
                    {
                        str += getString(R.string.repeating_status);
                    }

                    mStatusTextView.setText(str);
                    mTrack1View.setText(new String(track1).trim());
                    mTrack2View.setText(new String(track2).trim());
                    mTrack3View.setText(new String(track3).trim());

                    if (mBeepSoundCheck.isChecked())
                    {
                        if (((result & 0x01) == 0x01) && ((result & 0x02) == 0x02) && ((result & 0x04) == 0x04))
                        {
                            playBeep(true);
                        }
                        else
                        {
                            playBeep(false);
                        }
                    }
                }

                if (!mAutoScanModeCheck.isChecked())
                {
                    isQuit = true;
                    mStartReadButton.setEnabled(true);
                    mStartReadButton.setText(getString(R.string.btn_start_reading));
                    mIsTriggered = !mIsTriggered;
                }

                String result = String.format(res.getString(R.string.result_count),
                        mSuccessCount, mPartCount, mFailCount, mTotalCount, mTrack1Count, mTrack2Count, mTrack3Count);

                mResultTextView.setText(result);

            }
        });
    }

    private void clearTextView()
    {
        mStatusTextView.setText("\n\n\n");
        mResultTextView.setText("\n\n\n\n");
        mTrack1View.setText("\n\n");
        mTrack2View.setText("\n");
        mTrack3View.setText("\n\n\n");
    }

    public String errorMsg(int status)
    {
        String msg = new String();

        status &= 0x07;

        if (status > 0x00)
        {
            msg = getString(R.string.track_success);

            if (status == 0x01)
            {
                msg += getString(R.string.track2_track3_fail);
            }
            else if (status == 0x02)
            {
                msg += getString(R.string.track1_track3_fail);
            }
            else if (status == 0x03)
            {
                msg += getString(R.string.track3_fail);
            }
            else if (status == 0x04)
            {
                msg += getString(R.string.track1_track2_fail);
            }
            else if (status == 0x05)
            {
                msg += getString(R.string.track1_track2_fail);
            }
            else if (status == 0x06)
            {
                msg += getString(R.string.track1_fail);
            }
            else if (status == 0x07)
            {
                msg += getString(R.string.all_track_success);
            }
        }
        else
        {
            msg = getString(R.string.track_read_fail);
        }

        return msg;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_info:
                openInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openInfo()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        String version = getString(R.string.msg_version_suffix);
        try
        {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (pi != null)
            {
                version = pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        alert.setMessage(getString(R.string.app_name) + " v" + version);
        alert.show();
    }

    public void showClearDataDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.clear_data_dialog_title));
        builder.setMessage(getString(R.string.clear_data_dialog_msg));

        builder.setPositiveButton(getString(R.string.clear_data_dialog_positive), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                clearResult();
                clearTextView();
                Toast.makeText(getApplicationContext(), getString(R.string.data_clear_success_msg), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(getString(R.string.clear_data_dialog_negative), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getApplicationContext(), getString(R.string.data_clear_cancel_msg), Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void showProgress(final Activity act, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgress.setMessage(getString(R.string.initializing));
                mProgress.setCancelable(false);

                try
                {
                    if (bShow)
                    {
                        mProgress.show();
                    }
                    else
                    {
                        mProgress.dismiss();
                    }
                } catch (Exception e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        });
    }

    class AsyncMasterConnect extends AsyncTask<Void, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            do
            {
                if (mClient.isConnect())
                    break;
                try
                {
                    Thread.sleep(1000);
                } catch (Exception e)
                {

                }
            }
            while (true);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            showProgress(MsrDemoActivity.this, false);
        }
    }

    class AsyncInit extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            initIccrDemo();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            showProgress(MsrDemoActivity.this, false);
            super.onPostExecute(aVoid);
        }
    }
}
