
package com.hly.fontxiu;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.youmi.android.AdManager;
import net.youmi.android.dev.OnlineConfigCallBack;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hly.fontxiu.feedback.FeedBackActivity;
import com.hly.fontxiu.fontall.FontAllFragment;
import com.hly.fontxiu.fontquality.FontBoutiqueFragment;
import com.hly.fontxiu.points.EarnPointsFragment;
import com.hly.fontxiu.setting.AboutActivity;
import com.hly.fontxiu.setting.UpdateHelper;
import com.hly.fontxiu.utils.ApkInstallHelper;
import com.hly.fontxiu.utils.CommonUtils;
import com.hly.fontxiu.utils.FileUtils;
import com.hly.fontxiu.utils.PointsHelper;

public class MainActivity extends FragmentActivity implements OnClickListener,
        PointsChangeNotify {

    public static final String TAG = "MainActivity";

    private static final int PAGE_COUNT = 3;

    public static final int FONT_QUALITY_LIST = 0;
    public static final int FONT_ALL = 1;
    public static final int FONT_EARN_POINTS = 2;

    private TextView mTabQuality;
    private TextView mTabAllFont;
    private TextView mTabEarnPoints;

    ListFragmentAdapter mAdapter;
    private static ViewPager sViewPager;
    private TextView[] mTabs;

    public int mLastPosition;

    SharedPreferences sp;
    
    String mKey = "mAwardFirstTime";  // key
    String defaultValue = null;    // 默认的 value，当获取不到在线参数时，会返回该值

    public static String sAwardPoints = "300";
    
    
    String updateKey = "mUpdate";
    public static String sUpdateFontFile;
    public static String sFontFileUri;
    
    public static final int NO_INSTALL_PERMISSION = 1;
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		super.handleMessage(msg);
    		if (msg.what == NO_INSTALL_PERMISSION){
    			Toast.makeText(getApplicationContext(), R.string.font_apply_only_in_meitu2, Toast.LENGTH_LONG).show();
    		}
    	};
    };
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(CommonUtils.FontXiu, Context.MODE_PRIVATE);
        
        installFontApk();

        initTab();
        initViewPager();

        sViewPager.setCurrentItem(0);
        updateTab(0);
        initOnlineParameter(this);
        initUpdateParameter(this);
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                initAd();
                runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						firstLoadApp(sp);
					}
				});
            }
        }).start();
        

        UpdateHelper task = new UpdateHelper(this);
        task.execute(false);
    }



	private void initUpdateParameter(MainActivity mainActivity) {
		// 2. 异步调用（可在任意线程中调用）
        AdManager.getInstance(this).asyncGetOnlineConfig(updateKey, new OnlineConfigCallBack() {
            @Override
            public void onGetOnlineConfigSuccessful(String key, String value) {
                // 获取在线参数成功
            	String[] str = value.split(";");
            	sFontFileUri = str[0];
            	sUpdateFontFile = str[1];
            	Log.d(TAG, "sUpdateFontFile="+sUpdateFontFile+",sFontFileUri="+sFontFileUri);
            	File f = getFilesDir();
            	final String fontFileName = "fontlist.xml";
            	File fontlist = new File(f.getAbsolutePath(),fontFileName);
            	if (!fontlist.exists() || sUpdateFontFile.equals("true")){
            		new Thread(new Runnable() {
						
						@Override
						public void run() {
							FileOutputStream fos = null;
		    				try {
		    					// 连接地址
		    					URL u = new URL(sFontFileUri);
		    					HttpURLConnection c = (HttpURLConnection) u
		    							.openConnection();
		    					// c.setRequestMethod("GET");
		    					// c.setDoOutput(true);
		    					// c.connect();

		    					// 计算文件长度
		    					int lenghtOfFile = c.getContentLength();

		    					String fileName = fontFileName;
		    					fos = openFileOutput(fileName, Context.MODE_PRIVATE);// 文件处理细节
		    					
		    					
		    					InputStream in = c.getInputStream();

		    					// 下载的代码
		    					byte[] buffer = new byte[1024];
		    					int len = 0;
		    					long total = 0;

		    					while ((len = in.read(buffer)) > 0) {
//		    						total += len; // total = total + len1
//		    						publishProgress(""
//		    								+ (int) ((total * 100) / lenghtOfFile));
		    						fos.write(buffer, 0, len);
		    					}

		    					fos.flush();

		    				} catch (Exception e) {
		    					e.printStackTrace();
		    				} finally {
		    					if (fos != null) {
		    						try {
		    							fos.close();
		    						} catch (IOException e) {
		    							e.printStackTrace();
		    						}
		    					}
		    					Intent intent = new Intent();
		    					intent.setAction(FontAllFragment.GENERATED_FONTFILE_ACTION);
		    					sendBroadcast(intent);
		    				}
						}
					}).start();
            	}
            }

            @Override
            public void onGetOnlineConfigFailed(String key) {
                // 获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常
            	sUpdateFontFile = "false";
            }
        });		
	}

	private void initOnlineParameter(Context context) {
    	 // 1. 同步调用方法，务必在非 UI 线程中调用，否则可能会失败。
        //String value = AdManager.getInstance(context).syncGetOnlineConfig(mKey, defaultValue);

        // 2. 异步调用（可在任意线程中调用）
        AdManager.getInstance(this).asyncGetOnlineConfig(mKey, new OnlineConfigCallBack() {
            @Override
            public void onGetOnlineConfigSuccessful(String key, String value) {
                // 获取在线参数成功
            	sAwardPoints = value;
            }

            @Override
            public void onGetOnlineConfigFailed(String key) {
                // 获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常
            	sAwardPoints = 280+"";
            }
        });
	}

	private void firstLoadApp(SharedPreferences sp) {
    	WifiManager wm = (WifiManager) getSystemService(
				Context.WIFI_SERVICE);
		// WifiInfo wifiInfo = wm.getConnectionInfo();
		boolean isWifi = wm.isWifiEnabled()
				&& (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED);

		ConnectivityManager cm = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isNetData = ni.isConnected();

		if (isWifi || isNetData) {
			boolean isFirstLoading = sp.getBoolean("isFirstLoading", true);
			if (isFirstLoading){
				PointsHelper.awardPoints(this, Integer.parseInt(sAwardPoints));
				sp.edit().putBoolean("isFirstLoading", false).commit();
				new AlertDialog.Builder(this).setTitle("字体秀秀")
						.setMessage("恭喜您获得"+Integer.parseInt(sAwardPoints)+"金币奖励")
						.setNeutralButton("确定", null).create().show();
			}
		}
	}

	@Override
    protected void onResume() {
        super.onResume();
    }
	
    private void installFontApk() {
    	// 初始化数据pb
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String filePath = Environment.getExternalStorageDirectory()+"/fontxiuxiu";
					File file = new File(filePath);
					if (!file.exists()){
						ApkInstallHelper.unZip(MainActivity.this, "fontapk.zip",filePath );
					}
					
					//过滤apk文件
					File[] files = file.listFiles(new FileFilter() {
						
						@Override
						public boolean accept(File pathname) {
							if (pathname.getName().endsWith(".apk")){
								return true;
							}
							return false;
						}
					});
					//静默安装精品
					for (int i=0; i < files.length; i++){
						String apkFilePath = files[i].getAbsolutePath();
						PackageManager pm = getPackageManager();
						PackageInfo info = pm.getPackageArchiveInfo(apkFilePath,
								PackageManager.GET_ACTIVITIES);
						String packageName = info.applicationInfo.packageName;
						silentInstall(packageName,apkFilePath);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
			}
		}).start();
	}
    
    public void silentInstall(String packageName, String path) {
    	try{
			Uri uri = Uri.fromFile(new File(path));
			PackageManager pm = getPackageManager();
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
        AdManager.getInstance(this).init("cf94d98f827676b6",
                "5f0af37c8bb20297", false);
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
            /*
             * File f = new File(Environment.getExternalStorageDirectory()
             * 　　　　　　 +"/Pictures/2.png"); 　　　　 Uri u = Uri.fromFile(f); 　　　　
             * it.putExtra(Intent.EXTRA_STREAM, u);
             */
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
                .append("4.提供最精致的字体给用户").append("\n").append("【适配机器】")
                .append("\n").append("1.美图手机2").append("\n")
                .append("2.MTK平台JB2及以上版本").append("\n").append("交流群：")
                .append("\n").append("qq群：185378427");
    }

    @Override
    public void onClick(View v) {
        int position = v.getId();
        mTabs[position].setSelected(true);
        sViewPager.setCurrentItem(position);
    }

    private void initTab() {
        mTabQuality = (TextView)findViewById(R.id.tab_font_quality);
        mTabQuality.setText("精品");
        mTabQuality.setId(FONT_QUALITY_LIST);
        mTabQuality.setOnClickListener(this);

         mTabAllFont = (TextView)findViewById(R.id.tab_font_all);
         mTabAllFont.setText("全部");
         mTabAllFont.setId(FONT_ALL);
         mTabAllFont.setOnClickListener(this);

        mTabEarnPoints = (TextView)findViewById(R.id.tab_earn_points);
        mTabEarnPoints.setText("获取积分");
        mTabEarnPoints.setId(FONT_EARN_POINTS);
        mTabEarnPoints.setOnClickListener(this);

        mTabs = new TextView[PAGE_COUNT];
        mTabs[FONT_QUALITY_LIST] = mTabQuality;
         mTabs[FONT_ALL] = mTabAllFont;
        mTabs[FONT_EARN_POINTS] = mTabEarnPoints;
    }

    private void initViewPager() {
        mAdapter = new ListFragmentAdapter(getSupportFragmentManager(),
                PAGE_COUNT);
        sViewPager = (ViewPager)findViewById(R.id.view_pager_contain);
        sViewPager.setAdapter(mAdapter);
        sViewPager.setOnPageChangeListener(mOnPageChangeListener);
    }

    private SimpleOnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            updateTab(position);
        }
    };

    private void updateTab(int currentPosition) {
        if (currentPosition >= 0 && currentPosition < PAGE_COUNT) {
            mTabs[mLastPosition].setSelected(false);
            mLastPosition = currentPosition;
            mTabs[currentPosition].setSelected(true);
        }
    }

    private class ListFragmentAdapter extends FragmentPagerAdapter {
        private Fragment[] mListFragment;

        public ListFragmentAdapter(FragmentManager fm, int pageCount) {
            super(fm);
            mListFragment = new Fragment[pageCount];
        }

        /**
         * 跳转到相关页面 mListFragment[position] = new
         * WhiteListFragment().setSimId(BlockCenterActivity.this.getSimId());
         */
        @Override
        public Fragment getItem(int position) {
            if (null == mListFragment[position]) {
                switch (position) {
                case FONT_QUALITY_LIST:
                    // TODO
                    mListFragment[position] = new FontBoutiqueFragment();
                    break;

                case FONT_ALL:
                    // TODO
                    mListFragment[position] = new FontAllFragment();
                    break;
                case FONT_EARN_POINTS:
                    // TODO
                    mListFragment[position] = new EarnPointsFragment();
                    break;
                default:
                    break;
                }
            }
            return mListFragment[position];
        }

        @Override
        public int getCount() {
            return mListFragment.length;
        }
    }

    public int getLastPosition() {
        return mLastPosition;
    }

    @Override
    public void onPointBalanceChange(int currentPoints) {
        PointsHelper.sCurrPoints = currentPoints;
        Log.d(TAG, "current points is:" + currentPoints);
        sp.edit().putInt(CommonUtils.CURRENT_POINTS, currentPoints).commit();
        Intent i = new Intent();
        i.setAction("com.hly.fontxiu.showpoints");
        sendBroadcast(i);
    }

    public static ViewPager getViewPager() {
        return sViewPager;
    }
}
