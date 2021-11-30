package kr.co.pointmobile.nfcdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.tabs.TabLayout;

import kr.co.pointmobile.nfcdemo.fragments.TagReadFragment;
import kr.co.pointmobile.nfcdemo.fragments.TagWriteFragment;

import java.util.ArrayList;

import vpos.messenger.MessengerClient;

public class NfcDemoActivity extends AppCompatActivity {

    private ProgressDialog mProgress;
    MessengerClient mClient = null;

    private TabLayout mTabLayout = null;
    private ViewPager mViewPager = null;
    private SectionPagerAdapter mSectionPagerAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_demo);

        mProgress = new ProgressDialog(NfcDemoActivity.this);

        init();

        mClient = MessengerClient.getInstance(getApplicationContext());
        mClient.init();

        showProgress(NfcDemoActivity.this, true);
        AsyncMasterConnect async = new AsyncMasterConnect();
        async.execute();
    }

    private void init() {
        ActionBar aBar = getSupportActionBar();
        aBar.setIcon(R.drawable.ic_launcher);
        aBar.setDisplayUseLogoEnabled(true);
        aBar.setDisplayShowHomeEnabled(true);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_tag_read));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_tag_write));
        mViewPager = findViewById(R.id.view_pager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager(),
                mTabLayout.getTabCount());
        mViewPager.setAdapter(mSectionPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 0) {
                    hideKeyboard();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        mClient.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
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

//		String  version = "v1.0.0";
        alert.setMessage(getString(R.string.app_name) + version);
        alert.show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragment = new ArrayList<>();

        public SectionPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            mFragment.add(new TagReadFragment());
            mFragment.add(new TagWriteFragment());
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragment.get(position);
        }

        @Override
        public int getCount() {
            return mFragment.size();
        }
    }

    public void showProgress(final Activity act, final boolean bShow)
    {
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgress.setMessage(getString(R.string.initializing));
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
            showProgress(NfcDemoActivity.this, false);
        }
    }
}
