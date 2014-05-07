package com.hly.fontxiu.fontquality;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.hly.fontxiu.MainActivity;
import com.hly.fontxiu.R;
import com.hly.fontxiu.fontmanager.FontResUtil;
import com.hly.fontxiu.fontmanager.FontResource;
import com.hly.fontxiu.utils.ApkInstallHelper;
import com.hly.fontxiu.utils.PointsHelper;
import com.hly.fontxiu.utils.SharedPreferencesHelper;

public class FontDetailActivity extends Activity implements OnClickListener {

	// private ImageView ivFontDetal;
	public static final int NEED_POINTS = 200;
	
	List<FontResource> mFontRes = new ArrayList<FontResource>();
	
	private String mPackgeName,mFontFileName;
	

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
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver,filter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			String pkgName = intent.getData().toSafeString();
			int index = pkgName.indexOf(":");
			String packageName = pkgName.substring(index + 1, pkgName.length());
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
		default:
			break;
		}
	}

	private void getFontAndUse() {

		// final ProgressDialog dialog = ProgressDialog.show(this, "提示",
		// "正在应用字体...");
		int currentPoints = PointsHelper.getCurrentPoints(this);
		if (currentPoints < NEED_POINTS && !SharedPreferencesHelper.isFontApplied(this, mPackgeName)) {
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
			FontResource fontRes = result;
            FontResUtil.updateSysteFontConfiguration(fontRes);
            FontResUtil.saveSystemFontRes(mContext, fontRes);
            if (!SharedPreferencesHelper.isFontApplied(mContext, fontRes.getPackageName())){
	            SharedPreferencesHelper.addToApplied(mContext, fontRes.getPackageName());
	            PointsHelper.spendPoints(mContext, NEED_POINTS);
            }
            Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
            finish();
		}
	}


	
}
