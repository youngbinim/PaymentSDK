package org.techtown.ifmmanager;

import android.content.Context;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class IfmMakePkt {

    private Context mContext;

    public IfmMakePkt(Context context){
        mContext = context;
    }

    public byte [] makeICReaderPacket(HashMap<String, String> reqMapData, byte CommandID)
    {
        byte [] rdPktBuf = null;
        byte[]  dataBuf =null;
        int index;

        switch(CommandID){
            case IfmPkt.TRD_TYPE_REQ_KSN_INJECTION: {
                byte[] key_sn = new byte[10];

                key_sn = reqMapData.get(IfmPkt.MapData.key_sn).getBytes();
                index = 0;
                System.arraycopy(key_sn,0,dataBuf,index,key_sn.length);
                index = key_sn.length;
                dataBuf[index] = (byte)IfmPkt.CMD_FS;

                rdPktBuf =
                break;
            }
        }

        return rdPktBuf;
    }
}
