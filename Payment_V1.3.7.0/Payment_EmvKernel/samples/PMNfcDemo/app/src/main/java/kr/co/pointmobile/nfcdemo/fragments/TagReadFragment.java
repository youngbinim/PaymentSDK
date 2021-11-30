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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import kr.co.pointmobile.nfcdemo.R;

import java.util.Arrays;

import vpos.apipackage.APDU_RESP;
import vpos.apipackage.APDU_SEND;
import vpos.apipackage.Picc;
import vpos.util.ByteUtil;

public class TagReadFragment extends Fragment
{
    private ToneGenerator mTonegenerator;
    private TextView mTextState;
    private TextView mTextResult;
    private Button mBtnStart;
    private Button mBtnStop;

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

    public TagReadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mTonegenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 500);

        View view = inflater.inflate(R.layout.fragment_tag_read, container, false);
        mTextState = view.findViewById(R.id.text_state);
        mTextResult = view.findViewById(R.id.text_picc_value);

        mBtnStart = view.findViewById(R.id.btn_read_start);
        mBtnStart.setOnClickListener(mOnClickListener);
        mBtnStop = view.findViewById(R.id.btn_read_stop);
        mBtnStop.setOnClickListener(mOnClickListener);

        // Inflate the layout for this fragment
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
            if(v.getId() == R.id.btn_read_start)
            {
                mTextState.setText(R.string.nfc_read_start);
                isOut = false;

                mBtnStart.setEnabled(false);
                AsyncPicc async = new AsyncPicc();
                async.execute();

            }
            else if(v.getId() == R.id.btn_read_stop)
            {
                isOut = true;
                mBtnStart.setEnabled(true);
                mTextState.setText(R.string.nfc_read_stop);
            }
        }
    };

    private void updateResult(final String result)
    {
        updateResult(result, 0);
    }

    private void updateResult(final String result, final int isAppend)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(isAppend == 1)
                {
                    mTextResult.append(result);
                }
                else
                {
                    mTextResult.setText(result);
                }
            }
        });
    }

    class AsyncPicc extends AsyncTask<Void, Void, Boolean>
    {
        public void runable(int m_iTimeOut)
        {
            String msgS;
            int length;
            int cardTypeFlag = 0;

            Arrays.fill(cardtype, (byte)0);
            Arrays.fill(serialNo, (byte)0);
            Arrays.fill(ats, (byte)0);
            Arrays.fill(dataIn, (byte)0);
            Arrays.fill(dataOut, (byte)0);
            Arrays.fill(outLen, (byte)0);

            int ret = Picc.Lib_PiccOpen();
            if (0 != ret) {
                updateResult("Picc_Open error!");
                Log.e("PICC_Thread[ run ]", "Picc_Open error!");
                return;
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
            updateResult("CardType:" + new String(cardtype).trim());
            cardTypeFlag = 0;
            if(serialNo[0] > 0) {
                updateResult("\nUID:" + ByteUtil.bytearrayToHexString(serialNo, serialNo[0]+1), 1);
            }
            if(ats[0] > 0) {
                updateResult("\nATS:" + ByteUtil.bytearrayToHexString(ats, ats[0]+1), 1);
            }
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
                        updateResult("Picc_M1Authority() succeed!",1);

                        ret = Picc.Lib_PiccM1ReadBlock(blkNo, blkValue);
                        if (0 == ret)
                        {
                            msgS = "\nPiccM1ReadBlock() succeed!\nValue : " + ByteUtil.bytearrayToHexString(blkValue, 16);
                            updateResult(msgS, 1);
                        }
                        else
                        {
                            msgS = "\nPicc_M1ReadBlock() fail! return " + ret;
                            updateResult(msgS, 1);
                        }
                    }
                    else
                    {
                        updateResult("\nPicc_M1Authority() fail! return " + ret, 1);
                    }
                } else if(cardTypeFlag == 1 || cardTypeFlag == 2){
                    byte cmd[] = new byte[4];

                    cmd[0] = 0x00;			//0-3 cmd
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
                        updateResult("\n" + strInfo,1);
                    }
                    else
                    {
                        updateResult("\nPicc_Command failed! return " + ret, 1);
                        Log.e("PICC_Thread[ run ]", "Picc_Command failed! return " + ret);
                    }
                }
                else if(cardTypeFlag == 4)
                {
                    ret = Picc.Lib_PiccReadT1T((byte)3, outLen, dataOut);
                    if(ret == 0)
                    {
                        //length = outLen[0];
                        updateResult("\nT1T read data:" + ByteUtil.bytesToString(dataOut), 1);
                    }
                    else
                    {
                        updateResult("\nT1T read data failed, ret : " + ret, 1);
                    }

                }
                else if (cardTypeFlag == 5)
                {
                    ret = Picc.Lib_PiccRead15693((byte) 0, outLen, dataOut);
                    if (ret == 0)
                    {
                        //length = outLen[0];
                        //updateResult("\n15693 read data:" + ByteUtil.bytearrayToHexString(dataOut, length), 1);
                        updateResult("\n15693 read data:" + ByteUtil.bytesToString(dataOut), 1);
                    }
                    else
                        updateResult("\n15693 read data failed, ret : " + ret, 1);
                }
                else if (cardTypeFlag == 6)
                {
                    ret = Picc.Lib_PiccGetFelicaState(dataOut);
                    if (ret == 0)
                        updateResult("\nFelica state:" + dataOut[0], 1);
                    else
                        updateResult("\nGet Felica state failed!", 1);

                    ret = Picc.Lib_PiccReadFelica(outLen, dataOut);
                    if (ret == 0)
                    {
                        //length = outLen[0];
                        //updateResult("\nFelica read data:" + ByteUtil.bytearrayToHexString(dataOut, length), 1);
                        updateResult("\nFelica read data:" + ByteUtil.bytesToString(dataOut), 1);
                    }
                    else
                        updateResult("\nFelica read data failed, ret : " + ret, 1);
                }
                else if (cardTypeFlag == 7)
                {
                    ret = Picc.Lib_PiccGetT2TState(dataOut);
                    if (ret == 0)
                        updateResult("\nT2T state:" + dataOut[0], 1);
                    else
                        updateResult("\nInit T2T state failed!", 1);

                    ret = Picc.Lib_PiccReadT2T(outLen, dataOut);
                    if (ret == 0)
                    {
                        //length = outLen[0];
                        //updateResult("\nT2T read data:" + ByteUtil.bytearrayToHexString(dataOut, length), 1);
                        updateResult("\nT2T read data:" + ByteUtil.bytesToString(dataOut), 1);
                    }
                    else
                        updateResult("T2T read data failed, ret : " + ret, 1);
                }
                mTonegenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 1000);
            } else {
                updateResult("Time Out!");
                Log.e("PICC_Thread11[ run ]", "Time Out!");
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
            mTextState.setText(R.string.nfc_read_stop);
            mBtnStart.setEnabled(true);
            super.onPostExecute(aBoolean);
        }
    }
}
