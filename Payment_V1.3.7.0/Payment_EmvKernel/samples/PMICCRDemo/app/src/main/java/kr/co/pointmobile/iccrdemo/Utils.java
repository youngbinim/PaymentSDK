package kr.co.pointmobile.iccrdemo;

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

public class Utils
{
    private static final String TAG = Utils.class.getSimpleName();

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


    /**
     * <
     * for debugging
     *
     * @param bytes    : byte[]{0x31, 0x32, 0x33, 0x34, 0x35...}
     * @param startPos : 변환할 buffer의 시작 위치
     * @param length   : 변환할 길이
     * @return : String("... 33 34 ...)
     */
    public static String bytesToNibble(byte[] bytes, int startPos, int length)
    {
        StringBuffer sb = new StringBuffer();
        int v = 0;

        if (bytes.length < (startPos + length))
        {
            return null;
//			return new String("");
        }

        byte[] tmp = new byte[length];
        System.arraycopy(bytes, startPos, tmp, 0, length);

        return bytesToNibble(tmp);
    }

    /**
     * <
     * for debugging
     *
     * @param bytes : byte[]{0x31, 0x32, 0x33...}
     * @return : String{"31 32 33..."}
     */
    public static String bytesToNibble(byte[] bytes)
    {
        StringBuffer sb = new StringBuffer();
        int v = 0;

        sb.append(" ");
        for (int j = 0; j < bytes.length; j++)
        {
            if ((j % 16) == 0 && j > 0)
            {
                sb.append("\n\t");
            }

            v = bytes[j] & 0xFF;
            sb.append(hex[v >>> 4]);
            sb.append(hex[v & 0x0F]);
            sb.append(" ");
        }

//		sb.append("\n");

        return sb.toString();
    }

    /**
     * <
     *
     * @param nibble : String{"2019"} => byte[]{0x20, 0x19}
     * @return
     */
    public static byte[] nibbleToHex(String nibble)
    {
        byte[] dest = new byte[nibble.length() / 2];

        int srcLength = nibble.length();

        /**< make a fair */
        srcLength = (srcLength / 2) * 2;

        for (int i = 0; i < srcLength; i += 2)
        {
//			byte    tmp1 = (byte)Character.digit(nibble.charAt(i), 16);
//			byte    tmp2 = (byte)Character.digit(nibble.charAt(i + 1), 16);
//			byte    tmp3 = (byte)((tmp1 << 4) + tmp2);

            dest[i / 2] = (byte) ((Character.digit(nibble.charAt(i), 16) << 4) +
                    Character.digit(nibble.charAt(i + 1), 16));
        }

        return dest;
    }

    /**
     * <
     *
     * @param nibble : byte[]{0x32, 0x30, 0x31, 0x39} => byte[]{0x20, 0x19}
     * @return
     */
    public static byte[] nibbleToHex(byte[] nibble)
    {

        if ((nibble.length % 2) == 1)
        {
            return new byte[0];
        }

        byte[] dest = new byte[nibble.length / 2];

        for (int i = 0; i < nibble.length; i += 2)
        {
            dest[i / 2] = (byte) ((Character.digit(nibble[i], 16) << 4) +
                    Character.digit(nibble[i + 1], 16));
        }

        return dest;
    }


    /**
     * <
     *
     * @param bytes : byte[]{0x04, 0x056, 0x37}
     * @return : byte[]{0x30, 0x34, 0x35, 0x36, 0x33, 0x37}
     */
    public static byte[] hexToNibbleBytes(byte[] bytes)
    {
        byte[] tmp = new byte[bytes.length * 2];
        int j = 0;

        for (int i = 0; i < bytes.length; i++)
        {
            int v = bytes[i] & 0xFF;

            tmp[j++] = (byte) (hex[v >>> 4]);
            tmp[j++] = (byte) (hex[v & 0x0F]);
        }

        return tmp;
    }
}
