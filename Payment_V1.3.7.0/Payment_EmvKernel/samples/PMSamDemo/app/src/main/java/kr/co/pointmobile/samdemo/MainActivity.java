package kr.co.pointmobile.samdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.pointmobile.samdemo.utils.PmUtils;

import vpos.apipackage.APDU_RESP;
import vpos.apipackage.APDU_SEND;
import vpos.apipackage.Icc;
import vpos.apipackage.Sys;
import vpos.messenger.MessengerClient;
import vpos.util.LogUtil;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog mProgress;
    private TextView mAtrStatusTextView; //SAM Atr Result
    private TextView mApduResultTextView;//APDU Result
    private TextView mApduValueTextView; //APDU Value
    private EditText mApduValueEditText;

    private final byte ATR_RES = 0x01;  // ATR Result Text View
    private final byte APDU_IN = 0x02;   // APDU Input Data Text View
    private final byte APDU_RES = 0x04;  // APDU Result Text View

    protected MessengerClient mClient = null;

    private PmUtils pmUtils;

    private Resources res;

    private boolean isThreadFinished;

    private byte vcc_mode = 1; //5V, 2:3V, 3:1.8V
    private byte ATR[] = new byte[64 + 1]; //response buffer

    protected APDU_SEND apduSend = null;
    protected APDU_RESP apduResp = null;

    byte samSlot = (byte) 0x01;

    // extended APDU
    private final String selPSE = "00 A4 04 00 00 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31 01 00"; // extended APDU
    public byte sampleAPDU[] = {0x00,/*CLA:class*/
            (byte) 0xA4,/*INS:instruction*/
            0x04,/*P1:parameter 1*/
            0x00,/*P2:parameter 2*/
            0x00, 0x0E,/*Lc:data length*/
            (byte) 0x31, (byte) 0x50, (byte) 0x41, (byte) 0x59, (byte) 0x2E, (byte) 0x53, (byte) 0x59, (byte) 0x53,
            (byte) 0x2E, (byte) 0x44, (byte) 0x44, (byte) 0x46, (byte) 0x30, (byte) 0x31,    //'1PAY.SYS.DDF01' /*Data*/
            0x01, 0x00 /*Le:be expected response length*/};            // APDU : Select file(AID)

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init()
    {
        ActionBar aBar = getSupportActionBar();

        aBar.setIcon(R.drawable.ic_launcher);
        aBar.setDisplayUseLogoEnabled(true);
        aBar.setDisplayShowHomeEnabled(true);

        findViewById(R.id.BtnSamATR).setOnClickListener(onClickListener);
        findViewById(R.id.BtnSamAPDU).setOnClickListener(onClickListener);
        findViewById(R.id.BtnSamPowerDown).setOnClickListener(onClickListener);

        mAtrStatusTextView = findViewById(R.id.textViewSamAtrValue);
        mApduResultTextView = findViewById(R.id.textViewApduResult);
        mApduValueTextView = findViewById(R.id.textViewApduValue);
        mApduValueEditText = findViewById(R.id.edittextWriteData);

        mAtrStatusTextView.setMovementMethod(new ScrollingMovementMethod());
        mApduResultTextView.setMovementMethod(new ScrollingMovementMethod());
        mApduValueTextView.setMovementMethod(new ScrollingMovementMethod());

        RadioGridGroup colGroup = findViewById(R.id.layout_radio_btn);
        //colGroup.setOnCheckedChangeListener(mRadioCheck);
        colGroup.setCheck(R.id.rbtnSAM1);
        colGroup.setOnRadioGripGroupListener(mRadioCheck);

        res = getResources();

        pmUtils = new PmUtils();

        mProgress = new ProgressDialog(MainActivity.this);
        mClient = MessengerClient.getInstance(getApplicationContext());
        mClient.init();

        Sys.Lib_Beep();

        showProgress(MainActivity.this, true);
        AsyncMasterConnect async = new AsyncMasterConnect();
        async.execute();

    }

    private void initializeUi()
    {
        mAtrStatusTextView.setText("");
        mApduResultTextView.setText("");
        mApduValueTextView.setText("");
        mApduValueEditText.setText("");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initializeUi();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mClient.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
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

    public void viewTextClear(int clearOpt)
    {
        if ((clearOpt & 0x01) == ATR_RES)
        {
            mAtrStatusTextView.setText(null);
        }
        if ((clearOpt & 0x02) == APDU_IN)
        {
            mApduResultTextView.setText(null);
        }
        if ((clearOpt & 0x04) == APDU_RES)
        {
            mApduValueTextView.setText(null);
        }
    }

    public void btnActivation(boolean act)
    {
        findViewById(R.id.BtnSamATR).setEnabled(act);
        findViewById(R.id.BtnSamAPDU).setEnabled(act);
        findViewById(R.id.BtnSamPowerDown).setEnabled(act);
    }

//    public RadioGroup.OnCheckedChangeListener mRadioCheck = new RadioGroup.OnCheckedChangeListener()
//    {
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId)
//        {
//            if (group.getId() == R.id.layout_radio_btn)
//            {
//                viewTextClear(ATR_RES | APDU_IN | APDU_RES);
//                Icc.Lib_IccClose(samSlot);
//
//                switch (checkedId)
//                {
//                    case R.id.rbtnSAM1:
//                        samSlot = (byte) 0x01;
//                        break;
//
//                    case R.id.rbtnSAM2:
//                        samSlot = (byte) 0x02;
//                        break;
//
//                    case R.id.rbtnSAM3:
//                        samSlot = (byte) 0x03;
//                        break;
//
//                    case R.id.rbtnSAM4:
//                        samSlot = (byte) 0x04;
//                        break;
//
//                    default:
//                        break;
//                }
//            }
//        }
//    };

    public RadioGridGroup.OnRadioGripGroupChangeListener mRadioCheck = new RadioGridGroup.OnRadioGripGroupChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGridGroup group, int checkedId) {
            if (group.getId() == R.id.layout_radio_btn)
            {
                viewTextClear(ATR_RES | APDU_IN | APDU_RES);
                Icc.Lib_IccClose(samSlot);

                switch (checkedId)
                {
                    case R.id.rbtnSAM1:
                        samSlot = (byte) 0x01;
                        break;

                    case R.id.rbtnSAM2:
                        samSlot = (byte) 0x02;
                        break;

                    case R.id.rbtnSAM3:
                        samSlot = (byte) 0x03;
                        break;

                    case R.id.rbtnSAM4:
                        samSlot = (byte) 0x04;
                        break;

                    default:
                        break;
                }
            }
        }
    };

    private void startIccThread(byte slot, byte cmdType, byte[] cmdAPDU)
    {
        IccThread iccThread = new IccThread(slot, cmdType, cmdAPDU);
        iccThread.start();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.BtnSamATR:
                {
                    viewTextClear(ATR_RES | APDU_IN | APDU_RES);
                    startIccThread(samSlot, PmUtils.CMD_ATR, null);
                    break;
                }

                case R.id.BtnSamPowerDown:
                    startIccThread(samSlot, PmUtils.CMD_PWR_DOWN, null);
                    break;

                case R.id.BtnSamAPDU:
                {
                    viewTextClear(APDU_IN | APDU_RES);
                    byte[] cAPDU = new byte[260];
                    String value;

                    // 입력값이 없을때
                    if (mApduValueEditText.length() == 0)
                    {
                        value = String.format(res.getString(R.string.apdu_default_data),pmUtils.bytesToNibble(sampleAPDU, 10));
                        System.arraycopy(sampleAPDU, 0, cAPDU, 0, sampleAPDU.length);
                    }
                    else
                    {
                        value = getString(R.string.apdu_input_data);
                        value += mApduValueEditText.getText().toString();
                    }

                    mApduValueTextView.setText(value);
                    startIccThread(samSlot, PmUtils.CMD_APDU, cAPDU);
                    break;
                }

                default:
                    return;
            }
            btnActivation(false);
        }
    };

    private void setResult(final byte cmdType,final int result,final  byte[] iccResp)
    {

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                String outMsg = "";

                if(PmUtils.CMD_CHECK == cmdType)
                {
                    if (result != 0)
                    {
                        mApduResultTextView.setText(pmUtils.getIccAtrErrorDesc(result));
                        outMsg = String.format(res.getString(R.string.icc_check_fail),samSlot,result);
                    }
                }
                else if(PmUtils.CMD_ATR == cmdType)
                {
                    if (result != 0)
                    {
                        mAtrStatusTextView.setText(getString(R.string.communication_error));
                        mApduResultTextView.setText(pmUtils.getIccAtrErrorDesc(result));
                        outMsg = "";
                    }
                    else
                    {
                        byte[] atrResult = iccResp;
                        mAtrStatusTextView.setText(pmUtils.bytesToNibble(atrResult, 8));
                        outMsg = String.format(res.getString(R.string.atr_success),samSlot);
                    }
                }
                else if(PmUtils.CMD_APDU == cmdType)
                {
                    if (result == 0)
                    {
                        byte[] apduResult = iccResp;

                        APDU_RESP apduResp = new APDU_RESP(apduResult);
                        apduResult = new byte[apduResp.LenOut];

                        System.arraycopy(apduResp.DataOut, 0, apduResult, 0, apduResult.length);
                        outMsg = String.format(res.getString(R.string.apdu_result_value),pmUtils.bytesToNibble(apduResult, 11));
                    }
                    else
                    {
                        outMsg = pmUtils.getIccErrorDesc(result, getString(R.string.apdu_fail));
                    }
                }
                else if(PmUtils.CMD_PWR_DOWN == cmdType)
                {
                    if (result == 0)
                    {
                        outMsg = getString(R.string.power_down_success);
                    }
                    else
                    {
                        outMsg = getString(R.string.power_down_fail);
                    }
                }
                else
                {
                    LogUtil.d(TAG,getString(R.string.cmdtype_error));
                }

                if (outMsg.length() > 0)
                {
                    mApduResultTextView.setText(outMsg);
                }
                btnActivation(true);
            }
        });
    }

    private class IccThread extends Thread
    {
        private byte cmdType;
        private byte slot;
        private byte[] cmdAPDU;

        public IccThread(byte slot, byte cmdType, byte[] cmdAPDU)
        {
            this.slot = slot;
            this.cmdType = cmdType;
            this.cmdAPDU = cmdAPDU;
        }

        public void run()
        {
            isThreadFinished = false;

            synchronized (this)
            {
                switch (cmdType)
                {
                    case PmUtils.CMD_ATR:
                        byte[] atrResult = new byte[64 + 1];
                        runnable(cmdType, slot, null, atrResult);
                        break;

                    case PmUtils.CMD_APDU:
                        byte[] apduResult = new byte[PmUtils.MAX_BUF];
                        runnable(cmdType, slot, cmdAPDU, apduResult);
                        break;

                    case PmUtils.CMD_PWR_DOWN:
                        runnable(cmdType, slot, null, null);
                        break;

                    default:
                        break;
                }
            }
            isThreadFinished = true;
        }

        private int runnable(byte cmdType, byte slot, byte[] cAPDU, byte[] iccResp)
        {
            int ret = 0;

            if (slot == 0) /*ICC*/
            {
                ret = Icc.Lib_IccCheck(slot);
                if (ret != 0)
                {
                    return ret;
                }
                else
                {
                    LogUtil.d(TAG, getString(R.string.icc_check_success));
                }
            }

            switch (cmdType)
            {
                case PmUtils.CMD_ATR:
                    byte[] atrResult = new byte[260];
                    ret = Icc.Lib_IccOpen(slot, vcc_mode, atrResult);
                    if (ret  == 0)
                    {
                        iccResp = new byte[atrResult[0]];
                        System.arraycopy(atrResult, 1, iccResp, 0, iccResp.length);
                    }
                    else
                    {
                        iccResp = null;
                    }
                    setResult(cmdType,ret,iccResp);
                    break;

                case PmUtils.CMD_APDU:
                    ret = samProcess(slot, cAPDU, iccResp);
                    setResult(cmdType,ret,iccResp);
                    break;

                case PmUtils.CMD_PWR_DOWN:
                    ret = Icc.Lib_IccClose(slot);
                    setResult(cmdType,ret,iccResp);
                    break;

                default:
                    ret = -3;// unknown command
                    break;
            }
            return ret;
        }

        private int samProcess( byte slot, byte[] cAPDU, byte[] iccResp)
        {
            int ret;

            if (slot == 0)
            {
                ret = Icc.Lib_IccCheck(slot);
                if (ret != 0)
                {
                    setResult(cmdType,ret,iccResp);
                    return ret;
                }
            }

            ret = Icc.Lib_IccOpen(slot, vcc_mode, ATR);
            if (ret != 0)
            {
                setResult(cmdType,ret,iccResp);
                return ret;
            }

            ret = Icc.Lib_IccCommand(slot, cAPDU, iccResp);
            if (ret  == 0)
            {
                apduResp = new APDU_RESP(iccResp);
                setResult(cmdType,ret,iccResp);
                return ret;

            }

            Icc.Lib_IccClose(slot);
            pmUtils.delayMS(300);

            return ret;
        }
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

                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
            while (true);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);
            showProgress(MainActivity.this, false);
        }
    }
}