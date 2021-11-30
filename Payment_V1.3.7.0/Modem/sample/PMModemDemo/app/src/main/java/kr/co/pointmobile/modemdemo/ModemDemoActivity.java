package kr.co.pointmobile.modemdemo;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import vpos.messenger.MessengerClient;
import vpos.apipackage.Modem;

public class ModemDemoActivity extends AppCompatActivity {
	
	private static final String TAG = ModemDemoActivity.class.getName();
	
	private Button btnDial, btnSendData, btnHookOn;
	private TextView viewResult;
	
	boolean mIsQuit = false;
    int mode = 0;

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

		mProgress = new ProgressDialog(ModemDemoActivity.this);
		mClient = MessengerClient.getInstance(getApplicationContext());
		mClient.init();

		showProgress(ModemDemoActivity.this, R.string.initializing, true);
		AsyncMasterConnect async = new AsyncMasterConnect();
		async.execute();
	}

	public void Initialize() {
		Log.d(TAG, "Initizlize++++");
		
		btnDial = findViewById(R.id.btnDial);
		btnSendData = findViewById(R.id.btnSendData);
		btnHookOn = findViewById(R.id.btnHookOn);

		viewResult = findViewById(R.id.ResultStatusView);
		
		viewResult.setMovementMethod(new ScrollingMovementMethod());

		viewResult.setText("");
		Log.i(TAG, "Initizlize----");
	}

    public class RecvData_T extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            int ret;
            int len[] = new int[1];
            byte data[] = new byte[2048];
            byte temp[] = new byte[20480];
            int recvlen = 0;
            while (mIsQuit == false) {
                Arrays.fill(data, (byte)0);
                ret = Modem.Lib_ApiModemRcvString(data, len);
                if (ret == 0 && len[0] > 0) {
                    if ((recvlen+len[0]) > 20480) {
                        Arrays.fill(temp, (byte)0);
                        recvlen = 0;
                    }
                    System.arraycopy(data, 0, temp, recvlen, len[0]);
                    recvlen+=len[0];
                    showLogResult("Recv : " + new String(data) + "\n", false);
                    if (new String(temp).contains("RING")) {
                        Arrays.fill(temp, (byte)0);
                        recvlen = 0;
                        ret = Modem.Lib_ApiModemHookOff();
                        if (ret == 0) {
                            showLogResult("Hook off success!\n", false);
                        } else {
                            showLogResult("Hook off failed!\n", false);
                        }
                    } else if (new String(temp).contains("NO CARRIER")) {
                        showLogResult("Hook on!\n", false);
                        Arrays.fill(temp, (byte)0);
                        recvlen = 0;
                    }
                }
                mSleep(500);
            }
        }
    }

    public void mSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onPause+++");
        super.onPause();
        mIsQuit = true;
        mSleep(100);
        Modem.Lib_ComClose();
    }

	@Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG, "onResume+++");
        mIsQuit = false;

        int ret = Modem.Lib_ApiModemInit(); 
        if (ret == 0) {
            setEnableButtons(0);
            showLogResult("Modem Init OK!\n", true);
            new RecvData_T().start();
        } else {
            showLogResult("Modem Init failed, please check contact state between the device and cradle!\n\rIf still have a problem even though reconnect, please reboot the device and cradle and retry it!", true);
            setEnableButtons(4);
        }
	}

	@Override
	protected void onDestroy() {
        Log.d(TAG, "onDestroy+++");
		mClient.close();
		super.onDestroy();
	}

	private void setEnableButtons(int mode) {
        if (mode == 0) {
            btnDial.setEnabled(true);
            btnSendData.setEnabled(true);
            btnHookOn.setEnabled(true);
        } else if (mode == 1) {
            btnDial.setEnabled(true);
            btnSendData.setEnabled(false);
            btnHookOn.setEnabled(false);
        } else if (mode == 2) {
            btnDial.setEnabled(false);
            btnSendData.setEnabled(true);
            btnHookOn.setEnabled(false);
        } else if (mode == 3) {
            btnDial.setEnabled(false);
            btnSendData.setEnabled(false);
            btnHookOn.setEnabled(true);
        } else {
            btnDial.setEnabled(false);
            btnSendData.setEnabled(false);
            btnHookOn.setEnabled(false);
        }
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// menu implementation
	// ref: https://lktprogrammer.tistory.com/161

	/**
	 * Activity가 시작할 때 한번만 호출되는 함수, Menu와 관련된 초기 설정과는 같은 작업이 이루어지는 함수.
	 * @param menu
	 * @return
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
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

		alert.setMessage(getString(R.string.app_name) + version);
		alert.show();
	}

	private void showLogResult(final String strLog,final boolean isClear) {
		ModemDemoActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (isClear) {
					viewResult.setText("");
                }

				viewResult.append(strLog);
			}
		});
	}

	public void showProgress(final Activity act, final int resId, final boolean bShow) {
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgress.setMessage(getString(resId));
				mProgress.setCancelable(false);

				try {
					if (bShow) {
						mProgress.show();
					} else {
						mProgress.dismiss();
					}
                } catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}

	class AsyncMasterConnect extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "AsyncMasterConnect++++");
			do {
				if (mClient.isConnect()) {
                    Log.d(TAG, "Client connect ok");
                    //mIsQuit = false;
					break;
                }

				try {
					Thread.sleep(1000);
				} catch (Exception e)
				{
				}

			} while (true);

            Log.d(TAG, "AsyncMasterConnect---");
			return true;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);

			showProgress(ModemDemoActivity.this, R.string.initializing, false);
		}
	}

    String NUM = "834\0";
    public void onClickDial(View v) {
        if (mode == 1)
            return;

        new Thread() {
            public void run() {
                showLogResult("\nDialing "+NUM+" ...\n", false);
                int ret = Modem.Lib_ApiModemDialUp(NUM.getBytes(), (byte)0);
                if (ret == 0) {
                    showLogResult("Dial "+NUM+" success.\n", false);
                    mode = 1;
                } else {
                    showLogResult("Dial "+NUM+" failed.\n", false);
                    return;
                }
            };
        }.start();
    }

    public void onClickSendData(View v) {
        int ret = Modem.Lib_ApiModemSendString("0123456789abc".getBytes(), 13);
        if (ret == 0) {
            showLogResult("Send : 0123456789abc\n", false);
        } else {
            showLogResult("Send failed!\n", false);
        }
    }

    public void onClickHookOn(View v) {
        new Thread() {
            public void run() {
                //setEnableButtons(3);
                int ret = Modem.Lib_ApiModemHookOn();
                if (ret == 0) {
                    showLogResult("Hook on success!\n", false);
                    mode = 0;
                } else {
                    showLogResult("Hook on failed!\n", false);
                }
            };
        }.start();
    }
}

