package org.techtown.ifmmanager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

public class KeyManager {   // KEY INJECTION , KEY INFORMATION

    IFMManager  mIfmManager;

    public boolean Key_Injection(String FilePath) {
        BufferedReader bufferedReader = null;
        String strline = null;
        String strKSN = null;
        String strDevSN = Utils.getDeviceSerialNumber();
        mIfmManager = new IFMManager();

        try{
            bufferedReader = new BufferedReader(new FileReader(FilePath));

            while((strline = bufferedReader.readLine()) != null){
                String [] dataFiled = strline.split(",");

                if(strDevSN.equals(dataFiled[2]) == true){
                    strKSN = dataFiled[3];
                    break;
                }
                continue;
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        if(strKSN != null){
            LinkedHashMap<String, String> reqMapData = new LinkedHashMap<>();

            reqMapData.put(IfmPkt.MapData.packetMapkey_FD[0], strKSN);

            mIfmManager.reqIFM(reqMapData,IfmPktCommon.TRD_TYPE_SN_INJECTION);
        }

        // 시리얼 번호 비교하여 KSN 가져온 뒤 KSN 주입 명령어(0xFD)호출

        return true;
    }
}
