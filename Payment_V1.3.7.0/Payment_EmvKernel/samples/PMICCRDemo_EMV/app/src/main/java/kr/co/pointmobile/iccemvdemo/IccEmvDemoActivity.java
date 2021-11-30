package kr.co.pointmobile.iccemvdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import vpos.apipackage.APDU_RESP;
import vpos.apipackage.APDU_SEND;
import vpos.apipackage.Icc;
import vpos.messenger.MessengerClient;
import vpos.util.ByteUtil;

import static vpos.apipackage.Icc.Lib_IccClose;
import static vpos.emvkernel.EmvKernel.EmvLib_GetTLV;

public class IccEmvDemoActivity extends AppCompatActivity {
	
	private static final String TAG = IccEmvDemoActivity.class.getName();
	
	public static IccEmvManager iccEmvManager;
	
	private Button btnATR;
	private Button btnAPDU;
	private Button btnPowerDown;
	private Button btnReadCardNum;
	private Button btn360sReset;
	private TextView viewResult;
	private TextView viewStatus;
	private EditText editApduInput;
	private RadioButton rbtnISO;
	private RadioButton rbtnEMV;
	
	public int count  = 0;
	public boolean isInserted = false;

	private static final int msrResultCallbackRead = 0;
	private static final int msrResultCallbackFail = 1;
	
//	private final String selPSE = "00 A4 04 00 0C D2 76 00 01 35 4B 41 53 4D 30 31 00 00";
//	private final String selPSE = "00 A4 04 00 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00";
	private final String selPSE = "00 A4 04 00 00 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00 00"; // extended APDU
	private final String visaAID = "00 A4 04 00 00 07 A0 00 00 00 03 10 10 00 00";
	private final String visaRID = "00 A4 04 00 00 05 A0 00 00 00 03 00 00";
//	private final String selPSE = "";

	private ProgressDialog mProgress;
	private MessengerClient mClient = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar aBar = getSupportActionBar();
		aBar.setIcon(R.drawable.ic_launcher);
		aBar.setDisplayUseLogoEnabled(true);
		aBar.setDisplayShowHomeEnabled(true);

		Initialize();

		mProgress = new ProgressDialog(IccEmvDemoActivity.this);
		mClient = MessengerClient.getInstance(getApplicationContext());
		mClient.init();

		showProgress(IccEmvDemoActivity.this, R.string.initializing, true);
		AsyncMasterConnect async = new AsyncMasterConnect();
		async.execute();
	}

	public void Initialize()
	{
		Log.i(TAG, "Initizlize++++");
		
		rbtnISO = findViewById(R.id.rbtnISO);
		rbtnEMV = findViewById(R.id.rbtnEMV);
		rbtnEMV.setChecked(true);
		
		btnATR = findViewById(R.id.btnATR);
		btnAPDU = findViewById(R.id.btnAPDU);
		btnPowerDown = findViewById(R.id.btnPowerDown);
		btnReadCardNum = findViewById(R.id.btnReadCardNum);// EMV transaction
		
		btn360sReset = findViewById(R.id.btn360sReset);

		viewResult = findViewById(R.id.ResultStatusView);
		viewStatus = findViewById(R.id.viewStatus);
		
		editApduInput = findViewById(R.id.editApduInput);

		btnATR.setOnClickListener(mOnClickListener);
		btnAPDU.setOnClickListener(mOnClickListener);
		btnPowerDown.setOnClickListener(mOnClickListener);
		btnReadCardNum.setOnClickListener(mOnClickListener);
		btn360sReset.setOnClickListener(mOnClickListener);

		viewResult.setMovementMethod(new ScrollingMovementMethod());

		iccEmvManager = new IccEmvManager();
		dispStatus();
		viewResult.setText("");
		Log.i(TAG, "Initizlize----");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
//		if(iccEmvManager != null) {
//			iccEmvManager.runIccDetectPolling(false, null, 0);
//		}
		mClient.close();
		super.onDestroy();
	}

	private void setEnableButtons(boolean enable) {
		btnATR.setEnabled(enable);
		btnAPDU.setEnabled(enable);
		btnPowerDown.setEnabled(enable);
		btnReadCardNum.setEnabled(enable);
		btn360sReset.setEnabled(enable);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			int ret = 0;
			byte vccMode = 1;

			setEnableButtons(false);
			switch (v.getId()) {

				case R.id.btnATR:
					Log.i(TAG, "ATR On++++");
					showProgress(IccEmvDemoActivity.this, R.string.processing, true);
					AsyncAttrOn asyncAttrOn = new AsyncAttrOn();
					asyncAttrOn.execute();
					Log.i(TAG, "ATR On----");
					break;

				case R.id.btnPowerDown:
					Log.i(TAG, "Power Down++++");
					showProgress(IccEmvDemoActivity.this, R.string.processing, true);
					AsyncAttrDown asyncAttrDown = new AsyncAttrDown();
					asyncAttrDown.execute();
					Log.i(TAG, "Power Down----");
					break;

				case R.id.btnAPDU:
					Log.i(TAG, "Send APDU++++");
					showProgress(IccEmvDemoActivity.this, R.string.processing, true);
					AsyncSendAPDU asyncApdu = new AsyncSendAPDU();
					asyncApdu.execute();
					Log.i(TAG, "Send APDU----");

					break;

				// 자체적으로 ATR 부터 APDU 까지 처리한다.
				case R.id.btnReadCardNum:// EMV transaction
					Log.i(TAG, "btnReadCardNum++++");
					showProgress(IccEmvDemoActivity.this, R.string.processing, true);
					AsyncGetPayment asyncPayment = new AsyncGetPayment();
					asyncPayment.execute();

					Log.i(TAG, "btnReadCardNum----");
					return;
				case R.id.btn360sReset:
					Log.i(TAG, "360s Reset+++++");
					viewResult.setText("");
					Log.i(TAG, "360s Reset-----");
					break;
			}
			setEnableButtons(true);
		}
	};

	private boolean dispStatus() {
		if (isInserted == true) {
			viewStatus.setHint(getString(R.string.card_inserted));
		} else {
			viewStatus.setHint(getString(R.string.card_not_inserted));
		}

		return isInserted;
	}

	private byte HexData(char data) {
		byte ret = (byte) 0xff;

		if (0x30 <= data && 0x39 >= data) {
			ret = (byte) (data - 0x30);
		}
		else if (0x41 <= data && 0x46 >= data) {
			ret = (byte) (data - 0x41 + 10);
		}
		else if (0x61 <= data && 0x66 >= data) {
			ret = (byte) (data - 0x61 + 10);
		}

		return ret;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////
	// menu implementation
	// ref: https://lktprogrammer.tistory.com/161

	/**<
	 * Activity가 시작할 때 한번만 호출되는 함수, Menu와 관련된 초기 설정과는 같은 작업이 이루어지는 함수.
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);

//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId()) {
			case R.id.action_info:
				openInfo();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void openInfo() {
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

//		String version = "v1.0.0";
		alert.setMessage(getString(R.string.app_name) + version);
		alert.show();
	}

	private void showLogState(final String strLog)
	{
		IccEmvDemoActivity.this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				viewStatus.setText(strLog);
			}
		});
	}

	private void showLogResult(final String strLog,final boolean isClear)
	{
		IccEmvDemoActivity.this.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if(isClear)
					viewResult.setText("");

				//textMsgView.setText(strLog);
				viewResult.append(strLog);

			}
		});
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

			iccEmvManager.initIccEmv(getApplicationContext(), getFilesDir().getPath() + "/emv");

			return true;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);

			showProgress(IccEmvDemoActivity.this, R.string.initializing, false);
		}
	}

	class AsyncAttrOn extends AsyncTask<Void, Void, byte[]>
	{
		@Override
		protected byte[] doInBackground(Void... params)
		{
			byte ATR[] = new byte[100];
			byte vcc_mode = 1;
			int ret = Icc.Lib_IccCheck((byte) 0);
			if (ret != 0)
			{
				showLogState(getString(R.string.card_not_inserted));
				showLogResult("ATR fail!", true);
				return null;
			}

			ret = Icc.Lib_IccOpen((byte) 0, vcc_mode, ATR);
			if (ret != 0)
			{
				showLogState("");
				showLogResult("ATR fail!", true);
				Icc.Lib_IccClose( (byte)0);
				return null;
			}
			return ATR;
		}

		@Override
		protected void onPostExecute(byte[] result)
		{
			super.onPostExecute(result);

			if(result != null)
			{
				viewStatus.setHint(R.string.card_inserted);         // "Card inserted"
				String resultATR = "success the ATR.\n";
				resultATR += EmvUtils.bytesToNibble(result, 1, result[0]);

				viewResult.setText(resultATR);
			}

			showProgress(IccEmvDemoActivity.this, R.string.processing, false);
		}
	}

	class AsyncAttrDown extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{

			int ret = Icc.Lib_IccClose((byte) 0);
			if (ret != 0)
			{

				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);

			showLogState("");
			if(result )
			{
				viewResult.setText(getString(R.string.atr_power_down));
			}
			else
			{
				showLogResult("failed the ICC power down!", true);
			}

			showProgress(IccEmvDemoActivity.this, R.string.processing, false);
		}
	}

	class AsyncSendAPDU extends AsyncTask<String, Void, byte[]>
	{
		@Override
		protected byte[] doInBackground(String... params)
		{

			String strAPDU = null;
			if(params.length > 0)
				strAPDU = params[0];

			byte slot = 0;
			byte ATR[] = new byte[100];
			byte vcc_mode = 1;
			byte dataIn[] = new byte[512];

			int ret = Icc.Lib_IccCheck((byte) 0);
			if (ret != 0)
			{
				showLogState(getString(R.string.card_not_inserted));
				showLogResult("ATR fail!", true);
				return null;
			}

			ret = Icc.Lib_IccOpen((byte) 0, vcc_mode, ATR);
			if (ret != 0)
			{
				showLogState("");
				showLogResult("ATR fail!", true);
				Icc.Lib_IccClose((byte) 0);
				return null;
			}

			APDU_SEND ApduSend;
			byte[] resp = new byte[516];
			byte[] cAPDU;
			if(strAPDU == null)
			{
				byte[] cmd = new byte[]{(byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00};
				short lc = 0x0e;
				short le = 0x00;

				String sendMsg = "1PAY.SYS.DDF01"; // PSE

				dataIn = sendMsg.getBytes();

				ApduSend = new APDU_SEND(cmd, lc, dataIn, le);
				cAPDU = ApduSend.getBytes();
			}
			else
			{
				if ((strAPDU.length() % 2) != 0)
				{
					showLogResult("Invalid input data!", true);
					Icc.Lib_IccClose(slot);
					return null;
				}
				cAPDU = EmvUtils.nibbleToHex(strAPDU);
			}

			ret = Icc.Lib_IccCommand(slot, cAPDU, resp);

			if (0 == ret)
			{
				return resp;
			}
			else
			{
				return null;
			}


		}

		@Override
		protected void onPostExecute(byte[] result)
		{
			super.onPostExecute(result);

			showLogState("");
			if (result != null)
			{
				APDU_RESP ApduResp = null;
				String strInfo = "";
				ApduResp = new APDU_RESP(result);
				strInfo = ByteUtil.bytearrayToHexString(ApduResp.DataOut, ApduResp.LenOut) + "SWA:"
						+ ByteUtil.byteToHexString(ApduResp.SWA) + " SWB:" + ByteUtil.byteToHexString(ApduResp.SWB);
				viewResult.setText(strInfo);
			}
			else
			{
				showLogResult("Lib_IccCommand() fail!", true);
			}

			showProgress(IccEmvDemoActivity.this, R.string.processing, false);
		}
	}

	class AsyncGetPayment extends AsyncTask<Void, Void, byte[]>
	{
		@Override
		protected byte[] doInBackground(Void... params)
		{
			byte[]  val = null;
			byte[]  track2 = new byte[512];
			int[]   len = new int[1];
			int     ret = 0;

			ret = Icc.Lib_IccCheck((byte) 0);
			if (ret != 0)
			{
				showLogState(getString(R.string.card_not_inserted));
				showLogResult("GetPayment fail!", true);
				return null;
			}

			iccEmvManager.startEMV();

			if((ret = EmvLib_GetTLV("57", track2, len)) == 0)
			{
				val = new byte[len[0]];
				System.arraycopy(track2, 0, val, 0, len[0]);
				track2 = EmvUtils.hexToNibbleBytes(val);
			}
			else
			{
				track2 = null;
			}

			Lib_IccClose((byte)0x00);
			return track2;
		}

		@Override
		protected void onPostExecute(byte[] bytes)
		{
			if(bytes != null)
			{
				viewResult.setText(new String(bytes).trim());
			}
			else
			{
				viewResult.append("ICC read fail!");
			}
			setEnableButtons(true);
			showProgress(IccEmvDemoActivity.this, R.string.processing, false);
			super.onPostExecute(bytes);
		}
	}
}

