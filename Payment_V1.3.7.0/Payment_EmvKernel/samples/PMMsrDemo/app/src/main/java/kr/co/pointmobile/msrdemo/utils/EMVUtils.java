package kr.co.pointmobile.msrdemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vpos.emvkernel.EMV_PARAM;
import vpos.emvkernel.EmvKernel;
import vpos.emvkernel.ICallBackFunc;

public class EMVUtils
{
    private static final String TAG = EMVUtils.class.getSimpleName();

    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    public static void initEmv(Context context, String path)
    {
        checkFile(path);
        EmvKernel.EmvLib_SetFilePath(path);
        EmvKernel.EmvLib_Init2(new ICallBackFunc()
        {
            @Override
            public int cEmvLib_WaitAppSel(int i, String[] strings, int i1, byte b)
            {
                return 0;
            }

            @Override
            public int cEmvLib_GetHolderPwd(int i, int i1, byte b, byte[] bytes, byte b1)
            {
                return 0;
            }
        });

        DefaultApps.addAllAPP();//It can be called only once on the first run

        DefaultCapks.addAllEMVCapk(true);//It can be called only once on the first run
        // LogUtil.d(TAG,"# write the default CAPK! (" + ret + ")");
        setParam();
    }

    @SuppressLint("NewApi")
    private static void checkFile(String path)
    {

        File destDir = new File(path);

        if (!destDir.exists())
        {
            if (destDir.mkdirs())
            {
                destDir.setExecutable(true);
                destDir.setReadable(true);
                destDir.setWritable(true);
            }
            else
            {
            }
        }
        else
        {
            destDir.setExecutable(true);
            destDir.setReadable(true);
            destDir.setWritable(true);
            for (File f : destDir.listFiles())
            {
                // f.delete();
            }
        }
    }

    //It needs to be modified according to the actual situation
    private static int setParam()
    {
        EMV_PARAM params = new EMV_PARAM();
        int ret = EmvKernel.EmvLib_GetParam(params);

        if (ret != 0)
        {
            return ret;
        }

        params.setCountryCode(new byte[]{(byte) 0x08, (byte) 0x40});
        params.setTransCurrCode(new byte[]{(byte) 0x08, (byte) 0x40});
        params.setCapability(new byte[]{(byte) 0x60, (byte) 0xF8, (byte) 0xC8});
        params.setExCapability(new byte[]{(byte) 0xFF, (byte) 0x80, (byte) 0xF0, (byte) 0xA0, (byte) 0x01});

        ret = EmvKernel.EmvLib_SetParam(params);

        return ret;
    }

    public static String Bytes2HexString(byte[] b, int len)
    {
        return Bytes2HexString(b).substring(0, len * 2);
    }

    public static String Bytes2HexString(byte[] b)
    {
        if (b == null || b.length == 0)
        {
            return null;
        }

        byte[] buff = new byte[2 * b.length];

        for (int i = 0; i < b.length; i++)
        {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    public static byte[] getDate2Byte()
    {
        byte[] date = new byte[3];
        String s = getTime("yyyyMMdd").substring(2);
        date = HexString2Bytes(s);
        return date;
    }

    private static String getTime(String format)
    {
        Date dt = new Date();
        SimpleDateFormat matter = new SimpleDateFormat(format,
                Locale.getDefault());
        return matter.format(dt);
    }

    private static byte[] HexString2Bytes(String hexstr)
    {
        if (TextUtils.isEmpty(hexstr))
        {
            return null;
        }
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++)
        {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    private static int parse(char c)
    {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    public static byte[] getTime2Byte()
    {
        byte[] time = new byte[3];
        String s = getTime("HHmmss");
        time = HexString2Bytes(s);
        return time;
    }
}
