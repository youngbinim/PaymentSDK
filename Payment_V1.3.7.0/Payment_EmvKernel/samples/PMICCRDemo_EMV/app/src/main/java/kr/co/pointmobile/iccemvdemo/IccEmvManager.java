package kr.co.pointmobile.iccemvdemo;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import vpos.emvkernel.EMV_PARAM;
import vpos.emvkernel.EmvKernel;
import vpos.emvkernel.ICallBackFunc;

import static vpos.emvkernel.EmvKernel.EmvLib_AppSel;
import static vpos.emvkernel.EmvKernel.EmvLib_BeforeTrans;
import static vpos.emvkernel.EmvKernel.EmvLib_ProcTransBeforeOnline;
import static vpos.emvkernel.EmvKernel.EmvLib_TransInit;

public class IccEmvManager
{
	// EMV Return Code
	public static final int EMV_SUCCESS = 0;				// Success
	public static final int EMV_ERR_RET = (-1);
	public static final int EMV_ERR_APPBLOCK = (-2);		// Applilcation Locked
	public static final int EMV_ERR_NOAPP = (-3); 			// No application in card
	public static final int EMV_ERR_USERCANCEL = (-4); 		// User cancel
	public static final int EMV_ERR_TIMEOUT = (-5); 		// Timeout
	public static final int EMV_ERR_EMVDATA = (-6); 		// Card data error
	public static final int EMV_ERR_NOTACCEPT = (-7); 		// Transaction not accept
	public static final int ERR_EMVDENIAL = (-8); 			// Transaction declined
	public static final int EMV_ERR_KEYEXP = (-9); 			// The key expired
	public static final int EMV_ERR_NOPINPAD = (-10); 		// No pinpad
	public static final int EMV_ERR_NOPIN = (-11); 			// No pin input
	public static final int EMV_ERR_CAPKCHECKSUM = (-12); 	// Check sum error
	public static final int EMV_ERR_NOTFOUND = (-13); 		// No data found
	public static final int EMV_ERR_NODATA = (-14); 		// No data found
	public static final int EMV_ERR_OVERFLOW = (-15); 		// Over flow
	public static final int EMV_ERR_NOTRANSLOG = (-16); 	// No log
	public static final int EMV_ERR_NORECORD = (-17); 		// No record
	public static final int EMV_ERR_NOLOGITEM = (-18); 		// No log item
	public static final int EMV_ERR_ICCRESET = (-19); 		// Icc reset error
	public static final int EMV_ERR_ICCCMD = (-20); 		// Icc command error
	public static final int EMV_ERR_ICCBLOCK = (-21); 		// Icc locked
	public static final int EMV_ERR_ICCNORECORD = (-22); 	// Icc no record
	public static final int EMV_ERR_USECONTACT = (-23); 	// RFID failed,
	public static final int EMV_ERR_APPEXP = (-24); 		// qPBOC card expired
	public static final int EMV_ERR_BLACKLIST = (-25); 		// qPBOC black list card
	public static final int EMV_ERR_GPORSP = (-26);			// Err from GPO
	public static final int EMV_ERR_USE_OTHER = (-27);
	public static final int EMV_ERR_LASTREAD = (-28); 		// Read last recode error
	public static final int EMV_ERR_TRANSEXCEEDED = (-29);	// Trance exceeded
	public static final int EMV_ERR_NULL = (-30); 			// Path error
	public static final int EMV_ERR_NOAMT = (-31); 			// No Amount Error
	public static final int EMV_ERR_PINBLOCK = (-32);		// PIN locked
	public static final int EMV_ERR_FILE = (-33); 			// EMV file error
	public static final int EMV_DATA_EXIST = (-34);			// Data has existed
	public static final int EMV_ERR_APPPATH = (-35);    	// Path error
	public static final int EMV_ERR_PAYSCHEME = (-37); 		// Refer your mobile payment
	public static final int EMV_ERR_NOTALLOWED = (-38);		// Application is not

	// EMV PARAMs
	private static final byte[] MerchantName = {
			'P', 'o', 'i', 'n', 't', 'm', 'o', 'b', 'i', 'l', 'e'
	};                                                                                          //9F4E
	private static final byte[] MerchantCategoryCode = {(byte) 0x00, (byte) 0x01, '1', '2'};    //9F15
	private static final byte[] MerchantID = {
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5'
	};                                                                                          //9F16
	private static final byte[] TerminalID = {'1', '2', '3', '4', '5', '6', '7', '8'};          //9F1C
	private static final byte TerminalType = (byte) 0x22;                                       //9F35
	private static final byte[] TerminalCapability = {
			(byte) 0x60, (byte) 0xF8, (byte) 0xC8
	};                                                                                          //9F33
	private static final byte[] AdditionalExCapability = {
			(byte) 0xFF, (byte) 0x80, (byte) 0xF0, (byte) 0xA0, 0x01
	};                                                                                          //9F40
	private static final byte TransactionCurrencyExponent = (byte) 0x02;                        //5F36
	private static final byte TransactionReferenceCurrencyExponent = (byte) 0x02;               //9F3D
	private static final byte[] TransactionReferenceCurrency = {(byte) 0x04, (byte) 0x10};      //9F3C
	private static final byte[] TerminalCountryCode = {(byte) 0x04, (byte) 0x10};               //9F1A
	private static final byte[] TransactionCurrencyCode = {(byte) 0x04, (byte) 0x10};           //5F2A

	private static final long TransactionReferenceCurrencyConversion = (long)0;
	private static final byte bBatchCapture = (byte) 0x00;
	private static final byte bSupportAdvices = (byte) 0x00;                                    //default : 0x01
	private static final byte TrasnsactionType = (byte) 0x00;                                   //9C
	private static final byte ForceOnline = (byte) 0x01;                                        //1: Force the transaction online
	private static final byte GetDataPIN = (byte) 0x00;                                         //1: Support, 0:Not Support
	private static final byte SupportPPSESel = (byte) 0x01;
	private static final byte[] TerminalTransactionQuality = {
			(byte) 0x24, (byte) 0x00, (byte) 0x40, (byte) 0x80
	};                                                                                          //9F66
	private static final byte[] IFD_SN = {
			'0', '0', '0', '0', '0', '0', '0', '0', '1'
	};                                                                                          //9F1E(Interface device serial number)

	// Not Used for EMV
	private static final byte ECTSI = (byte) 0x00;
	private static final byte EC_bTermLimitCheck = (byte) 0x01;
	private static final long EC_TermLimit = (long) 100;
	private static final byte CL_bStatusCheck = (byte) 0x00;
	private static final long CL_FloorLimit = (long) 100;
	private static final long CL_TransLimit = (long) 100;
	private static final long CL_CVMLimit = (long) 100;
	private static final byte SMTSI = (byte) 0x00;
	private static final byte bExceptionFile = (byte) 0x01;// Whether the exception file are supported, 1:support, 0:not support

	private static String[] TagNames = {
		// PMPOS_KS Tags
		"57",   "5F34",   "5A", "9F27", "9F53",   "9C", "9F03", "9F34",
		"9F35", "9F1E",   "95", "9F09", "9F1A", "9F41", "9F26",   "9A",
		"9F36", "9F37", "9F10", "9F02",   "84", "9F33", "9F15", "9F21",
		"5F2A",   "82", "9F06", "5F28", "BF0C", "9F6E"
		//-
	};

	public String getSharedPreference(Context context, String prefName, String key)
	{
		SharedPreferences pref = context.getSharedPreferences(prefName, 0);
		String val = pref.getString(key, "");
		return val;
	}

	public void setSharedPreference(Context context, String prefName, String key, String val)
	{
		SharedPreferences pref = context.getSharedPreferences(prefName, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public static EMV_PARAM setDefaultParam(EMV_PARAM params)
	{
		params.setMerchName(MerchantName);
		params.setMerchCateCode(MerchantCategoryCode);
		params.setMerchId(MerchantID);
		params.setTermId(TerminalID);
		params.setTerminalType(TerminalType);
		params.setCapability(TerminalCapability);
		params.setExCapability(AdditionalExCapability);
		params.setTransCurrExp(TransactionCurrencyExponent);
		params.setReferCurrExp(TransactionReferenceCurrencyExponent);
		params.setReferCurrCode(TransactionReferenceCurrency);
		params.setCountryCode(TerminalCountryCode);
		params.setTransCurrCode(TransactionCurrencyCode);
		params.setReferCurrCon(TransactionReferenceCurrencyConversion);
		params.setbBatchCapture(bBatchCapture);
		params.setbSupportAdvices(bSupportAdvices);
		params.setTransType(TrasnsactionType);
		params.setForceOnline(ForceOnline);
		params.setGetDataPIN(GetDataPIN);
		params.setSurportPSESel(SupportPPSESel);
		params.setTermTransQuali(TerminalTransactionQuality);
		params.setIFD_SN(IFD_SN);
		return params;
	}

	//TODO : Set the EMV Configuration Data : 필 확인요망
	//It needs to be modified according to the actual situation
	public static int setParam()
	{
		EMV_PARAM params = new EMV_PARAM();
		int ret = EmvKernel.EmvLib_GetParam(params);

		if (ret != 0)
		{
			return ret;
		}

		params = setDefaultParam(params);
		ret = EmvKernel.EmvLib_SetParam(params);

		return ret;
	}

	public void initIccEmv(Context context, String path)
	{
		String emvInit = getSharedPreference(context, "EMV-Init", "Perform");
		Log.d("@ IccDemo", emvInit);

		Log.d("", "app path-->" + path);

		initEmv(context, path);

		if (!emvInit.equals("YES"))
		{
			setSharedPreference(context, "EMV-Init", "Perform", "YES");

//			byte[] key_data = new byte[]{(byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0x12, (byte) 0x34, (byte) 0x56,
//					(byte) 0x78, (byte) 0x90, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x90, (byte) 0x12};
//
//			byte[] workkey = new byte[]{(byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38,
//					(byte) 0x39, (byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36};
//
//			// TODO : 용도 확인요망 : 사용하지 않을 것으로 판단. Library_의 Pin-Pad 참조
//			Lib_LoadKeyPinPad((byte) 0, (byte) 0, (byte) 0, (byte) key_data.length, key_data); //required modify
//			Lib_LoadEncryptWorkKeyPinPad((byte) 0X06, (byte) 0, (byte) 0, (byte) workkey.length, workkey, workkey); //required modify
		}

	}

	public static void initEmv(Context context, String path)
	{
		EmvUtils.checkFile(path);
		EmvKernel.EmvLib_SetFilePath(path);
		EmvKernel.EmvLib_Init2(new ICallBackFunc()
		{
			@Override
			public int cEmvLib_WaitAppSel(int i, String[] strings, int i1, byte b)
			{
				Log.d("@initEmv", "\r\n>> call the cEmvLib_WaitAppSel()");
				return 0;
			}

			@Override
			public int cEmvLib_GetHolderPwd(int i, int i1, byte b, byte[] bytes, byte b1)
			{
				Log.d("@initEmv", "\r\n>> call the cEmvLib_GetHolderPwd()");
				return 0;
			}
		});

		DefaultApps.addAllAPP();				//It can be called only once on the first run
		DefaultCapks.addAllEMVCapk(true);//It can be called only once on the first run
		setParam();
	}


	private static String proData(String data) {
		if (TextUtils.isEmpty(data)) {
			return data;
		}
		if (data.length() == 2) {
			return "  " + data;
		} else {
			return data;
		}
	}

	private static void getTlvValue(String[] tags) {
		Log.d("@ getTlvValue", "*************getTLV*************");
		if (tags == null || tags.length == 0) {
			return;
		}
		String result = "";
		int len[] = new int[1];
		byte buff[] = new byte[80];

		for (String key : tags) {
			int ret = EmvKernel.EmvLib_GetTLV(key, buff, len);
			if (ret == 0) {
				result += (proData(key) + ":    "
						+ EmvUtils.Bytes2HexString(buff, len[0]) + "      Len:"
						+ len[0] + "\r\n");
			} else {
				result += (proData(key) + ":    null\r\n");
			}
		}

		//NOTICE: Display the EMV TLV Data  in Log Window
		Log.d("", result);
		//-
	}

	private static int transComplete() {
		byte Result = 0;
		byte RspCode[] = { 0x30, 0x30 };
		byte AuthCode[] = new byte[6];
		int AuthCodeLen = 0;
		byte IAuthData[] = new byte[64];
		int IAuthDataLen = 0;
		byte Script[] = new byte[128];
		int ScriptLen = 0;
		int ret = 0;
		int resultLen[] = {0};
		byte result[] = new byte[100];

		ret = EmvKernel.EmvLib_ProcTransComplete(Result, RspCode, AuthCode,
				AuthCodeLen, IAuthData, IAuthDataLen, Script, ScriptLen);
		Log.d(">> EmvLib_ProcTransComplete", "ret=" + ret);

		//The return value of the script result does not affect the transaction result
		int iret = EmvKernel.EmvLib_GetScriptResult(result, resultLen);
		Log.d(">> EmvLib_GetScriptResult", "ret=" + iret + ", resultLen="+resultLen[0]);

		return ret;
	}

	public static void startEMV()
	{
		Log.d("@ startEMV", "*************startEMV*************");

		long transAmount = 1L;
		long backamt = 0L;
		byte[] tdate = EmvUtils.getDate2Byte();
		byte[] ttime = EmvUtils.getTime2Byte();
		int ret = EMV_SUCCESS;

		Log.d("", "date:" + EmvUtils.Bytes2HexString(tdate));
		Log.d("", "time:" + EmvUtils.Bytes2HexString(ttime));

		EmvLib_TransInit();

		ret = EmvLib_BeforeTrans(transAmount, backamt, tdate, ttime);
		if (EMV_SUCCESS != ret)
		{
			Log.d(">> EmvLib_BeforeTrans", "(" + ret + ")");
			return;
		}

		/**<
		 * EMV transaction processing including select application, GPO, read application data.
		 */
		long TransNo = 0L;
		ret = EmvLib_AppSel((byte) 0/*slot*/, TransNo);
		if (EMV_SUCCESS != ret)
		{
			Log.d(">> EmvLib_AppSel fail!", "(" + ret + ")");
			return;
		}

		/**<
		 * EMV transaction processing including offline data authentication, terminal risk management,
		 * cardholder verification, terminal action analysis, card action analysis and the first GAC.
		 */
		byte[] ifonline = new byte[2];
		ret = EmvLib_ProcTransBeforeOnline(ifonline);
		if (EMV_SUCCESS != ret) {
			Log.d(">> EmvLib_ProcTransBeforeOnline fail!", "(" + ret + ")");
			return ;
		}
		Log.d(">> EmvLib_ProcTransBeforeOnline", "isOnline-->" + (ifonline[0] == 1 ? "Online" : "Offline"));

		getTlvValue(TagNames);

		if (1 == ifonline[0]){
			//you need to send the online message to the issuer

			//and after receive the back message,you need call this interface to complete the trade
			ret = transComplete();
		}
	}
}
