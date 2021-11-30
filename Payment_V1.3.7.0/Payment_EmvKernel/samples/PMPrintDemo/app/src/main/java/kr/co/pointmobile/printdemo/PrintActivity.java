package kr.co.pointmobile.printdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;

import java.util.concurrent.TimeUnit;

import vpos.apipackage.Print;
import vpos.messenger.MessengerClient;

public class PrintActivity extends AppCompatActivity
{
	private static final String    TAG = PrintActivity.class.getSimpleName();

	////////////////////////////////////////////////////////////////////////////////////////////////
	private final int PRINT_TEXT=0;
	//	public static final int PRINT_UNICODE=1;
	private final int PRINT_BATTERY_LIFE = 1;
	private final int PRINT_LOGO = 2;
	private final int PRINT_BLOCK = 3;
	private final int PRINT_BARCODE = 4;
	private final int PRINT_SPEED = 5;
	private final int LINE_FEED = 6;
	private final int PRINT_STRESS = 7;

	private final int MIN_GRAY_VALUE = 1;
	private final int MAX_GRAY_VALUE = 6;

	private final int MIN_FONT_SIZE = 0;
	private final int MAX_FONT_SIZE = 3;

	private final int ERROR_NOPAPER = -1;
	private final int ERROR_TO_HOT = -2;
	private final int ERROR_BUSY = -4001;
	private final int ERROR_LACK_PAPER = -4002;
	private final int ERROR_FORMAT_ERROR = -4003;
	private final int ERROR_BROKEN = -4004;
	private final int ERROR_TOHOT = -4005;
	private final int ERROR_UNFINISHED = -4006;
	private final int ERROR_NO_FONT_LIBRARY = -4007;
	private final int ERROR_BUFFER_OVERFLOW = -4008;
	private final int ERROR_LOW_BATTERY = -4011;
	private final int ERROR_TEMPERATURE_LOW = -4012;
	private final int ERROR_OTHER = -1001;

	private final int ERROR_INVALIDATE_GRAY_VALUE = 0x7001;
	private final int ERROR_INVALIDATE_FONT_SIZE = 0x7002;
	private int SUCCESS = 0;
	////////////////////////////////////////////////////////////////////////////////////////////////
	public Print   printer = null;

	private static boolean  isOut = false;
	public static int       grayValue = 4;// default: 4

	public PrintThread  prtThread = null;
	public EditText     editGreyVal;
	public EditText     editLines;
	private EditText mEditHours;
	public TextView     textMsgView;

	public Button       btnBatteryLT;   // battery life test
	public Button       btnPrintST;     // printing speed test

	private EditText mEditTextMessage;
	private EditText mEditFontSize;

	private boolean mStressState = false;
	private Button mBtnStressTest;

	private boolean m_bThreadFinished = true;
	public int btnBLTStatus = 0;    // Battery Life Test status
	public boolean fBLT = false;      // Battery Life Test Flag
	private PowerManager.WakeLock mWakeLock;

	private ProgressDialog mProgress;
	MessengerClient mClient = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar aBar = getSupportActionBar();

		aBar.setIcon(R.drawable.ic_launcher);
		aBar.setDisplayUseLogoEnabled(true);
		aBar.setDisplayShowHomeEnabled(true);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, getClass().getName());

		init();

        mProgress = new ProgressDialog(PrintActivity.this);
		mClient = MessengerClient.getInstance(getApplicationContext());
		mClient.init();

		showProgress(PrintActivity.this, R.string.initializing, true);
		AsyncMasterConnect async = new AsyncMasterConnect();
		async.execute();
	}
	
	private void init() {
		btnBatteryLT = findViewById(R.id.btnBatteryLifeTest);
		btnBatteryLT.setOnClickListener(mOnClickListener);
		btnPrintST = findViewById(R.id.btnSpeedTest);
		btnPrintST.setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_TextPrint).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_LogoPrint).setOnClickListener(mOnClickListener);
		findViewById(R.id.button_barcode).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_LineFeed).setOnClickListener(mOnClickListener);
		findViewById(R.id.btnMsgClear).setOnClickListener(mOnClickListener);
		findViewById(R.id.btn_Quit).setOnClickListener(mOnClickListener);
		mBtnStressTest = findViewById(R.id.btn_stress_test_start);
		mBtnStressTest.setOnClickListener(mOnClickListener);

		editGreyVal = findViewById(R.id.et_SetGray);
		editLines = findViewById(R.id.et_LineFeed);
		mEditHours = findViewById(R.id.edit_time);

		mEditTextMessage = (EditText)findViewById(R.id.edit_textprint);
		mEditFontSize = (EditText)findViewById(R.id.edit_fontsize);

		textMsgView = findViewById(R.id.textView_msg);
//		textMsgView.setMaxLines(11);
		textMsgView.setVerticalScrollBarEnabled(true);
		textMsgView.setMovementMethod(new ScrollingMovementMethod());
		
		printer = new Print();
		printer.Lib_PrnInit();

		editGreyVal.setText("4");
		editLines.setText("3");
		mEditHours.setText("72");
		mEditFontSize.setText("1");

		editGreyVal.addTextChangedListener(mGrayTextWatcher);
		mEditFontSize.addTextChangedListener(mFontTextWatcher);

		return;
	}

    @Override
    protected void onDestroy() {
        mClient.close();
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
	// Buttons +++++++++++

	private TextWatcher mGrayTextWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s)
		{
			String text = s.toString();
			if(text != null && text.length() == 1)
			{
				try{
					int value = Integer.parseInt(text);
					if(value > MAX_GRAY_VALUE || value < MIN_GRAY_VALUE)
					{
						editGreyVal.setText("");
						showErrorDialog(ERROR_INVALIDATE_GRAY_VALUE);
					}
				}catch(Exception e)
				{
					showErrorDialog(ERROR_INVALIDATE_GRAY_VALUE);
					editGreyVal.setText("");
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
		}
	};

	private TextWatcher mFontTextWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s)
		{
			String text = s.toString();
			if(text != null && text.length() == 1)
			{
				try{
					int value = Integer.parseInt(text);
					if(value > MAX_FONT_SIZE || value < MIN_FONT_SIZE)
					{
						mEditFontSize.setText("");
						showErrorDialog(ERROR_INVALIDATE_FONT_SIZE);
					}
				}catch(Exception e)
				{
					mEditFontSize.setText("");
					showErrorDialog(ERROR_INVALIDATE_FONT_SIZE);
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
		}
	};

	private View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

			if(v.getId() == R.id.btn_TextPrint)
			{
				startPrint(PRINT_TEXT);
			}
			else if(v.getId() == R.id.btn_LogoPrint)
			{
				startPrint(PRINT_LOGO);
			}
			else if(v.getId() == R.id.button_barcode)
			{
				startPrint(PRINT_BARCODE);
			}
			else if(v.getId() == R.id.btn_LineFeed)
			{
				startPrint(LINE_FEED);
			}
			else if(v.getId() == R.id.btnSpeedTest)
			{
				if(btnBLTStatus >= 1)
				{
					btnPrintST.setText("Printing Speed Test");
					btnBatteryLT.setText("Battery Life Test");
					btnBLTStatus = 0;
					fBLT = false;
					enableButton(true);
					return;
				}

				startPrint(PRINT_SPEED);
			}
			else if(v.getId() == R.id.btnBatteryLifeTest)
			{
				switch(btnBLTStatus)
				{
					case 0:
						int result = startPrint(PRINT_BATTERY_LIFE);
						if(result == SUCCESS)
						{
							btnBatteryLT.setText("PAUSE");
							btnPrintST.setText("STOP");
							btnBLTStatus = 1;
							fBLT = true;
						}
						break;
					case 1:
						btnBatteryLT.setText("CONTINUE");
						btnBLTStatus = 2;
						break;
					case 2:
						btnBatteryLT.setText("PAUSE");
						btnBLTStatus = 1;
						break;
					default:
						btnBLTStatus = 0;
						return;
				}
			}
			else if(v.getId() == R.id.btn_stress_test_start)
			{
				if(!mStressState)
				{
					mStressState = true;
					mBtnStressTest.setText(R.string.stress_test_stop);

					int result = startPrint(PRINT_STRESS);
				}
				else
				{
					mStressState = false;
					mBtnStressTest.setText(R.string.stress_test);
				}
			}
			else if(v.getId() == R.id.btnMsgClear)
			{
				textMsgView.setText("");
			}
			else if(v.getId() == R.id.btn_Quit)
			{
				if(mStressState)
				{
					mStressState = false;
				}
				else if (m_bThreadFinished)
				{
					finish();
				}

			}

		}
	};

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			enableButton(true);
		}
	};
	
	public void enableButton(boolean enable) {
		findViewById(R.id.btn_LineFeed).setEnabled(enable);
		findViewById(R.id.btn_LogoPrint).setEnabled(enable);
		findViewById(R.id.btn_TextPrint).setEnabled(enable);
		findViewById(R.id.button_barcode).setEnabled(enable);

		findViewById(R.id.btnBatteryLifeTest).setEnabled(enable);
		findViewById(R.id.btnSpeedTest).setEnabled(enable);
		mBtnStressTest.setEnabled(enable);

		if(enable == true) {
			btnPrintST.setText("Printing Speed Test");
			btnBatteryLT.setText("Battery Life Test");
			mBtnStressTest.setText(R.string.stress_test);
		}

		return;
	}
	
	public int startPrint(int type)
	{
		int ret = Print.Lib_PrnCheckStatus();
		if(ret != SUCCESS)
		{
			showErrorDialog(ret);
			return ERROR_OTHER;
		}

		if (prtThread != null && prtThread.isThreadFinished() == false) {
			return ERROR_OTHER;
		}

		///////////////////////////////////////////////////////////
		enableButton(false);
		if(type == PRINT_BATTERY_LIFE)
		{
			findViewById(R.id.btnBatteryLifeTest).setEnabled(true);
			findViewById(R.id.btnSpeedTest).setEnabled(true);
		}
		else if(type == PRINT_STRESS)
		{
			mBtnStressTest.setEnabled(true);
		}
		////////////////////////////////////////////////////////////

		if(type == LINE_FEED)
		{
			prtThread = new PrintThread(type);

		}
		else if(type == PRINT_STRESS)
		{
			prtThread = new PrintThread(type);
			int duration = 72;
			String strDuration = mEditHours.getText().toString();
			if(strDuration == null || strDuration.length() == 0) {
				duration = 72;
			} else {
				duration = Integer.parseInt(strDuration);
			}
			prtThread.setDuration(duration);
		}
		else
		{
			String  greyS = editGreyVal.getText().toString();

			if(greyS == null || greyS.length() == 0) {
				grayValue = 4;
			} else {
				grayValue = Integer.parseInt(greyS);
			}

			prtThread = new PrintThread(type, grayValue);
		}
		prtThread.start();

		return SUCCESS;
	}

	private void showLogState(final String strLog, final boolean isClear)
	{
		PrintActivity.this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(isClear)
					textMsgView.setText("");

				//textMsgView.setText(strLog);
				textMsgView.append(strLog);

			}
		});
	}
	////////////////////////////////////////////////////////////////////////////////////////////////

	
	public class PrintThread extends Thread
	{
		int     type;
		byte    grayVal = 2;
		int duration = 0;

		public PrintThread(int type)
		{
			this.type = type;
		}

		public PrintThread(int type, int grayVal)
		{
			this.type = type;
			this.grayVal = (byte)grayVal;
		}

		public boolean isThreadFinished()
		{
			return m_bThreadFinished;
		}

		public int getDuration()
		{
			return duration;
		}

		public void setDuration(int duration)
		{
			this.duration = duration;
		}

		public void run()
		{
			// method 안의 code 동기화
			synchronized (this) {
				runable(type);
				m_bThreadFinished = true;
			}
		}

		private byte getFontSize()
		{
			try{
				String strFont = mEditFontSize.getText().toString();
				byte size = Byte.parseByte(strFont) ;
				size *= 0x10;
				return size;
			}catch (Exception e)
			{
				return -1;
			}
		}

		public void runable(int type)
		{
			int ret = SUCCESS;
			ret = Print.Lib_PrnInit();

			switch (type)
			{
				case PRINT_TEXT:

					textPrintTest();

					break;

				case PRINT_LOGO:
					Bitmap bitmap = BitmapFactory.decodeResource(PrintActivity.this.getResources(), R.drawable.android_logo);
					if(bitmap == null)
					{
						break;
					}

					if ((ret = printer.Lib_PrnBmp(bitmap)) == 0)
					{
						if((ret = Print.Lib_PrnStart()) == 0)
						{
							lineFeed(5, true);// true=즉시 feed
						}
					}
					break;

				case PRINT_BARCODE: {
					barcordPrint();
					lineFeed(5, true);// true=즉시 feed
					break;
				}

				case LINE_FEED:
					String  lineS = editLines.getText().toString();
					int     lines;
					
					if(lineS == null || lineS.length() == 0) {
						lines = 4;
					} else {
						lines = Integer.parseInt(lineS);
					}
					lineFeed(lines, true);// true=즉시 feed
					break;

//              android:visibility="gone"
				case PRINT_BATTERY_LIFE:
					ret = batteryTest();
					break;//switch break;

				case PRINT_SPEED:
					ret = speedTest();
					break;

				case PRINT_STRESS:
					ret = stressTest();
					break;

				default:
					break;
			}

			////////////////////
			Message msg = Message.obtain();
			handler.sendMessage(msg);
			////////////////////
		}

		private int barcordPrint()
		{
			int ret = SUCCESS;
			String content = "1234567890";

			ret = Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 24, (byte) 24, (byte) 0x00);
			Print.Lib_PrnSetGray((byte) 2);
			printer.Lib_PrnBarcode(content, 360, 120, BarcodeFormat.CODE_128);
			Print.Lib_PrnStr("CODE_128 : " + content + "\n\n");
			printer.Lib_PrnBarcode(content, 360, 120, BarcodeFormat.CODE_39);
			Print.Lib_PrnStr("CODE_39 : " + content + "\n\n");
			ret = Print.Lib_PrnStart();
			Log.d("", "Lib_PrnStart ret = " + ret);

			delayMS(300);

			Print.Lib_PrnInit();
			ret = Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 24, (byte) 24, (byte) 0x00);
			content = "12345670";

			try{
				printer.Lib_PrnBarcode(content, 360, 120, BarcodeFormat.EAN_8);
				Print.Lib_PrnStr("EAN_8 : " + content + "\n\n");
			}catch (Exception e)
			{
				e.printStackTrace();
			}

			printer.Lib_PrnBarcode(content, 240, 240, BarcodeFormat.QR_CODE);
			Print.Lib_PrnStr("QR_CODE : " + content + "\n\n");
			ret = Print.Lib_PrnStart();

			Log.d("", "Lib_PrnStart ret = " + ret);

			return ret;
		}

		private int speedTest()
		{
			int ret = SUCCESS;
			String time;

			ret = Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 16, (byte) 24, (byte) 0x00);
			Print.Lib_PrnSetGray(grayVal);

			long start = System.currentTimeMillis();
			Print.Lib_PrnStr("----------------------------------------\n");
			Print.Lib_PrnStr("16 X 16 : 0x00\n");
			Print.Lib_PrnStr("中国银联直连测试\n");
			Print.Lib_PrnStr("商户编号(MERCHANT NO):\n");
			Print.Lib_PrnStr("   001420183990573\n");
			Print.Lib_PrnStr("终端编号(TERMINAL NO):0002671\n");
			Print.Lib_PrnStr("OPERATOR NO:12345678\n");
			Print.Lib_PrnStr("우리나라대한민국우리나라대한민국\n");
			Print.Lib_PrnStr("우리나라대한민국우리나라대한민국\n");
			Print.Lib_PrnStr("99999999999999999999999999999\n");
			Print.Lib_PrnStr("99999999999999999999999999999\n");
			Print.Lib_PrnStr("00000000000000000000000000000\n");
			Print.Lib_PrnStr("00000000000000000000000000000\n");
			Print.Lib_PrnStr("12345678901234567890123456789\n");
			Print.Lib_PrnStr("12345678901234567890123456789\n");
			Print.Lib_PrnStr("1234567890123456789012345678901234567890123456789012345678\n");
			Print.Lib_PrnStr("1234567890123456789012345678901234567890123456789012345678\n");
			Print.Lib_PrnStr("가나다라마바사아자차카타파하가갸\n");
			Print.Lib_PrnStr("가나다라마바사아자차카타파하가갸\n");
			Print.Lib_PrnStr("나냐너녀노뇨누뉴느니다댜더뎌도됴두듀드디라랴러려로료루류르리마먀머며모묘무뮤므미바뱌버벼보뵤부뷰\n");
			Print.Lib_PrnStr("나냐너녀노뇨누뉴느니다댜더뎌도됴두듀드디라랴러려로료루류르리마먀머며모묘무뮤므미바뱌버벼보뵤부뷰\n");
			Print.Lib_PrnStr("16 X 16 : 0x00\n");
			Print.Lib_PrnStr("中国银联直连测试\n");
			Print.Lib_PrnStr("商户编号(MERCHANT NO):\n");
			Print.Lib_PrnStr("   001420183990573\n");
			Print.Lib_PrnStr("终端编号(TERMINAL NO):0002671\n");
			Print.Lib_PrnStr("OPERATOR NO:12345678\n");
			Print.Lib_PrnStr("우리나라대한민국우리나라대한민국\n");
			Print.Lib_PrnStr("우리나라대한민국우리나라대한민국\n");
			Print.Lib_PrnStr("99999999999999999999999999999\n");
			Print.Lib_PrnStr("99999999999999999999999999999\n");
			Print.Lib_PrnStr("00000000000000000000000000000\n");
			Print.Lib_PrnStr("00000000000000000000000000000\n");
			Print.Lib_PrnStr("12345678901234567890123456789\n");
			Print.Lib_PrnStr("12345678901234567890123456789\n");
			Print.Lib_PrnStr("1234567890123456789012345678901234567890123456789012345678\n");
			Print.Lib_PrnStr("1234567890123456789012345678901234567890123456789012345678\n");
			Print.Lib_PrnStr("abcdefghijklmnopqrstuvwxyz\n");
			Print.Lib_PrnStr("ABCDEFGHIJKLMNOPQRSTUVW\n");
			Print.Lib_PrnStr("ABCDEFGHIJKLMNOPQRSTUVW\n");
			Print.Lib_PrnStr("----------------------------------------\n");


			if ((ret = startPrint()) != 0)
			{
				lineFeed(1, true);
				return ret;
			}

			long end = System.currentTimeMillis();

			//////////////////////////////////////////////////////////////////////////////////////////////////
			Print.Lib_PrnInit();
			ret = Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 16, (byte) 24, (byte) 0x20);
			Print.Lib_PrnSetGray(grayVal);

			lineFeed(3, false);

			Print.Lib_PrnStr("----------------------------------------\n");
			time = "Start = " + start + "\n";
			Print.Lib_PrnStr(time);

			time = "End = " + end + "\n";
			Print.Lib_PrnStr(time);

			end -= start;
			time = "print time = " + end;
			Print.Lib_PrnStr(time);

			long speed = 144000 / end;
			time = "speed = " + (int) speed + " mm/s";
			Print.Lib_PrnStr(time);
			Print.Lib_PrnStr("----------------------------------------\n");

			startPrint();
			lineFeed(5, true);

			return ret;
		}

		private int batteryTest()
		{
			int loopCount = 2500;
			int ret = SUCCESS;
			int i = 1;
			int chkCount = 0;
			mWakeLock.acquire();

			while (i <= loopCount)
			{
				if (fBLT == false)
				{
					break;
				}

				if (btnBLTStatus == 1)
				{
					Print.Lib_PrnInit();
					Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 16, (byte) 24, (byte) 0x00);
					Print.Lib_PrnSetGray(grayVal);

					// 20 CHAR & 20 LINE = 1 PAGE
					ret = Print.Lib_PrnStr("DDDDDDDDDDDDDDDDDDDD\n");
					if(ret != 0)
						continue;
					for (int j = 0; j < 9; j++)
					{
						ret = Print.Lib_PrnStr("QQQQQQQQQQQQQQQQQQQQ\n");
						if(ret != 0)
							break;
						ret = Print.Lib_PrnStr("BBBBBBBBBBBBBBBBBBBB\n");
						if(ret != 0)
							break;
					}
					if(ret != 0)
						continue;

					ret = Print.Lib_PrnStr("page = " + i + " =================\n\n");
					if(ret != 0)
						continue;

					/////////////////////
					ret = startPrint();
					if(ret != 0)
						continue;
					/////////////////////

					if (!fBLT)
					{
						lineFeed(5, true);
						break;
					}

					i++;
				}
				else if (btnBLTStatus == 2)
				{ // waiting
					delayMS(100);
				}
				showLogState("Print Page : "+i, true);
			}
			mWakeLock.release();
			btnBLTStatus = 0;

			return ret;
		}

		private int stressTest()
		{
			int ret = SUCCESS;
			long startTime = System.currentTimeMillis();
			long checkTime = 0;
			int printPage = 1;
			mWakeLock.acquire();

			while(true)
			{
				ret = Print.Lib_PrnInit();
				Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 16, (byte) 24, (byte) 0x00);
				Print.Lib_PrnSetGray(grayVal);

				// 20 CHAR & 20 LINE = 1 PAGE
				Print.Lib_PrnStr("DDDDDDDDDDDDDDDDDDDD\n");
				for (int j = 0; j < 9; j++)
				{
					Print.Lib_PrnStr("QQQQQQQQQQQQQQQQQQQQ\n");
					Print.Lib_PrnStr("BBBBBBBBBBBBBBBBBBBB\n");
				}
				Print.Lib_PrnStr("Loop Count = " + printPage + " ===========\n\n");

				while ((ret = Print.Lib_PrnCheckStatus()) != 0 && mStressState)
				{
					String message = getErrorMessage(ret);
					showLogState(message + "\n", false);
					Log.d("AAAA", "Print State : " + ret);
					delayMS(2000);

					checkTime = System.currentTimeMillis();
					long diff = checkTime - startTime;
					int hours = (int)TimeUnit.MILLISECONDS.toHours(diff);
					if(hours == duration )
					{
						break;
					}
				}
				if(ret != 0)
				{
					showLogState("Time Over \n", false);
					break;
				}

				if(!mStressState)
				{
					lineFeed(5, true);
					break;
				}

				checkTime = System.currentTimeMillis();
				long diff = checkTime - startTime;
				int hours = (int)TimeUnit.MILLISECONDS.toHours(diff);
				int minutes = (int)((diff / 1000) / 60 % 60);
				int seconds = (int)((diff / 1000) % 60);
				String strTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				Print.Lib_PrnStr(strTime +"\n");

				startPrint();

				if(hours == duration || !mStressState)
				{
					lineFeed(5, true);
					break;
				}

				System.gc();

				delayMS(10);
				printPage++;

				showLogState("Print Loop Count : "+printPage + "\n", true);
			}
			mWakeLock.release();
			mStressState = false;

			return ret;
		}

		public int startPrint()
		{
			int ret=0;
			int chkCount=0;
			
			while((ret = Print.Lib_PrnCheckStatus()) != 0 && chkCount < 60)
			{
				delayMS(1000);
				chkCount++;
			}

			if(ret != 0) {
				return ret;
			}

			if((ret = Print.Lib_PrnStart()) != 0) {
			}
			
			return ret;
		}

		private int textPrintTest()
		{
			String message = mEditTextMessage.getText().toString();
			byte fontSize = getFontSize();
			if(fontSize == -1)
			{
				showLogState("Set font size \n", true);
				return -1;
			}
			if(message == null || message.length() < 1)
			{
				showLogState("Set Print message \n", true);
				//showLogState("Set Print message \n", false);
				return -1;
			}

			byte extSize = 16;
			byte zoom = 0x00;
			if(fontSize == 0)
			{
				extSize = 16;
				zoom = 0x00;
			}
			else if(fontSize == 1)
			{
				extSize = 24;
				zoom = 0x00;
			}
			else if(fontSize == 2)
			{
				extSize = 16;
				zoom = 0x33;
			}
			else if(fontSize == 3)
			{
				extSize = 24;
				zoom = 0x33;
			}


			int ret = Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 24, extSize, zoom);
			if(ret != 0)
			{
				showLogState("Parameter error \n", true);
				return ret;
			}
			Print.Lib_PrnSetGray(grayVal);
			lineFeed(1, false);
			Print.Lib_PrnStr(message + "\n");
			ret = startPrint();
			lineFeed(3, true);

			if(ret != 0)
			{
				ret = Print.Lib_PrnFeedPaper(3);
				ret = Print.Lib_PrnCheckStatus();
			}

			return ret;
		}

		public int textPrintTest(byte fontSize)
		{
//			Print.Lib_PrnInit();

			fontSize *= 0x10;
			int ret = Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 16, (byte) 24, fontSize);

			if(ret != 0) {
				return ret;
			}

			Print.Lib_PrnSetGray(grayVal);
			lineFeed(1, false);
			Print.Lib_PrnStr("16 X 24 : " + fontSize + "\n");
			Print.Lib_PrnStr("中国银联直连测试\n");
			Print.Lib_PrnStr("商户编号(MERCHANT NO):\n");
			Print.Lib_PrnStr("   001420183990573\n");
			Print.Lib_PrnStr("终端编号(TERMINAL NO):00026715\n");
			Print.Lib_PrnStr("OPERATOR NO:12345678\n");
			Print.Lib_PrnStr("우리나라대한민국우리나라대한민국\n");
			Print.Lib_PrnStr("9999999999999999999999999999999999999999\n");
			Print.Lib_PrnStr("0000000000000000000000000000000000000000\n");
			Print.Lib_PrnStr("1234567890123456789012345678901234567890\n");

			///////////////////////////
			ret = startPrint();

			if(fontSize >= 0x30) {// end of printing
				lineFeed(5, true);
			}
			else {
				lineFeed(1, true);
			}
			///////////////////////////

			if(ret != 0)
			{
				ret = Print.Lib_PrnFeedPaper(3);
				ret = Print.Lib_PrnCheckStatus();
			}

			return ret;
		}

		/**
		 * @param line : num of lines
		 * @param fOutPrn : immediately print out option
		 * @return
		 */
		public int lineFeed(int line, boolean fOutPrn)
		{
			int ret = SUCCESS;
			
			if(fOutPrn == true)
			{
				ret = Print.Lib_PrnInit();
				Print.Lib_PrnSetFont(getAssets(), "GCFontUnicode.bin", (byte) 16, (byte) 24, (byte) 0x20);
			}

			for(int i = 0; i < line; i++)
			{
				if((ret = printer.Lib_PrnStr(" \n")) < 0)
					break;
			}

//			Print.Lib_PrnSetSpace((byte)0, (byte)line);
			
			if(fOutPrn == true) {
				ret = Print.Lib_PrnStart();
			}
			
			return ret;
		}
	}
	
	void delayMS(int Ms)
	{
		try {
			Thread.sleep(Ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id)
		{
			case R.id.action_info:
				openInfo();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showErrorDialog(int type)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.error);
		alert.setPositiveButton(R.string.ok, null);
		alert.setMessage(getErrorMessage(type));
		alert.show();
	}

	private String getErrorMessage(int type)
	{
		String message = getString(R.string.error_other);
		if(type == ERROR_NOPAPER || type == ERROR_LACK_PAPER)
			message = getString(R.string.error_nopaper);
		else if(type == ERROR_TO_HOT || type == ERROR_TOHOT)
			message = getString(R.string.error_hot);
		else if(type == ERROR_BUSY)
			message = getString(R.string.error_busy);
//		else if(type == ERROR_LACK_PAPER)
//			message = getString(R.string.error_paper);
		else if(type == ERROR_FORMAT_ERROR)
			message = getString(R.string.error_format);
		else if(type == ERROR_BROKEN)
			message = getString(R.string.error_broken);
		else if(type == ERROR_UNFINISHED)
			message = getString(R.string.error_unfinished);
		else if(type == ERROR_NO_FONT_LIBRARY)
			message = getString(R.string.error_font);
		else if(type == ERROR_BUFFER_OVERFLOW)
			message = getString(R.string.error_overflow);
		else if(type == ERROR_LOW_BATTERY)
			message = getString(R.string.error_battery);
		else if(type == ERROR_TEMPERATURE_LOW)
			message = getString(R.string.error_temperature_low);
		else if(type == ERROR_INVALIDATE_GRAY_VALUE)
			message = getString(R.string.error_invalide_gray);
		else if(type == ERROR_INVALIDATE_FONT_SIZE)
			message = getString(R.string.error_invalide_font);

		return message;
	}

	private void openInfo()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		String version = getString(R.string.msg_version_suffix);
		try {
			PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			if (pi != null) {
				version = pi.versionName;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

//		String  version = "v1.0.0";
		alert.setMessage(getString(R.string.app_name) + version);
		alert.show();
	}


	public void showProgress(final Activity act, final int resId, final boolean bShow)
	{
		act.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgress.setMessage(getString(resId));
				mProgress.setCancelable(false);

				try
				{
					if (bShow)
					{
						mProgress.show();
					}
					else
					{
						mProgress.dismiss();
					}
				}
				catch (Exception e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}

	class AsyncMasterConnect extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			do{
				if(mClient.isConnect())
					break;

				try{
					Thread.sleep(1000);
				}catch (Exception e)
				{
				}

			}while(true);

			return true;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			showProgress(PrintActivity.this, R.string.initializing, false);
		}
	}

}


	