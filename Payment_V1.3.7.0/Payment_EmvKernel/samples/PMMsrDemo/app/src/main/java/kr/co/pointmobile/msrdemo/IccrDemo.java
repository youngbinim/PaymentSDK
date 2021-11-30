package kr.co.pointmobile.msrdemo;

import android.content.Context;
import android.util.Log;

import kr.co.pointmobile.msrdemo.utils.EMVUtils;

import static kr.co.pointmobile.msrdemo.utils.EMVUtils.Bytes2HexString;
import static kr.co.pointmobile.msrdemo.utils.EMVUtils.getDate2Byte;
import static kr.co.pointmobile.msrdemo.utils.EMVUtils.getTime2Byte;
import static kr.co.pointmobile.msrdemo.utils.PmUtils.getSharedPreference;
import static kr.co.pointmobile.msrdemo.utils.PmUtils.setSharedPreference;
import static vpos.apipackage.PinPad.Lib_LoadEncryptWorkKeyPinPad;
import static vpos.apipackage.PinPad.Lib_LoadKeyPinPad;
import static vpos.emvkernel.EmvKernel.EmvLib_AppSel;
import static vpos.emvkernel.EmvKernel.EmvLib_BeforeTrans;
import static vpos.emvkernel.EmvKernel.EmvLib_TransInit;

public class IccrDemo
{

    private static final int SUCCESS = 0;

    public static void initIccrDemo(Context context, String path)
    {

        String emvInit = getSharedPreference(context, "EMV-Init", "Perform");
        Log.d("IccDemo", emvInit);

        Log.d("", "app path-->" + path);

        EMVUtils.initEmv(context, path);

        if (!emvInit.equals("YES"))
        {
            setSharedPreference(context, "EMV-Init", "Perform", "YES");

            byte[] key_data = new byte[]{(byte) 0x12, (byte) 0x34,(byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0x12, (byte) 0x34, (byte) 0x56,
                    (byte) 0x78, (byte) 0x90, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0x12};

            Lib_LoadKeyPinPad((byte) 0, (byte) 0, (byte) 0, (byte) key_data.length, key_data);

            byte[] workkey = new byte[]{(byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38,
                    (byte) 0x39, (byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36};

            Lib_LoadEncryptWorkKeyPinPad((byte) 0X06, (byte) 0, (byte) 0, (byte) workkey.length, workkey, workkey);
        }

    }

    public static void startEMV()
    {
        Log.d("", "*************startEMV*************");

        long transAmount = 1L;
        long backamt = 0L;
        byte[] tdate = getDate2Byte();
        byte[] ttime = getTime2Byte();

        Log.d("", "date:" + Bytes2HexString(tdate));
        Log.d("", "time:" + Bytes2HexString(ttime));

        EmvLib_TransInit();

        int ret = EmvLib_BeforeTrans(transAmount, backamt, tdate, ttime);
        if (ret != SUCCESS)
        {
            Log.d("@ EmvLib_BeforeTrans", "(" + ret + ")");
        }

        long TransNo = 0L;
        ret = EmvLib_AppSel((byte) 0/*slot*/, TransNo);
        if (ret != SUCCESS)
        {
            Log.d("@ EmvLib_AppSel fail!", "(" + ret + ")");
        }
    }
}
