package org.techtown.ifmmanager;

public class IfmPktCommon {
    public static final byte TRD_TYPE_SN_INJECTION = (byte)0xFD;    // key serial number injection command
    public static class Mapdata{

        public static final String  fwVersion="fwVersion";
        public static final String  fwBuildDate="fwBuildDate";
        public static final String  fwBiildType="fwBiildType";

        public static String[] callbackMapKey_0A = new String[] {
                fwVersion,
                fwBuildDate,
                fwBiildType
        };
    }
}
