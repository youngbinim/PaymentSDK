package kr.co.pointmobile.samdemo.utils;

public class PmUtils {

    public static final byte    CMD_CHECK = 0x01;
    public static final byte    CMD_ATR = 0x02;
    public static final byte    CMD_APDU = 0x03;
    public static final byte    CMD_PWR_DOWN = 0x04;

    public static final int     MAX_BUF = 516;

    public final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public String getIccErrorDesc(int errCode, String title) {

        String  retStr = title;
        retStr += getIccErrorDesc(errCode);
        return retStr;
    }

    public String getIccErrorDesc(int errCode) {
        String  retStr="";

        return retStr;
    }

    public void delayMS(int val)
    {
        try
        {
            Thread.sleep(val);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public String bytesToNibble(byte[] bytes, int maxCol)
    {
        StringBuffer sb = new StringBuffer();
        int v = 0;

        sb.append(" ");
        for(int j = 0; j < bytes.length; j++)
        {
            if((j % maxCol) == 0 && j > 0)
            {
                sb.append("\n\t");
            }

            v = bytes[j] & 0xFF;
            sb.append(hexArray[v >>> 4]);
            sb.append(hexArray[v & 0x0F]);
            sb.append(" ");
        }
        return sb.toString();
    }

    public byte hexData(char data)
    {
        byte ret = (byte) 0xff;
        if (0x30 <= data && 0x39 >= data)
        {
            ret = (byte) (data - 0x30);
        }
        else if (0x41 <= data && 0x46 >= data)
        {
            ret = (byte) (data - 0x41 + 10);
        }
        else if (0x61 <= data && 0x66 >= data)
        {
            ret = (byte) (data - 0x61 + 10);
        }
        return ret;
    }

    public int StringToHexData(String data, byte[] writeData)
    {
        int length = 0, charlength = 0;
        int strlength = data.length();
        char temp;
        byte temp2;
        for (int i = 0; i < strlength; i++)
        {
            temp = data.charAt(i);
            if (temp == ' ')
            {
                if (charlength != 0)
                    length++;
                charlength = 0;
                continue;
            }
            else if (charlength >= 2)
            {
                charlength = 0;
                length++;
            }

            temp2 = hexData(temp);
            if (temp2 != 0xff)
            {
                writeData[length] = (byte) ((writeData[length] * (charlength * 0x10)) + (temp2 & 0xF));
                charlength++;
            }
        }
        if (charlength != 0)
            length++;

        return length;
    }

    // APDU error description
    public String getIccCommandErrorDesc(int errCode)
    {
        switch (errCode * -1)
        {
            case 2503:
                return "Time out";
            case 2405:
                return "Card moved out";
            case 2401:
                return "Parity error";
            case 2403:
                return "The slot NO. error";
            case 2400:
                return "The length of send data too long";
            case 2404:
                return "The protocol error(not T=0 an T=1";
            case 2406:
                return "Card reset error";
            default:
                break;
        }
        return null;
    }

    // ATR error description
    public String getIccAtrErrorDesc(int errCode)
    {
        switch (errCode * -1)
        {
            case 2401:
                return "parity error !";
            case 2405:
                return "no card !";
            case 2402:
                return "parameter cannot be void...";
            case 2403:
                return "card slot error !";
            case 2400:
                return "data length error !";
            case 2404:
                return "protocol error !";
            case 2406:
                return "card hasn 't been initialized...";
            case 2500:
                return "IC card reset voltage mode error !";
            case 2503:
                return "communication failed...";

            case 2100:
                return "TS error !";
            case 2101:
                return "TCK error !";
            case 2102:
                return "ATR response timeout...";
            case 2103:
                return "TA1 errpr !";
            case 2104:
                return "TA2 error !";
            case 2105:
                return "TA3 error !";
            case 2106:
                return "TB1 error !";
            case 2107:
                return "TB2 error !";
            case 2108:
                return "TB3 error !";
            case 2109:
                return "TC1 error !";
            case 2110:
                return "TC2 error !";
            case 2111:
                return "TC3 error !";
            case 2112:
                return "TD1 error !";
            case 2113:
                return "TD2 error !";
            case 2114:
                return "ATR length error !";

            case 2200:
                return "card response timeout !";
            case 2201:
                return "resend error !";
            case 2202:
                return "Rereceive error !";
            case 2203:
                return "character parity error !";
            case 2204:
                return "state byte error !";

            case 2300:
                return "BWT error !";
            case 2301:
                return "CWT error !";
            case 2302:
                return "ABORT communication error !";
            case 2303:
                return "EDC error !";
            case 2304:
                return "Synchronous communication error !";
            case 2305:
                return "EGT error !";
            case 2306:
                return "BGT error !";
            case 2307:
                return "NAD error !";
            case 2308:
                return "PCB error !";
            case 2309:
                return "LEN error !";
            case 2310:
                return "IFSC error !";
            case 2311:
                return "IFSD error !";
            case 2312:
                return "too many times wrong !";
            case 2313:
                return "character parity error !";
            case 2314:
                return "invalid characters group !";
        }
        return null;
    }

    // ICC Check error description (ICC detection)
    public String getIccCheckErrorDesc(int errCode)
    {
        String retStr = "";

        switch (errCode)
        {
            case -2200:
                return "ICC_T0_TIMEOUT";
            case -2201:
                return "ICC_T0_MORE_SEND_ERR";
            case -2202:
                return "ICC_T0_MORE_RCV_ERR";
            case -2203:
                return "ICC_T0_PARAM_ERR";
            case -2204:
                return "ICC_T0_INVALID_SW";//INVALID STATUS WORD
            case -2400:
                return "ICC_DATA_LENTH_ERR";
            case -2401:
                return "ICC_PAR_ERR";
            case -2402:
                return "ICC_PARAMETER_ERR";
            case -2403:
                return "ICC_SLOT_ERR";
            case -2404:
                return "ICC_PROTOCOL_ERR";
            case -2405:
                return "ICC_CARD_OUT";
            case -2406:
                return "ICC_NO_INIT_ERR";
            case -2407:
                return "ICC_MESS_OVER_TIME";
            case -2408:
                return "ICC_PPS_ERR";
            case -2100:
                return "ICC_ATR_TS_ERR";
            case -2101:
                return "ICC_ATR_TCK_ERR";
            case -2102:
                return "ICC_ATR_TIMEOUT";
            case -2115:
                return "ICC_TS_TIMEOUT";
            case -2103:
                return "ICC_ATR_TA1_ERR";
            case -2104:
                return "ICC_ATR_TA2_ERR";
            case -2105:
                return "ICC_ATR_TA3_ERR";
            case -2106:
                return "ICC_ATR_TB1_ERR";
            case -2107:
                return "ICC_ATR_TB2_ERR";
            case -2108:
                return "ICC_ATR_TB3_ERR";
            case -2109:
                return "ICC_ATR_TC1_ERR";
            case -2110:
                return "ICC_ATR_TC2_ERR";
            case -2111:
                return "ICC_ATR_TC3_ERR";
            case -2112:
                return "ICC_ATR_TD1_ERR";
            case -2113:
                return "ICC_ATR_TD2_ERR";
            case -2114:
                return "ICC_ATR_LENGTH_ERR";
            case -2300:
                return "ICC_T1_BWT_ERR";
            case -2301:
                return "ICC_T1_CWT_ERR";
            case -2302:
                return "ICC_T1_ABORT_ERR";
            case -2303:
                return "ICC_T1_EDC_ERR";
            case -2304:
                return "ICC_T1_SYNCH_ERR";
            case -2305:
                return "ICC_T1_EGT_ERR";
            case -2306:
                return "ICC_T1_BGT_ERR";
            case -2307:
                return "ICC_T1_NAD_ERR";
            case -2308:
                return "ICC_T1_PCB_ERR";
            case -2309:
                return "ICC_T1_LENGTH_ERR";
            case -2310:
                return "ICC_T1_IFSC_ERR";
            case -2311:
                return "ICC_T1_IFSD_ERR";
            case -2312:
                return "ICC_T1_MORE_ERR";
            case -2313:
                return "ICC_T1_PARITY_ERR";
            case -2314:
                return "ICC_T1_INVLID_BLOCK";
            default:
                break;
        }
        return "Unknown Error!";
    }

}
