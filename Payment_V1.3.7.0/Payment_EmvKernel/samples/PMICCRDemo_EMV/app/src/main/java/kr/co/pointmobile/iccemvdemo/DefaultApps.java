package kr.co.pointmobile.iccemvdemo;

//import static vpos.emvkernel.EMV_APPLIST.*;
import android.util.Log;

import vpos.emvkernel.EMV_APPLIST;

import static vpos.emvkernel.EmvKernel.EmvLib_AddApp;

public class DefaultApps {
	static byte PART_MATCH=0x00;
	static byte FULL_MATCH=0x01;
	static byte priority=0;
	static byte targetPer=0;
	static byte maxTargetPer=0;
	static byte floorLimitCheck=1;
	static byte randTransSel=1;
	static byte velocityCheck=1;
	static long floorLimit=2000;
	static long threshold=0;
	static byte[] tacDenial={(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	static byte[] tacOnline={(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	static byte[] tacDefault={(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	static byte[] acquierID={(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56 };
//	static byte[] dDOL={(byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04};
	static byte[] dDOL={(byte) 0x0B,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04, (byte) 0x9F, (byte) 0x1C, (byte) 0x08,
						(byte) 0x9F, (byte) 0x1A, (byte) 0x02, (byte) 0x9A, (byte) 0x03};
//	static byte[] tDOL={(byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04};
	static byte[] tDOL={(byte) 0x03, (byte) 0x5F, (byte) 0x24, (byte) 0x03};
	static byte[] version={ (byte) 0x00, (byte) 0x8c };
	static byte[] riskManData=new byte[10];
	static byte ecTermLimitCheck=0;
	static long ecTermLimit=10000;
	static byte clStatusCheck=1;
	static long clFloorLimit=10000;
	static long clTransLimit=50000;
	static long clCVMLimit=20000;
	static byte TermQuali_byte2=0;
	
	public static void addAllAPP() {
		setDefaultConfig();
//		add_VSDC_APP();
//		add_EMV_TEST_APP();
//		add_EMV_TEST2_APP();
//		add_VSDC_APP3();
//		add_VSDC_APP4();
//		add_VSDC_APP5();
//		add_VSDC_APP6();
//		add_VSDC_APP7();
//		add_MASTER_TEST_APP();
//		add_JCB_TEST_APP();
//		// add_TEST_APP();
//		// ////////////////////////////////////
//		// add 20091110 for AMEX testing.
//		add_AMEX_TEST_APP();
//		add_EMV_TEST_ANOD();
//		add_EMV_TEST_ANOE();
//		add_PBOC_TEST_APP();
//		// add 2014025 for EMV L2 4.3c
//		add_DISCOVER_TEST_APP();
//		add_CUP_TEST_APP();
	}
	
	public static final int VISA_CREDIT=0;
	public static final int VISA_ELECTRON=1;
	public static final int VISA_INTERLINK=2;
	public static final int MASTER_CREDIT=3;
	public static final int MASTER_DEBIT=4;
	public static final int MASTER_CIRRUS=5;
	public static final int JCB_CREDIT=6;
	public static final int LOCAL_VISA=7;
	public static final int LOCAL_MASTER=8;
	public static final int LOCAL_DEBIT=9;
	public static final int AMEX_EXP=10;
	public static final int DISCOVER_CARD=11;
	public static final int UICC_DEBIT=12;
	public static final int UICC_CREDIT=13;
	public static final int UICC_QUASI=14;
	public static final int CONA_MOMEY=15;

//	AID, aidLen, selFlag, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck,
//	floorLimit, threshold,
//	tacDenial,
//	tacOnline,
//	tacDefault,
//	acquierID,
//	dDOL,
//	tDOL,
//	version, riskManData,
//	ecbTermLimitCheck, ecTermLimit, clbStatusCheck, clFloorLimit, clTransLimit, clCVMLimit
	
	public static EMV_APPLIST[]   emvAppList = new EMV_APPLIST[32];
	
	public static void setDefaultConfig() {
		//1. VISA Credit or Debit
		EMV_APPLIST tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x10, (byte)0x10, (byte)0x00, (byte)0x00},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},//TAC Denial
				new byte[] {(byte)0x58, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00},//TAC Online
				new byte[] {(byte)0x58, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00},//TAC Default
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x84},//application version
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		
		int ret = EmvLib_AddApp(tmpEA);
		Log.d("Lib-PM500", "@ return = " + ret);
		
		emvAppList[VISA_CREDIT] = tmpEA;
		
		// 2 Visa Electron
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x20, (byte)0x10},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},//TAC Denial
				new byte[] {(byte)0x58, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00},//TAC Online
				new byte[] {(byte)0x58, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00},//TAC Default
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x84},//application version
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(tmpEA);
		emvAppList[VISA_ELECTRON] = tmpEA;
		
		// 3 Visa Interlink :: Oct.14.2017(add-mskl)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x30, (byte)0x10},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0x58, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0x58, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x84},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[VISA_INTERLINK] = tmpEA;
		
		// 4 MASTER Card (credit)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x10, (byte)0x10},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x02},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[MASTER_CREDIT] = tmpEA;
		
		// 5 MASTER Card (debit)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x30, (byte)0x60},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00},
				new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[]{(byte)0x00, (byte)0x02},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[MASTER_DEBIT] = tmpEA;
		
		// 6 MASTER Card (Cirrus)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x60, (byte)0x00},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00},
				new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[]{(byte)0x00, (byte)0x02},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[MASTER_CIRRUS] = tmpEA;
		
		// 7 JCB Card(smart credit)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x65, (byte)0x10, (byte)0x10},
				(byte) 7,
				PART_MATCH,
				(byte) 0,//priority
				(byte) 0,//targetPer
				(byte) 0,//maxTargetPer
				(byte) 1,//floorLimitCheck
				(byte) 1,//randTransSel
				(byte) 1,//velocityCheck
				floorLimit,
				(byte) 0,//threshold
				new byte[] {(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0xFC, (byte)0x60, (byte)0xAC, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0xFC, (byte)0x60, (byte)0x24, (byte)0x28, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x02, (byte)0x00},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[JCB_CREDIT] = tmpEA;
		
		// 8 Local Card 1 : Visa
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xD4, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x10, (byte)0x10},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x84},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[LOCAL_VISA] = tmpEA;
		
		// 9 Local Card 2 : Master
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xD4, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x20, (byte)0x10},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x02},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[LOCAL_MASTER] = tmpEA;
		
		// 10 Local Card 3 : Debit
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xD4, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x20, (byte)0x20},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00},
				new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[]{(byte)0x00, (byte)0x84},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[LOCAL_DEBIT] = tmpEA;
		
		// 11 American Express
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x25, (byte)0x01},
				(byte) 6,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[]{(byte)0xCC, (byte)0x00, (byte)0xFC, (byte)0x80, (byte)0x00},
				new byte[]{(byte)0xFC, (byte)0x50, (byte)0xFC, (byte)0x20, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[]{(byte)0x00, (byte)0x01},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[AMEX_EXP] = tmpEA;
		
		// 12 DPAS
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x52, (byte)0x30, (byte)0x10},
				(byte) 7,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[]{(byte)0xCC, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[]{(byte)0xCC, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[]{(byte)0x00, (byte)0x01},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[DISCOVER_CARD] = tmpEA;
		
		// 13 - UICC(UionPay Debit)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x33, (byte)0x01, (byte)0x01, (byte)0x01},
				(byte) 8,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[]{(byte)0x00, (byte)0x20},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[UICC_DEBIT] = tmpEA;
		
		// 14 - UICC(UionPay Credit)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x33, (byte)0x01, (byte)0x01, (byte)0x02},
				(byte) 8,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x20},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[UICC_CREDIT] = tmpEA;
		
		// 15 - UICC(UionPay Quasi)
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x33, (byte)0x01, (byte)0x01, (byte)0x03},
				(byte) 8,
				PART_MATCH, priority, targetPer, maxTargetPer, floorLimitCheck, randTransSel, velocityCheck, floorLimit, threshold,
				new byte[] {(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[] {(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00},
				new byte[] {(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00},
				acquierID, dDOL, tDOL,
				new byte[] {(byte)0x00, (byte)0x20},
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck, clFloorLimit, clTransLimit, clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[UICC_QUASI] = tmpEA;
		
		// 16 - 코나머니
		tmpEA = new EMV_APPLIST(
				new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x51, (byte)0x40, (byte)0x25, (byte)0x26},
				(byte) 6,
				PART_MATCH,
				(byte) 0,//priority
				(byte) 0,//targetPer
				(byte) 0,//maxTargetPer
				(byte) 1,//floorLimitCheck
				(byte) 1,//randTransSel
				(byte) 1,//velocityCheck
				floorLimit,
				(byte) 0,//threshold
				new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00},
				new byte[]{(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00},
				new byte[]{(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00},
				new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x12, (byte)0x34, (byte)0x56 },//acquierID : TODO : 확인
				new byte[] {(byte)0x03, (byte)0x9F, (byte)0x37, (byte)0x04 },//dDOL : TODO : 확인
				new byte[] {(byte)0x0F, (byte)0x9F, (byte)0x02, (byte)0x06, (byte)0x5F,
						(byte)0x2A, (byte)0x02, (byte)0x9A, (byte)0x03, (byte)0x9C,
						(byte)0x01, (byte)0x95, (byte)0x05, (byte)0x9F, (byte)0x37, (byte)0x04 },//tDOL : TODO : 확인
				new byte[]{(byte)0x00, (byte)0x84},
				riskManData,
				ecTermLimitCheck,
				ecTermLimit,
				clStatusCheck,
				clFloorLimit,
				clTransLimit,
				clCVMLimit);
		EmvLib_AddApp(tmpEA);
		emvAppList[CONA_MOMEY] = tmpEA;
		
		/**< end of adding */
		return;
	}
	
	/////////////////////////////////////////////////
	public static final int MAX_EMV_APP = 32;
	
	public static EMV_APPLIST getEmvApp(int brand) {
		// array 0 ~ 15
		if(brand >= MAX_EMV_APP) {
			return null;
		}
		
		return emvAppList[brand];
	}
	

	public static void add_EMV_TEST_ANOD() {
		byte[] AID1 = { (byte) 0xA1,(byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55 };
		byte AidLen1 = 5;
		byte[] Version1 = {(byte) 0x12, (byte) 0x34};
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);

		EmvLib_AddApp(app);

	}
	
	public static EMV_APPLIST get_EMV_TEST_ANOD() {
		EMV_APPLIST app = new EMV_APPLIST(
				new byte[] { (byte) 0xA1, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55 },//AID
				(byte) 5,//aidLen
				PART_MATCH,//selFlag
				(byte) 0,//priority
				(byte) 0,//targetPer
				(byte) 0,//maxTargetPer
				(byte) 1,//floorLimitCheck
				(byte) 1,//randTransSel
				(byte) 1,//velocityCheck
				2000,//floorLimit
				(byte) 0,//threshold
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },//tacDenial
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },//tacOnline
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },//tacDefault
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56 },//acquierID
				new byte[] { (byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },//dDOL
				new byte[] { (byte) 0x0F, (byte) 0x9F, (byte) 0x02,	(byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03, (byte) 0x9C,
						(byte) 0x01, (byte) 0x95, (byte) 0x05, (byte) 0x9F, (byte) 0x37,
						(byte) 0x04 }, new byte[] { (byte) 0x12, (byte) 0x34 },//tDOL
				riskManData,
				ecTermLimitCheck,
				ecTermLimit,
				clStatusCheck,
				clFloorLimit,
				clTransLimit,
				clCVMLimit);

		return app;

	}

	public static void add_EMV_TEST_ANOE() {
		byte[] AID1 = { (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10,
						(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09 };
		byte AidLen1 = 16;
		byte[] Version1 = { (byte) 0x00, (byte) 0x96 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);

		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_EMV_TEST_ANOE() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x10, (byte) 0x10, (byte) 0x01, (byte) 0x02,
				(byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06,
				(byte) 0x07, (byte) 0x08, (byte) 0x09 }, (byte) 16, PART_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				2000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
						(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
						(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
						(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x00, (byte) 0x96 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);

		return app;
	}

	public static void add_EMV_TEST_APP() {
		byte[] AID1 = { (byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x99,(byte) 0x90, (byte) 0x90 };
		byte AidLen1 = 7;
		byte[] Version1 = { (byte) 0x00, (byte) 0x09 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);

		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_EMV_TEST_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x99,
				(byte) 0x90, (byte) 0x90 }, (byte) 7, PART_MATCH, (byte) 0,
				(byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1, 2000,
				(byte) 0, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34,
						(byte) 0x56 }, new byte[] { (byte) 0x03, (byte) 0x9F,
						(byte) 0x37, (byte) 0x04 }, new byte[] { (byte) 0x0F,
						(byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03,
						(byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x00, (byte) 0x09 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);

		return app;
	}

	public static void add_JCB_TEST_APP() {
		byte[] AID1 = { (byte) 0xF1, (byte) 0x23, (byte) 0x45, (byte) 0x67,(byte) 0x89, (byte) 0x01, (byte) 0x23 };
		byte AidLen1 = 7;
		byte[] Version1 = { (byte) 0x00, (byte) 0x8C };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);

		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_JCB_TEST_APP() {
		byte[] AID1 = {(byte) 0xF1, (byte) 0x23, (byte) 0x45, (byte) 0x67,
						(byte) 0x89, (byte) 0x01, (byte) 0x23 };
		byte AidLen1 = 7;
		byte selFlag = PART_MATCH;
		byte priority = 0;
		byte targetPer = 0;
		byte maxTargetPer = 0;
		byte floorLimitCheck = 1;
		byte randTransSel = 1;
		byte velocityCheck = 1;
		long floorLimit = 2000;
		long threshold = 0;
		byte[] tacDenial = { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };
		byte[] tacOnline = { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };
		byte[] tacDefault = { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00 };
		byte[] acquierID = { (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x12, (byte) 0x34, (byte) 0x56 };
		byte[] dDOL = { (byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04 };
		byte[] tDOL = { (byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
				(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
				(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
				(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 };
		byte[] version = { (byte) 0x00, (byte) 0x8c };

		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1, selFlag,
				priority, targetPer, maxTargetPer, floorLimitCheck,
				randTransSel, velocityCheck, floorLimit, threshold, tacDenial,
				tacOnline, tacDefault, acquierID, dDOL, tDOL, version,
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);

		return app;
	}

	public static void add_VSDC_APP() {
		byte[] AID1 = {  (byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10};
		byte AidLen1 = 7;
		byte[] Version1 = { (byte) 0x00, (byte) 0x96 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);

		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_VSDC_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x10, (byte) 0x10 }, (byte) 7, PART_MATCH, (byte) 0,
				(byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1, 2000, 0,
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34,
						(byte) 0x56 }, new byte[] { (byte) 0x03, (byte) 0x9F,
						(byte) 0x37, (byte) 0x04 }, new byte[] { (byte) 0x0F,
						(byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03,
						(byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x00, (byte) 0x96 }, riskManData, (byte) 1,
				(byte) 100000, (byte) 1, (byte) 20000, (byte) 100000,
				(byte) 5000);
		return app;
	}

	public static void add_VSDC_APP3() {
		byte[] AID1 = {(byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x03};
		byte AidLen1 = 8;
		byte[] Version1 = { (byte) 0x00, (byte) 0x96 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_VSDC_APP3() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x10, (byte) 0x10, (byte) 0x03 }, (byte) 8, PART_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				2000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
						(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
						(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
						(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x00, (byte) 0x96 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_VSDC_APP4() {
		byte[] AID1 = {(byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x04};
		byte AidLen1 = 8;
		byte[] Version1 = { (byte) 0x00, (byte) 0x96 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
	
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_VSDC_APP4() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x10, (byte) 0x10, (byte) 0x04 }, (byte) 8, PART_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				2000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
						(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
						(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
						(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x00, (byte) 0x96 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_VSDC_APP5() {
		byte[] AID1 = {(byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x05};
		byte AidLen1 = 8;
		byte[] Version1 = { (byte) 0x00, (byte) 0x96 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_VSDC_APP5() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x10, (byte) 0x10, (byte) 0x05 }, (byte) 8, PART_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				2000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
						(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
						(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
						(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x00, (byte) 0x96 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_VSDC_APP6() {
		byte[] AID1 = {(byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x06};
		byte AidLen1 = 8;
		byte[] Version1 = { (byte) 0x00, (byte) 0x96 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_VSDC_APP6() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x10, (byte) 0x10, (byte) 0x06 }, (byte) 8, PART_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				2000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
						(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
						(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
						(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x00, (byte) 0x96 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_VSDC_APP7() {
		byte[] AID1 = {(byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x10, (byte) 0x10, (byte) 0x07};
		byte AidLen1 = 8;
		byte[] Version1 = { (byte) 0x00, (byte) 0x96 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_VSDC_APP7() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
				(byte) 0x10, (byte) 0x10, (byte) 0x07 }, (byte) 8, FULL_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				2000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
						(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
						(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
						(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x00, (byte) 0x96 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_TEST_APP() {
		byte[] AID1 = { (byte) 0xd1, (byte) 0x56, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01};
		byte AidLen1 = 7;
		byte[] Version1 = { (byte) 0x00, (byte) 0x01 };
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_TEST_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xd1,
				(byte) 0x56, (byte) 0x00, (byte) 0x00, (byte) 0x01,
				(byte) 0x01, (byte) 0x01 }, (byte) 7, PART_MATCH, (byte) 0,
				(byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1, 2000,
				(byte) 0, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34,
						(byte) 0x56 }, new byte[] { (byte) 0x03, (byte) 0x9F,
						(byte) 0x37, (byte) 0x04 }, new byte[] { (byte) 0x0F,
						(byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03,
						(byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x00, (byte) 0x01 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_EMV_TEST2_APP() {
		byte[] AID1 = { (byte) 0xA0, (byte) 0x00, (byte) 0x00,(byte) 0x99, (byte) 0x99, (byte) 0x01};
		byte AidLen1 = 6;
		byte[] Version1 = {(byte) 0x99, (byte) 0x99};
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_EMV_TEST2_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0, (byte) 0x00,
				(byte) 0x00, (byte) 0x99, (byte) 0x99, (byte) 0x01 }, (byte) 6,
				PART_MATCH, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1,
				(byte) 1, 2000, (byte) 0, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x12, (byte) 0x34, (byte) 0x56 }, new byte[] {
						(byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x0F, (byte) 0x9F, (byte) 0x02,
						(byte) 0x06, (byte) 0x5F, (byte) 0x2A, (byte) 0x02,
						(byte) 0x9A, (byte) 0x03, (byte) 0x9C, (byte) 0x01,
						(byte) 0x95, (byte) 0x05, (byte) 0x9F, (byte) 0x37,
						(byte) 0x04 }, new byte[] { (byte) 0x99, (byte) 0x99 },
				riskManData, ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static EMV_APPLIST get_JCB_TEST2_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x65,
				(byte) 0x10, (byte) 0x10 }, (byte) 7, PART_MATCH, (byte) 0,
				(byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1, 2000,
				(byte) 0, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34,
						(byte) 0x56 }, new byte[] { (byte) 0x03, (byte) 0x9F,
						(byte) 0x37, (byte) 0x04 }, new byte[] { (byte) 0x0F,
						(byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03,
						(byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x02, (byte) 0x00 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_MASTER_TEST_APP() {
		byte[] AID1 = { (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x10, (byte) 0x10};
		byte AidLen1 = 7;
		byte[] Version1 = {(byte) 0x00, (byte) 0x02};
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_MASTER_TEST_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
				(byte) 0x10, (byte) 0x10 }, (byte) 7, PART_MATCH, (byte) 0,
				(byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1, 2000,
				(byte) 0, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34,
						(byte) 0x56 }, new byte[] { (byte) 0x03, (byte) 0x9F,
						(byte) 0x37, (byte) 0x04 }, new byte[] { (byte) 0x0F,
						(byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03,
						(byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x00, (byte) 0x02 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_AMEX_TEST_APP() {
		byte[] AID1 = { (byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x25,(byte) 0x01, (byte) 0x05, (byte) 0x01 };
		byte AidLen1 = 8;
		byte[] Version1 = {(byte) 0x00, (byte) 0x01};
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_AMEX_TEST_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x25,
				(byte) 0x01, (byte) 0x05, (byte) 0x01 }, (byte) 8, PART_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				20000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x00, (byte) 0x01 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_DISCOVER_TEST_APP() {
		byte[] AID1 = { (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x52, (byte) 0x30, (byte) 0x10};
		byte AidLen1 = 7;
		byte[] Version1 = {(byte) 0x00, (byte) 0x01};
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_DISCOVER_TEST_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x52,
				(byte) 0x30, (byte) 0x10 }, (byte) 7, PART_MATCH, (byte) 0,
				(byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1, 2000,
				(byte) 0, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34,
						(byte) 0x56 }, new byte[] { (byte) 0x03, (byte) 0x9F,
						(byte) 0x37, (byte) 0x04 }, new byte[] { (byte) 0x0F,
						(byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03,
						(byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x00, (byte) 0x01 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		return app;
	}

	public static void add_CUP_TEST_APP() {
		byte[] AID1 = { (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x33, (byte) 0x01, (byte) 0x01, (byte) 0x02};
		byte AidLen1 = 8;
		byte[] Version1 = {(byte) 0x00, (byte) 0x30};
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);
		
		EmvLib_AddApp(app);
	}
	
	public static EMV_APPLIST get_CUP_TEST_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x33,
				(byte) 0x01, (byte) 0x01, (byte) 0x02 }, (byte) 8, PART_MATCH,
				(byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 1, (byte) 1,
				2000, (byte) 0, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00 }, new byte[] { (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00 }, new byte[] {
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12,
						(byte) 0x34, (byte) 0x56 }, new byte[] { (byte) 0x03,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x0F, (byte) 0x9F, (byte) 0x02, (byte) 0x06,
						(byte) 0x5F, (byte) 0x2A, (byte) 0x02, (byte) 0x9A,
						(byte) 0x03, (byte) 0x9C, (byte) 0x01, (byte) 0x95,
						(byte) 0x05, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },
				new byte[] { (byte) 0x00, (byte) 0x30 }, riskManData,
				ecTermLimitCheck, ecTermLimit, clStatusCheck,
				clFloorLimit, clTransLimit, clCVMLimit);
		
		return app;
	}

	public static void add_PBOC_TEST_APP() {
		byte[] AID1 = {(byte) 0xA0,(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x33, (byte) 0x01, (byte) 0x01 };
		byte AidLen1 = 7;
		byte[] Version1 = {(byte) 0x00, (byte) 0x30};
		
		EMV_APPLIST app = new EMV_APPLIST(AID1, AidLen1,PART_MATCH, priority,
				targetPer, maxTargetPer, floorLimitCheck,randTransSel, velocityCheck,
				floorLimit, threshold, tacDenial,tacOnline, tacDefault,acquierID,
				dDOL,tDOL, Version1,riskManData, ecTermLimitCheck, ecTermLimit,
				clStatusCheck,clFloorLimit, clTransLimit, clCVMLimit);

		EmvLib_AddApp(app);
		
	}
	
	public static EMV_APPLIST get_PBOC_TEST_APP() {
		EMV_APPLIST app = new EMV_APPLIST(new byte[] { (byte) 0xA0,
				(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x33,
				(byte) 0x01, (byte) 0x01 }, (byte) 7, PART_MATCH, (byte) 0,
				(byte) 0, (byte) 1, (byte) 1, (byte) 1, (byte) 1, 2000,
				(byte) 0, new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00,
						(byte) 0x00, (byte) 0x00 }, new byte[] { (byte) 0x00,
						(byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34,
						(byte) 0x56 }, new byte[] { (byte) 0x03, (byte) 0x9F,
						(byte) 0x37, (byte) 0x04 }, new byte[] { (byte) 0x0F,
						(byte) 0x9F, (byte) 0x02, (byte) 0x06, (byte) 0x5F,
						(byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03,
						(byte) 0x9C, (byte) 0x01, (byte) 0x95, (byte) 0x05,
						(byte) 0x9F, (byte) 0x37, (byte) 0x04 }, new byte[] {
						(byte) 0x00, (byte) 0x30 }, riskManData, (byte) 1,
				100000, (byte) 1, 20000, 100000, 5000);
		return app;
	}
	
	public static EMV_APPLIST get_EMV_TEST_ANOD_Ex() {
		EMV_APPLIST app = new EMV_APPLIST(
				new byte[] { (byte) 0xA1, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55 },//AID
				(byte) 5,//aidLen
				PART_MATCH,//selFlag
				(byte) 0,//priority
				(byte) 0,//targetPer
				(byte) 0,//maxTargetPer
				(byte) 1,//floorLimitCheck
				(byte) 1,//randTransSel
				(byte) 1,//velocityCheck
				2000,//floorLimit
				(byte) 0,//threshold
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },//tacDenial
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },//tacOnline
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },//tacDefault
				new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56 },//acquierID
				new byte[] { (byte) 0x03, (byte) 0x9F, (byte) 0x37, (byte) 0x04 },//dDOL
				new byte[] { (byte) 0x0F, (byte) 0x9F, (byte) 0x02,	(byte) 0x06, (byte) 0x5F,
							 (byte) 0x2A, (byte) 0x02, (byte) 0x9A, (byte) 0x03, (byte) 0x9C,
							 (byte) 0x01, (byte) 0x95, (byte) 0x05, (byte) 0x9F, (byte) 0x37,
							 (byte) 0x04 },//tDOL
				new byte[] { (byte) 0x12, (byte) 0x34 },//version
				riskManData,
				ecTermLimitCheck,
				ecTermLimit,
				clStatusCheck,
				clFloorLimit,
				clTransLimit,
				clCVMLimit);
		
		return app;
		
	}
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
/*
	public static void setDefaultConfigEx() {
		AppElement tmpAE = new AppElement();
		// 1. VISA Credit or Debit
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[]{(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x10, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'V';
		tmpAE.version = new byte[]{(byte)0x02, (byte)0x00, (byte)0x84};
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0x58, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0x58, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00};
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x84};
		tmpAE.selFlag = 0x01;
		setCommonConfig(tmpAE);
		aE[VISA_CREDIT] = tmpAE;

		tmpAE = new AppElement();
		// 2 Visa Electron
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[]{(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x20, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'V';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x84};
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0x58, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0x58, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[VISA_ELECTRON] = tmpAE;
		
		tmpAE = new AppElement();
		// 3 Visa Interlink :: Oct.14.2017(add-mskl)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x30, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'V';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x84};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0x58, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0x58, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[VISA_INTERLINK] = tmpAE;
		
		tmpAE = new AppElement();
		// 4 MASTER Card (credit)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x10, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'M';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x02};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[MASTER_CREDIT] = tmpAE;
		
		tmpAE = new AppElement();
		// 5 MASTER Card (debit)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x30, (byte)0x60, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'M';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x02};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[MASTER_DEBIT] = tmpAE;
		
		tmpAE = new AppElement();
		// 6 MASTER Card (Cirrus)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x60, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'M';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x02};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[MASTER_CIRRUS] = tmpAE;
		
		tmpAE = new AppElement();
		// 7 JCB Card(smart credit)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x65, (byte)0x10, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'J';
		tmpAE.version = new byte[]{(byte)0x02, (byte)0x00};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xFC, (byte)0x60, (byte)0xAC, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFC, (byte)0x60, (byte)0x24, (byte)0x28, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[JCB_CREDIT] = tmpAE;
		
		tmpAE = new AppElement();
		// 8 Local Card 1 : Visa
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xD4, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x10, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'V';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x84};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[LOCAL_VISA] = tmpAE;
		
		tmpAE = new AppElement();
		// 9 Local Card 2 : Master
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xD4, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x20, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'M';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x02};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[LOCAL_MASTER] = tmpAE;
		
		tmpAE = new AppElement();
		// 10 Local Card 3 : Debit
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xD4, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x20, (byte)0x20, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'V';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x84};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFE, (byte)0x50, (byte)0x80, (byte)0xA0, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[LOCAL_DEBIT] = tmpAE;
		
		tmpAE = new AppElement();
		// 11 American Express
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x25, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 6;
		tmpAE.brand = 'A';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x01};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xCC, (byte)0x00, (byte)0xFC, (byte)0x80, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xFC, (byte)0x50, (byte)0xFC, (byte)0x20, (byte)0x00};
		tmpAE.selFlag = 0x01;
		setCommonConfig(tmpAE);
		aE[AMEX_EXP] = tmpAE;

		tmpAE = new AppElement();
		// 12 DPAS
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x52, (byte)0x30, (byte)0x10, (byte)0x00, (byte)0x00};
		tmpAE.aidLen = 7;
		tmpAE.brand = 'D';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x01};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xCC, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xCC, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[DISCOVER_CARD] = tmpAE;
		
		tmpAE = new AppElement();
		// 13 - UICC(UionPay Debit)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x33, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x00};
		tmpAE.aidLen = 8;
		tmpAE.brand = 'C';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x20};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[UICC_DEBIT] = tmpAE;
		
		tmpAE = new AppElement();
		// 14 - UICC(UionPay Credit)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x33, (byte)0x01, (byte)0x01, (byte)0x02, (byte)0x00};
		tmpAE.aidLen = 8;
		tmpAE.brand = 'C';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x20};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[UICC_CREDIT] = tmpAE;
		
		tmpAE = new AppElement();
		// 15 - UICC(UionPay Quasi)
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x03, (byte)0x33, (byte)0x01, (byte)0x01, (byte)0x03, (byte)0x00};
		tmpAE.aidLen = 8;
		tmpAE.brand = 'C';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x20};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[UICC_QUASI] = tmpAE;
		
		tmpAE = new AppElement();
		// 16 - 코나머니
		setDefaultValue((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, 2000, (byte)0, tmpAE);
		tmpAE.AID = new byte[] {(byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x51, (byte)0x40, (byte)0x25, (byte)0x26, (byte)0x00};
		tmpAE.aidLen = 6;
		tmpAE.brand = 'V';
		tmpAE.version = new byte[]{(byte)0x00, (byte)0x84};;
		tmpAE.tacDenial = new byte[]{(byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00};
		tmpAE.tacOnline = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x04, (byte)0xF8, (byte)0x00};
		tmpAE.tacDefault = new byte[]{(byte)0xD8, (byte)0x40, (byte)0x00, (byte)0xA8, (byte)0x00};
		tmpAE.selFlag = (byte)0x01;
		setCommonConfig(tmpAE);
		aE[CONA_MOMEY] = tmpAE;
	}
*/
}
