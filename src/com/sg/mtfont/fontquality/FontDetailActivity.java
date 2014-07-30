package com.sg.mtfont.fontquality;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.sg.mtfont.MainActivity;
import com.sg.mtfont.R;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.utils.ApkInstallHelper;
import com.sg.mtfont.utils.FileUtils;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

public class FontDetailActivity extends Activity implements OnClickListener {
	
	public static final String TAG = "FontDetailActivity";

	// private ImageView ivFontDetal;
	public static final int NEED_POINTS = 200;
	
	List<FontResource> mFontRes = new ArrayList<FontResource>();
	
	private String mPackgeName,mFontFileName;
	
	private String mUri;
	private String mFontName;
	

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_font_detail);
		ImageView ivFontDetal = (ImageView) findViewById(R.id.iv_font_detail);
		findViewById(R.id.btn_use_font).setOnClickListener(this);
		Intent intent = getIntent();
		int resourceId = -1;
		if (null != intent) {
			resourceId = intent.getIntExtra(
					FontBoutiqueFragment.FONT_DETAIL_RESOURCE, -1);
			mPackgeName = intent.getStringExtra(FontBoutiqueFragment.FONT_FILE_PATCH_RESOURCE);
			mFontFileName = intent.getStringExtra(FontBoutiqueFragment.FONT_FILENAME);
		}
		if (resourceId > 0) {
			ivFontDetal.setImageResource(resourceId);
		}
		
		//TODO 注册应用安装完成广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver,filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String packageName = intent.getData().getSchemeSpecificPart();
			String packageName2 = mPackgeName;
			if (packageName.equals(packageName2)) {
				switchFont(packageName2);
			}
		}
	};
	
	public void switchFont(String packageName){
		PackageManager pm = getPackageManager();
		List<PackageInfo> packegeInfoList = FontResUtil
				.getFontPackegeInfoList(pm);
		FontLoadTask task = new FontLoadTask(getPackageManager(),
				FontDetailActivity.this, packegeInfoList);
		task.execute(packageName);
		
	}
	
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	};

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_use_font:
			getFontAndUse();
			break;
		case R.id.btn_download_font:
			//TODO
			downloadFontFile(mUri);
		default:
			break;
		}
	}

	/**
	 * 
	 *
	 * @param pUri
	 * 2014年7月30日 下午11:58:33
	 */
	private void downloadFontFile(String pUri) {
		String file = FileUtils.getSDCardPath()
				+ File.separatorChar + "download"
				+ File.separatorChar + mFontName
				+ ".apk";
		File fontApk = new File(file);
		if (!fontApk.exists()){
			DownloadManager dm = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(pUri);
			Request request = new Request(uri);
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
			request.setDestinationInExternalFilesDir(this, null, file);//TODO 考虑sd卡问题，可以考虑先下载安装完再删除存到应用files下面
			request.setDestinationInExternalPublicDir("fontxiuxiu/download", mFontName+".apk");
			long id = dm.enqueue(request);
			SharedPreferences sp = SharedPreferencesHelper.getSharepreferences(this);
			sp.edit().putBoolean(String.valueOf(id), true).commit();
			request.setTitle("字体下载");
			request.setDescription(mFontFileName+"下载中");	
		}
	}

	private void getFontAndUse() {

		// final ProgressDialog dialog = ProgressDialog.show(this, "提示",
		// "正在应用字体...");
		int currentPoints = PointsHelper.getCurrentPoints(this);
		if (!MainActivity.mConfig.isFree() && currentPoints < NEED_POINTS && !SharedPreferencesHelper.isFontApplied(this, mPackgeName)) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage(
							"应用此字体需要200积分\n您当前的积分为" + currentPoints + "，是否获取积分")
					.setPositiveButton("获取积分",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									ViewPager viewPager = MainActivity
											.getViewPager();
									if (viewPager != null) {
										FontDetailActivity.this.finish();
										viewPager.setCurrentItem(2);
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							}).show();
		} else {
			if (!ApkInstallHelper.checkProgramInstalled(this,mPackgeName)){
				String filePath = Environment.getExternalStorageDirectory()+"/fontxiuxiu";
				startActivity(ApkInstallHelper.getIntentFromApk(new File(filePath+"/"+mFontFileName)));
			} else{
				switchFont(mPackgeName);
			}
		}
	}
	
	

	
	class FontLoadTask extends AsyncTask<String, Void, FontResource>{
		
		Context mContext;
		List<PackageInfo> mPackegeInfoList;
		PackageManager  mPackageManager;

		public FontLoadTask(PackageManager packageManager,FontDetailActivity fontDetailActivity,
				List<PackageInfo> packegeInfoList) {
			this.mContext = fontDetailActivity;
			this.mPackegeInfoList = packegeInfoList;
			this.mPackageManager = packageManager;
		}

		@Override
		protected FontResource doInBackground(String... arg0) {
			List<FontResource> packageFontResList = FontResUtil.assembleFontResourceFromPackage(mPackageManager, arg0[0]);
			return packageFontResList.get(0);
		}

		@Override
		protected void onPostExecute(FontResource result) {
			try{
				FontResource fontRes = result;
	            FontResUtil.updateSysteFontConfiguration(fontRes);
	            FontResUtil.saveSystemFontRes(mContext, fontRes);
	            if (!SharedPreferencesHelper.isFontApplied(mContext, fontRes.getPackageName())){
		            SharedPreferencesHelper.addToApplied(mContext, fontRes.getPackageName());
		            if (!MainActivity.mConfig.isFree()){//如果不是免费版则需要消耗积分
		            	PointsHelper.spendPoints(mContext, NEED_POINTS);
		            }
	            }
	            Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
	            finish();
			}catch (NoSuchFieldError e){
				Log.e(TAG, e.getMessage());
				Toast.makeText(getApplicationContext(), R.string.font_apply_only_in_meitu2, Toast.LENGTH_LONG).show();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}


	
}
