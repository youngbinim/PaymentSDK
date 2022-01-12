package org.techtown.ifmmanager;

public class IfmPkt {
    public static final byte CMD_FS = (byte)0x1c;
    public static byte[] FS = new byte[]{CMD_FS};

    public static final byte TRD_TYPE_REQ_KSN_INJECTION         = (byte)0xFD;   //key serial number injection

    public static class MapData{
        public static final String key_sn = "key_sn";

        public static String[] packetMapkey_FD = new String[] {
                key_sn,
        };
    }
}
