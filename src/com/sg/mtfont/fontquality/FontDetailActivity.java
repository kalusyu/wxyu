package com.sg.mtfont.fontquality;

import java.io.File;
import java.lang.ref.WeakReference;
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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sg.mtfont.R;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.task.ChangeFontLoaderTask;
import com.sg.mtfont.utils.ApkInstallHelper;
import com.sg.mtfont.utils.CommonUtils;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.FileUtils;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

public class FontDetailActivity extends Activity implements OnClickListener {

	public static final String TAG = "FontDetailActivity";

	List<FontResource> mFontRes = new ArrayList<FontResource>();

	private String mPackgeName, mFontFileName;

	private String mUri = "https://github.com/kalusyu/fontxiuxiu/raw/master/yijiaziti.apk";// TODO
																							// test
	private String mFontName = "yijiaziti";
	Button mDownload, mInstall, mApply;
	int mDownloadResId = R.id.btn_download_font;
	int mInstallResId = R.id.btn_install_font;
	int mApplyResId = R.id.btn_apply_font;
	
	/**
	 * 预览图片滑动
	 */
	private FontPreviewAdapter mAdapter;
	private ViewPager mViewPager;
	private ArrayList<String> mUris;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_font_detail);
		setupViews();
		String uri = getIntent().getStringExtra(FontBoutiqueFragment.EXTRA_SELECTED_URL);
		mUris = getIntent().getExtras().getStringArrayList(FontBoutiqueFragment.EXTRA_ALL_URLS);
		initViewPager(uri);
		
		showButtonVisibility();

		// TODO 注册应用安装完成广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver, filter);

		// download receiver
		filter = new IntentFilter();
		filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(mReceiver, filter);

	}

	private void initViewPager(String uri) {
	    mAdapter = new FontPreviewAdapter(mUris);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mAdapter);
        mViewPager.setCurrentItem(mUris.indexOf(uri));
    }

    /**
	 * 
	 * 显示对应的button
	 * 2014年8月2日 下午5:06:46
	 */
	private void showButtonVisibility() {
		if (CommonUtils.checkFontInstalled(false, false)) {
			setButtonVisibility(mApplyResId);
		} else {
			setButtonVisibility(mDownloadResId);
		}
	}

	/**
	 * init ui
	 * 
	 * 2014年8月2日 下午5:07:11
	 */
	private void setupViews() {
		mDownload = (Button) findViewById(R.id.btn_apply_font);
		mInstall = (Button) findViewById(R.id.btn_install_font);
		mApply = (Button) findViewById(R.id.btn_download_font);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_PACKAGE_ADDED)
					|| action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
				String packageName = intent.getData().getSchemeSpecificPart();

				if (packageName != null && packageName.contains("android.font")) {
					setButtonVisibility(R.id.btn_apply_font);
					Log.d(TAG,
							"font apk had installed ,applying it to system packageName="
									+ packageName);
					switchFont(packageName);
				}
			} else if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) { // download
																					// complete
				long id = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, 0); // TODO
				boolean isDownload = SharedPreferencesHelper
						.getSharepreferences(context).getBoolean(
								String.valueOf(id), false);
				if (isDownload) {
					// TODO 下载完成做的处理
					setButtonVisibility(R.id.btn_install_font);
				}
			} else if (action
					.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
				Toast.makeText(context, "通知", Toast.LENGTH_LONG).show();
			}
		}
	};

	public void switchFont(String packageName) {
		PackageManager pm = getPackageManager();
		List<PackageInfo> packegeInfoList = FontResUtil
				.getFontPackegeInfoList(pm);
		ChangeFontLoaderTask task = new ChangeFontLoaderTask(
				getPackageManager(), FontDetailActivity.this, packegeInfoList);
		task.execute(packageName);

	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	};

	/**
	 * 
	 * @param id
	 *            2014年8月2日 下午4:42:56
	 */
	void setButtonVisibility(int id) {
		switch (id) {
		case R.id.btn_apply_font:
			mDownload.setVisibility(View.GONE);
			mInstall.setVisibility(View.GONE);
			mApply.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_download_font:
			mDownload.setVisibility(View.VISIBLE);
			mInstall.setVisibility(View.GONE);
			mApply.setVisibility(View.GONE);
			break;
		case R.id.btn_install_font:
			mDownload.setVisibility(View.GONE);
			mInstall.setVisibility(View.VISIBLE);
			mApply.setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_apply_font:
			getFontAndUse();
			break;
		case R.id.btn_download_font:
			// TODO
			downloadFontFile(mUri);
			break;
		case R.id.btn_install_font:
			// TODO
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * 
	 * @param pUri
	 *            2014年7月30日 下午11:58:33
	 */
	private void downloadFontFile(String pUri) {
		String file = FileUtils.getSDCardPath() + File.separatorChar
				+ "download" + File.separatorChar + mFontName + ".apk";
		File fontApk = new File(file);
		if (!fontApk.exists()) {
			DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(pUri);
			Request request = new Request(uri);
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
					| DownloadManager.Request.NETWORK_WIFI);
			request.setDestinationInExternalFilesDir(this, null, file);// TODO
																		// 考虑sd卡问题，可以考虑先下载安装完再删除存到应用files下面
			request.setDestinationInExternalPublicDir("fontxiuxiu/download",
					mFontName + ".apk");
			long id = dm.enqueue(request);
			SharedPreferences sp = SharedPreferencesHelper
					.getSharepreferences(this);
			sp.edit().putBoolean(String.valueOf(id), true).commit();
			request.setTitle("字体下载");
			request.setDescription(mFontFileName + "下载中");
		}
	}

	private void getFontAndUse() {

		// final ProgressDialog dialog = ProgressDialog.show(this, "提示",
		// "正在应用字体...");
		int currentPoints = PointsHelper.getCurrentPoints(this);
		//TODO
		if (/*!MainActivity.mConfig.isFree()
				&&*/ currentPoints < Constant.NEED_POINTS
				&& !SharedPreferencesHelper.isFontApplied(this, mPackgeName)) {
			new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage(
							"应用此字体需要200积分\n您当前的积分为" + currentPoints + "，是否获取积分")
					.setPositiveButton("获取积分",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
								    //TODO
									/*ViewPager viewPager = MainActivity
											.getViewPager();
									if (viewPager != null) {
										FontDetailActivity.this.finish();
										viewPager.setCurrentItem(2);
									}*/
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
			if (!ApkInstallHelper.checkProgramInstalled(this, mPackgeName)) {
				String filePath = Environment.getExternalStorageDirectory()
						+ "/fontxiuxiu";
				startActivity(ApkInstallHelper.getIntentFromApk(new File(
						filePath + "/" + mFontFileName)));
			} else {
				if (CommonUtils.checkFontInstalled(false,false
						/*MainActivity.mConfig.isFree()*/)) { // TODO change false
															// to variable
					switchFont(mPackgeName);
				} else {
					// TODO
					ApkInstallHelper.getIntentFromApk(new File(""));
				}
			}
		}
	}
	
	/**
	 * 
	 * @author Kalus Yu
	 *
	 */
	class FontPreviewAdapter extends PagerAdapter implements OnPageChangeListener{
        
        ArrayList<String> mUris;
        SparseArray<WeakReference<ImageView>> mSparseArray;
        
        public FontPreviewAdapter(ArrayList<String> uris) {
            mUris = uris;
            mSparseArray = new SparseArray<WeakReference<ImageView>>(mUris.size());
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            ImageLoader.getInstance().displayImage(mUris.get(position), imageView);
            container.addView(imageView,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mSparseArray.put(position, new WeakReference<ImageView>(imageView));
            return imageView;
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);  
            final WeakReference<ImageView> imageView = mSparseArray.get(position);  
            if (imageView != null) {  
                imageView.clear();  
            }  
        }

        @Override
        public int getCount() {
            return mUris.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onPageSelected(int position) {
            int size=mSparseArray.size();  
            for (int k = 0; k < size; k++) {  
                int key = mSparseArray.keyAt(k);  
                WeakReference<ImageView> viewWeakReference=mSparseArray.get(key);  
                if (null != viewWeakReference && null != viewWeakReference.get()) {  
                    ImageView imagePageView = (ImageView) viewWeakReference.get();  
                    if (key == position) {  
                        ImageLoader.getInstance().displayImage(mUris.get(position), imagePageView);
                    } 
                } 
            }  
        }
        
    }
}
