
package com.sg.mtfont;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import net.youmi.android.AdManager;
import net.youmi.android.dev.OnlineConfigCallBack;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsManager;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.sg.mtfont.bean.FontFile;
import com.sg.mtfont.feedback.FeedBackActivity;
import com.sg.mtfont.fontall.FontAllFragment;
import com.sg.mtfont.fontquality.FontBoutiqueFragment;
import com.sg.mtfont.fontquality.FontBoutiqueFragment.BoutiqueFragmentListener;
import com.sg.mtfont.points.EarnPointsFragment;
import com.sg.mtfont.setting.AboutActivity;
import com.sg.mtfont.setting.UpdateHelper;
import com.sg.mtfont.utils.CommonUtils;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.FileUtils;
import com.sg.mtfont.utils.HttpUtils;
import com.sg.mtfont.utils.PointsHelper;


/**
 * 
 * @author Kalus Yu
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener,
        PointsChangeNotify ,BoutiqueFragmentListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int FONT_QUALITY_LIST = 0;
    public static final int FONT_ALL = 1;
    public static final int FONT_EARN_POINTS = 2;
    public static int PAGE_COUNT = 3;

    private SharedPreferences sp;
    
    // first time get awards
    public static final String PREFER_AWARDS_KEY = "prefers_awards_key";
    public static final int NO_INSTALL_PERMISSION = 1;
    public static final String UPDATE_KEY = "mUpdate";
    public static final String AWARD_FIRST_TIME_KEY = "mAwardFirstTime";  // key
    public static String AWARD_POINTS = "300";
    public static FontHandler mHandler;
    private JSONArray mJsonArray;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(SplashActivity.SHARE_PREFER_KEYS, Context.MODE_PRIVATE);
        mHandler = new FontHandler(getApplicationContext());
        String json = getIntent().getStringExtra(Constant.FONTFILE);
        try {
        	if (json != null){
        		mJsonArray = new JSONArray(json);
        	}
        } catch (JSONException e) {
            e.printStackTrace();
        }

        checkAndInitUI();
        
        initOnlineParameter(this);
        
        udpateVersion();
    }
    
    
    private void checkAndInitUI() {
        new Thread("GetFreeUser"){
            public void run() {
                final boolean isFree = HttpUtils.checkIsFreeUser(CommonUtils.getImei(MainActivity.this));
                runOnUiThread(new Runnable() {
                    public void run() {
                        initActionBar();
                        initAdvertisement(isFree);
                    }

                });
            };
        }.start();
    }
    
    private void initActionBar(){
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(false);
        Tab boutique = bar.newTab().setText(R.string.boutique)/*.setIcon(R.drawable.ic_launcher)*/;
        Tab all = bar.newTab().setText(R.string.all)/*.setIcon(R.drawable.ic_launcher)*/;
        Tab points = bar.newTab().setText(R.string.get_points)/*.setIcon(R.drawable.ic_launcher)*/;
        Tab myDownload = bar.newTab().setText(R.string.myDownload);
        
        boutique.setTabListener(new FontTabListener(new FontBoutiqueFragment()));
//        all.setTabListener(new FontTabListener(new FontAllFragment()));
        myDownload.setTabListener(new FontTabListener(new MyDownloadFragment(mJsonArray)));
        
        points.setTabListener(new FontTabListener(new EarnPointsFragment()));
        
        bar.addTab(boutique);
//        bar.addTab(all);
        bar.addTab(myDownload);
        bar.addTab(points);
    }


    /**
     * 版本更新
     * @author Kalus Yu
     * 2014年10月3日 下午8:03:54
     */
    private void udpateVersion(){
        UpdateHelper task = new UpdateHelper(this);
        task.execute(false);
    }

    private void initAdvertisement(final boolean isFree) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!isFree){
                    initAd();
                }
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        startAppFirstTime(sp);
                    }
                });
            }
        }).start();
    }

	private void initOnlineParameter(Context context) {
    	 // 1. 同步调用方法，务必在非 UI 线程中调用，否则可能会失败。
        //String value = AdManager.getInstance(context).syncGetOnlineConfig(mKey, defaultValue);

        // 2. 异步调用（可在任意线程中调用）
        AdManager.getInstance(this).asyncGetOnlineConfig(AWARD_FIRST_TIME_KEY, new OnlineConfigCallBack() {
            @Override
            public void onGetOnlineConfigSuccessful(String key, String value) {
                // 获取在线参数成功
            	AWARD_POINTS = value;
            }

            @Override
            public void onGetOnlineConfigFailed(String key) {
                // 获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常
            	AWARD_POINTS = 280+"";
            }
        });
	}

	/**
	 * 第一次启动项目
	 * @author Kalus Yu
	 * @param sp
	 * 2014年10月3日 下午8:07:28
	 */
	private void startAppFirstTime(SharedPreferences sp) {
		if (CommonUtils.isConnected(this)) {
			boolean isFirstLoading = sp.getBoolean(PREFER_AWARDS_KEY, true);
			if (isFirstLoading){
				CommonUtils.getDeviceInfo("用户信息搜集",this);
				if (CommonUtils.getImei(this).equals("863564020008489")){
					PointsHelper.awardPoints(this, 100000);
				} else {
					PointsHelper.awardPoints(this, Integer.parseInt(AWARD_POINTS));
				}
				sp.edit().putBoolean(PREFER_AWARDS_KEY, false).apply();
				new AlertDialog.Builder(this).setTitle(getText(R.string.app_name))
						.setMessage(String.format(getResources().getString(R.string.dialog_alert_msg), AWARD_POINTS))
						.setNeutralButton(getText(R.string.ok), null).create().show();
			} 
		}
	}
	
    
    
    public static void silentInstall(Context ctx,String packageName, String path) {
    	try{
			Uri uri = Uri.fromFile(new File(path));
			PackageManager pm = ctx.getPackageManager();
			pm.installPackage(uri, null, 0, packageName);
    	}catch (SecurityException e){
    		Log.e(TAG, e.getMessage());
    		mHandler.sendEmptyMessage(NO_INSTALL_PERMISSION);
    	}
	}

	/**
     * 初始化AD
     */
    private void initAd() {
        // 初始化接口，应用启动的时候调用
        // 参数：appId, appSecret, 调试模式
        AdManager.getInstance(this).init("8152fb2979a26eff",
                "1e4fed19960bdcf3", false);
        // 如果使用积分广告，请务必调用积分广告的初始化接口:
        OffersManager.getInstance(this).onAppLaunch();

        // (可选)开启用户数据统计服务,默认不开启，传入false值也不开启，只有传入true才会调用
        AdManager.getInstance(this).setUserDataCollect(true);
        // (可选)注册积分监听-随时随地获得积分的变动情况
        PointsManager.getInstance(this).registerNotify(this);

        AdManager.getInstance(this).setUserDataCollect(true);// 统计用户数据
        AdManager.getInstance(this).setEnableDebugLog(false);// 关闭debug log
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 如果使用积分广告，请务必调用积分广告的初始化接口:
        OffersManager.getInstance(this).onAppExit();
        // 注销积分监听-如果在onCreate注册了，那这里必须得注销
        PointsManager.getInstance(this).unRegisterNotify(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
        case R.id.menu_share:
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.setType("text/plain");
            StringBuilder sb = new StringBuilder();
            initShareContent(sb);
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            startActivity(Intent.createChooser(intent, getTitle()));
            break;
        case R.id.menu_feedback:
            intent = new Intent(this, FeedBackActivity.class);
            startActivity(intent);
            break;
        case R.id.menu_about:
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            break;
        case R.id.menu_update:
            UpdateHelper update = new UpdateHelper(this);
            update.execute(true);
            break;

        default:
            break;
        }
        return true;
    }

    private void initShareContent(StringBuilder sb) {
        sb.append("字体秀秀是一款正真无需root，无需重启直接实现系统字体切换美化软件,拥有大量精美字体。软件安全稳定，操作简单，轻轻一点，给你不一样的心情");
        sb.append("\n").append("[主要功能]").append("\n").append("").append("\n")
                .append("1.无需root就可以轻松切换系统字体").append("\n")
                .append("2.无需重启一键动态切换系统字体。").append("\n")
                .append("3.首次运行软件会自动备份系统默认字体").append("\n")
                .append("4.提供最精致的字体给用户").append("\n").append("[适配机器]")
                .append("\n").append("美图手机2 专版").append("\n");
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onPointBalanceChange(int currentPoints) {
        PointsHelper.sCurrPoints = currentPoints;
        //Log.d(TAG, "current points is:" + currentPoints);
        sp.edit().putInt(CommonUtils.CURRENT_POINTS, currentPoints).commit();
        Intent i = new Intent();
        i.setAction("com.hly.fontxiu.showpoints");
        sendBroadcast(i);
    }
    
    
    class FontTabListener implements TabListener{
        
        private Fragment mFragment;
        
        public FontTabListener(Fragment fragment) {
            mFragment = fragment;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            android.support.v4.app.FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            tr.add(R.id.tab_fragment_content, mFragment);
            tr.commit();
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            android.support.v4.app.FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            tr.remove(mFragment);
            tr.commit();
        }
        
    }


    @Override
    public JSONArray getFontJson() {
        return mJsonArray;
    }
}
