package kr.co.pointmobile.nfcdemo.fragments;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import kr.co.pointmobile.nfcdemo.R;

import java.util.Arrays;

import vpos.apipackage.APDU_RESP;
import vpos.apipackage.APDU_SEND;
import vpos.apipackage.Picc;
import vpos.util.ByteUtil;


public class TagWriteFragment extends Fragment
{
    private ToneGenerator mTonegenerator;
    private EditText mEditWrite;
    private TextView mTextWriteState;
    private Button mBtnWriteStart;
    private Button mBtnWriteStop;

    //	public byte picc_mode;
    public byte picc_type = 'a';
    public byte blkNo = 60;
    public byte updateblkNo = 60;
    public byte cardtype[] = new byte[3];
    public byte pwd[] = new byte[20];
    public byte serialNo[] = new byte[100];
    public byte ats[] = new byte[300];
    public byte blkValue[] = new byte[20];
    public byte Value[] = new byte[20];
    public int i, leng;
    public byte piccApduSend[] = new byte[530];
    public byte piccApduResp[] = new byte[530];
    public byte dataIn[] = new byte[530];
    public byte dataOut[] = new byte[256];
    public byte outLen[] = new byte[2];

    private boolean m_bThreadFinished = true;
    private boolean isOut = false;

    public TagWriteFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mTonegenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 500);
        View view = inflater.inflate(R.layout.fragment_tag_write, container, false);

        mEditWrite = (EditText)view.findViewById(R.id.edit_writeText);
        mBtnWriteStart = (Button)view.findViewById(R.id.btn_write_start);
        mBtnWriteStart.setOnClickListener(mOnClickListener);
        mBtnWriteStop = (Button)view.findViewById(R.id.btn_write_stop);
        mBtnWriteStop.setOnClickListener(mOnClickListener);
        mTextWriteState = (TextView)view.findViewById(R.id.text_write_state);

        return view;
    }

    @Override
    public void onPause()
    {
        isOut = true;
        super.onPause();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(v.getId() == R.id.btn_write_start)
            {
                if(mEditWrite.getText().length() < 1)
                {
                    return;
                }

                mTextWriteState.setText(R.string.nfc_read_start);
                isOut = false;

                mBtnWriteStart.setEnabled(false);
                AsyncPiccWrite async = new AsyncPiccWrite();
                async.execute();
            }
            else if(v.getId() == R.id.btn_write_stop)
            {
                isOut = true;
                mBtnWriteStart.setEnabled(true);
                mTextWriteState.setText(R.string.nfc_read_stop);
            }
        }
    };


    private void updateResult(final String result)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mTextWriteState.setText(result);
            }
        });
    }


    class AsyncPiccWrite extends AsyncTask<Void, Void, Boolean>
    {
        public void runable(int m_iTimeOut)
        {
            String msgS = "";
            int cardTypeFlag = 0;
            byte[] writeByte = mEditWrite.getText().toString().getBytes();
            int length = writeByte.length;

            Arrays.fill(cardtype, (byte)0);
            Arrays.fill(serialNo, (byte)0);
            Arrays.fill(ats, (byte)0);
            Arrays.fill(dataIn, (byte)0);
            Arrays.fill(dataOut, (byte)0);
            Arrays.fill(outLen, (byte)0);

            int ret = Picc.Lib_PiccOpen();
            if (0 != ret) {
                updateResult("Picc_Open error!");
                return ;
            }
            Log.d("runable[ run ]", "m_iTimeOut = " + m_iTimeOut);

            boolean bPICCCheck = false;
            while (m_iTimeOut > 0 && isOut == false) {
                m_iTimeOut--;
                updateResult("wait time " + m_iTimeOut);
                Log.d("runable runable[ run ]", "wait time " + m_iTimeOut);

                ret = Picc.Lib_PiccCheck((byte)0, cardtype, serialNo, ats);
                if (0 == ret) {
                    Log.e("PICC_Thread[ run ]", "Picc_Check succeed!");
                    bPICCCheck = true;
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            cardTypeFlag = 0;
            if(cardtype[0] == 'A' && cardtype[1] == 'C')	//A?
                cardTypeFlag = 1;
            else if(cardtype[0] == 'B' && cardtype[1] == 'C') //B?
                cardTypeFlag = 2;
            else if(cardtype[0] == 'M' && cardtype[1] == 'C') //M?
                cardTypeFlag = 3;
            else if(cardtype[0] == 'A' && cardtype[1] == '1') //T1T?
                cardTypeFlag = 4;
            else if(cardtype[0] == 'V' && cardtype[1] == 'C') //15693?
                cardTypeFlag = 5;
            else if(cardtype[0] == 'F') //Felica?
                cardTypeFlag = 6;
            else if(cardtype[0] == 'A' && cardtype[1] == '2') //T2T?
                cardTypeFlag = 7;

            Log.e("TAG", "cardTypeFlag = " + cardTypeFlag);

            if (bPICCCheck)
            {
                if (cardTypeFlag == 3)
                {
                    pwd[0] = (byte) 0xff;
                    pwd[1] = (byte) 0xff;
                    pwd[2] = (byte) 0xff;
                    pwd[3] = (byte) 0xff;
                    pwd[4] = (byte) 0xff;
                    pwd[5] = (byte) 0xff;
                    pwd[6] = (byte) 0x00;

                    picc_type = 'A';
                    ret = Picc.Lib_PiccM1Authority(picc_type, blkNo, pwd, serialNo);
                    if (0 == ret)
                    {
                        blkValue[0] = (byte) 0x22;
                        blkValue[1] = (byte) 0x00;
                        blkValue[2] = (byte) 0x00;
                        blkValue[3] = (byte) 0x00;
                        blkValue[4] = (byte) 0xbb;
                        blkValue[5] = (byte) 0xff;
                        blkValue[6] = (byte) 0xff;
                        blkValue[7] = (byte) 0xff;
                        blkValue[8] = (byte) 0x44;
                        blkValue[9] = (byte) 0x00;
                        blkValue[10] = (byte) 0x00;
                        blkValue[11] = (byte) 0x00;
                        blkValue[12] = (byte) blkNo;
                        blkValue[13] = (byte) ~blkNo;
                        blkValue[14] = (byte) blkNo;
                        blkValue[15] = (byte) ~blkNo;
                        ret = Picc.Lib_PiccM1WriteBlock(blkNo, blkValue);
                        if (0 == ret)
                        {
                            updateResult("Picc_M1WriteBlock() succeed!");
                        }
                        else
                        {
                            updateResult("Picc_M1WriteBlock() fail!");
                        }
                    }
                    else
                    {
                        updateResult("Picc_M1Authority() fail!");
                    }
                }
                else if (cardTypeFlag == 1 || cardTypeFlag == 2)
                {
                    byte cmd[] = new byte[4];

                    cmd[0] = 0x00;            //0-3 cmd
                    cmd[1] = (byte) 0xa4;
                    cmd[2] = 0x04;
                    cmd[3] = 0x00;
                    short lc = 0x0e;
                    short le = 256;
                    byte[] data = "1PAY.SYS.DDF01".getBytes();

                    APDU_SEND ApduSend = new APDU_SEND(cmd, lc, data, le);
                    APDU_RESP ApduResp = null;
                    byte[] resp = new byte[516];
                    ret = Picc.Lib_PiccCommand(ApduSend.getBytes(), resp);
                    if (0 == ret)
                    {
                        String strInfo = "";
                        ApduResp = new APDU_RESP(resp);
                        strInfo = ByteUtil.bytearrayToHexString(ApduResp.DataOut, ApduResp.LenOut) + "\nSWA:" + ByteUtil.byteToHexString(ApduResp.SWA) + " SWB:" + ByteUtil.byteToHexString(ApduResp.SWB);
                        //updateResult("\n" + strInfo, 1);
                        updateResult("Picc_Command success!");
                    }
                    else
                    {
                        updateResult("Picc_Command failed!");
                    }
                }
                else if (cardTypeFlag == 4)
                {
                    System.arraycopy(writeByte, 0, dataIn, 0, length);
                    Arrays.fill(dataOut, (byte) 0);
                    Arrays.fill(outLen, (byte) 0);
                    ret = Picc.Lib_PiccWriteT1T((byte) 3, (byte) length, dataIn, outLen, dataOut);
                    if (ret == 0)
                    {
                        updateResult("T1T Write data succeed!");
                    }
                    else
                    {
                        updateResult("T1T Write data failed!");
                    }
                }
                else if (cardTypeFlag == 5)
                {
                    System.arraycopy(writeByte, 0, dataIn, 0, length);
                    Arrays.fill(dataOut, (byte) 0);
                    Arrays.fill(outLen, (byte) 0);
                    ret = Picc.Lib_PiccWrite15693((byte) 0, (byte) length, dataIn);
                    if (ret == 0)
                    {
                        updateResult("15693 Write data succeed!");
                    }
                    else
                    {
                        updateResult("15693 Write data failed!");
                    }
                }
                else if (cardTypeFlag == 6)
                {
                    System.arraycopy(writeByte, 0, dataIn, 0, length);
                    Arrays.fill(dataOut, (byte) 0);
                    Arrays.fill(outLen, (byte) 0);
                    ret = Picc.Lib_PiccWriteFelica((byte) length, dataIn);
                    if (ret == 0)
                    {
                        updateResult("Felica Write data succeed!");
                    }
                    else
                    {
                        updateResult("Felica Write data failed!");
                    }
                }
                else if (cardTypeFlag == 7)
                {
                    System.arraycopy(writeByte, 0, dataIn, 0, length);
                    Arrays.fill(dataOut, (byte) 0);
                    Arrays.fill(outLen, (byte) 0);
                    ret = Picc.Lib_PiccWriteT2T((byte) length, dataIn);
                    if (ret == 0)
                    {
                        updateResult("T2T Write data succeed!");
                    }
                    else
                    {
                        updateResult("T2T Write data failed!");
                    }
                }
                mTonegenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 1000);
            }
            else
            {
                updateResult("Time Out!");
            }
            Picc.Lib_PiccClose();
            Log.e("PICC_Thread11[ run ]", "Picc.Lib_PiccClose()!");
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            m_bThreadFinished = false;
            synchronized (this) {
                runable(10);
            }

            Log.d("m_bThreadFinished", "m_bThreadFinished = true");
            m_bThreadFinished = true;

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            mBtnWriteStart.setEnabled(true);
            super.onPostExecute(aBoolean);
        }
    }
}
