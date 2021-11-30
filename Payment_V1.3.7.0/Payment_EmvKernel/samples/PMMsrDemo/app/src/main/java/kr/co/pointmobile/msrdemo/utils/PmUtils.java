package kr.co.pointmobile.msrdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import device.sdk.Information;

public class PmUtils
{
    public static final char[] hexArray = "0123456789ABCDEF".toCharArray();


    public static String getSharedPreference(Context context, String prefName, String key) {
        SharedPreferences pref = context.getSharedPreferences(prefName, context.MODE_PRIVATE);
        String  val = pref.getString(key, "");

        return val;
    }

    public static void setSharedPreference(Context context, String prefName, String key, String val) {
        SharedPreferences pref = context.getSharedPreferences(prefName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, val);
        editor.commit();
    }

    public static byte[] hexToNibbleBytes(byte[] bytes) {
        byte[]  tmp=new byte[bytes.length * 2];
        int     j=0;

        for(int i=0; i < bytes.length; i++) {
            int     v=bytes[i] & 0xFF;

            tmp[j++] = (byte)(hexArray[v >>> 4]);
            tmp[j++] = (byte)(hexArray[v & 0x0F]);
        }

        return tmp;
    }

    public static boolean isPCIMode()
    {
        try{
            String partNumber = Information.getInstance().getPartNumber();
            if (partNumber != null && partNumber.length() > 6) {
                if (partNumber.charAt(12) == '6') {
                    return false;
                }
            }
            return true;
        }catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
