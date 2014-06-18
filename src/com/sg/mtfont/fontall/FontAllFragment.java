package com.sg.mtfont.fontall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import net.youmi.android.AdManager;
import net.youmi.android.dev.OnlineConfigCallBack;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sg.mtfont.MainActivity;
import com.sg.mtfont.R;
import com.sg.mtfont.bean.FontFile;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.fontquality.FontDetailActivity;
import com.sg.mtfont.utils.ApkInstallHelper;
import com.sg.mtfont.utils.FileUtils;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

public class FontAllFragment extends ListFragment {

	private AllListAdapter mAdapter;

	private Context mContext;

	private ArrayList<File> mFontsDownloadApk = new ArrayList<File>();

	private List<FontFile> mFontFiles = new ArrayList<FontFile>();

	private WeakReference<ProgressDialog> mProgress;
	
	private static final String TAG = "FontAllFragment";
	
	private static final int LOAD_MSG_OK = 1;
	
	public static final String GENERATED_FONTFILE_ACTION = "com.hly.fontxiu.FONTFILELIST";
	
	public static String sDescription;
	
	private TextView mTxtDescription;
	private Runnable mLoadFontFileListRunnable = new Runnable() {
		
		@Override
		public void run() {
			loadFontRes(mContext);
			loadInstalledFont();
			mHandler.sendEmptyMessage(LOAD_MSG_OK);
		}
	};
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == LOAD_MSG_OK){
				if (mFontFiles != null){
					mAdapter.notifyDataSetChanged();
				}
			} else if (msg.what == MainActivity.NO_INSTALL_PERMISSION){
				Toast.makeText(getActivity(), R.string.font_apply_only_in_meitu2, Toast.LENGTH_LONG).show();
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_font_all, container,
				false);
		mTxtDescription = (TextView) view.findViewById(R.id.txt_description);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		loadFontRes(mContext);
		loadInstalledFont();
		mAdapter = new AllListAdapter();
		setListAdapter(mAdapter);
		
		
		initDescriptionParameter(getActivity());
		

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		getActivity().registerReceiver(mReceiver, filter);
		
		filter = new IntentFilter();
		filter.addAction(GENERATED_FONTFILE_ACTION);
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	private void initDescriptionParameter(FragmentActivity activity) {
		AdManager.getInstance(activity).asyncGetOnlineConfig("mDescription", new OnlineConfigCallBack() {
            @Override
            public void onGetOnlineConfigSuccessful(String key, String value) {
                // 获取在线参数成功
            	sDescription = value;
            	Log.d(TAG, "sDescription="+sDescription);
            	if (mTxtDescription != null){
            		String[] s = value.split(";");
            		mTxtDescription.setText(s[0]+","+s[1]+"\n\t\t\t\t\t\t"+s[2] +"!");
            		mTxtDescription.setTextColor(Color.RED);
            	}
            }

            @Override
            public void onGetOnlineConfigFailed(String key) {
                // 获取在线参数失败，可能原因有：键值未设置或为空、网络异常、服务器异常
            	sDescription = "";
            }
        });		
	}

	@Override
	public void onResume() {
		super.onResume();
		getListView().setDivider(getResources().getDrawable(R.drawable.list_divider_light));
		getListView().setDividerHeight(4);
	}
	
	String[] mPackagesExclude = new String[] { 
			"com.monotype.android.font.xiaonaipaozhongwen",
			"com.monotype.android.font.cuojuehuiyi",
			"com.monotype.android.font.wuyunkuaizoukai",
			"com.monotype.android.font.jiangnandiao",
			"com.monotype.android.font.zhihualuo"
			
			 };

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			try{
				String pkgName = intent.getDataString();
				PackageManager pm = mContext.getPackageManager();
				List<PackageInfo> packegeInfoList = FontResUtil
						.getFontPackegeInfoList(pm);
				String packageName = null;
				if (pkgName != null){
					int index = pkgName.indexOf(":");
					packageName = pkgName.substring(index + 1, pkgName.length());
				}
				boolean defaultFont = Arrays.asList(mPackagesExclude).contains(packageName);
				if (packageName!= null && packageName.contains("android.font") && !defaultFont){
					Log.d(TAG, "font apk had installed ,applying it to system packageName=" + packageName);
					FontLoadTask task = new FontLoadTask(
							mContext.getPackageManager(), mContext, packegeInfoList);
					task.execute(packageName);
				}
				
				if (intent.getAction().equals(GENERATED_FONTFILE_ACTION)){
					Log.d(TAG, "intent.getAction()="+intent.getAction());
					mHandler.post(mLoadFontFileListRunnable);
				}
			}catch (Exception e){
				Log.e(TAG, "some errors message="+e.getMessage());
			}
		}
	};

	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
	};
	
	private void loadInstalledFont() {
		// TODO
		Log.d(TAG, "mFontFiles="+mFontFiles);
		if (mFontFiles != null){
			for (FontFile fontFile : mFontFiles) {
				boolean isApplied = SharedPreferencesHelper.isFontApplied(
						mContext, fontFile.getFontName());
				fontFile.setApplied(isApplied);
			}
		}
		
		for (File apkFile : mFontsDownloadApk){
			for (FontFile fontFile:mFontFiles){
				if (apkFile.getName().equals(fontFile.getFontName()+".apk")){
					fontFile.setDownloaded(true);
					break;
				}
			}
		}
		
	}

	/**
	 * 
	 * @param context
	 */
	private void loadFontRes(Context context) {
		// 加载fontlist.xml 列表
		FileInputStream fis = null;
		try {
			if (mContext != null){
				fis = mContext.getApplicationContext().openFileInput("fontlist.xml");
				mFontFiles = FileUtils.parseXmlFile(fis);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// 加载下载的apk文件
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String fontApkPath = FileUtils.getSDCardPath() + File.separatorChar
					+ context.getResources().getString(R.string.font_path);
			File file = new File(fontApkPath);
			if (file.exists()) {
				for (File child : file.listFiles()) {
					 if (child.getName().endsWith(".apk")){
						mFontsDownloadApk.add(child);
					 }
				}
			} else {
//				Toast.makeText(getActivity(),
//						R.string.font_download_file_is_not_exist,
//						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(getActivity(),
					R.string.current_sdcard_not_available, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(getActivity(), "p:" + position, 1).show();
	}
	
	class FontLoadTask extends AsyncTask<String, Void, FontResource> {

		Context mContext;
		List<PackageInfo> mPackegeInfoList;
		PackageManager mPackageManager;

		public FontLoadTask(PackageManager packageManager, Context context,
				List<PackageInfo> packegeInfoList) {
			this.mContext = context;
			this.mPackegeInfoList = packegeInfoList;
			this.mPackageManager = packageManager;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = new WeakReference<ProgressDialog>(
					ProgressDialog
							.show(mContext,
									null,
									getResources().getString(
											R.string.font_applying), true,
									false));
		}

		@Override
		protected FontResource doInBackground(String... arg0) {
			List<FontResource> packageFontResList = FontResUtil
					.assembleFontResourceFromPackage(mPackageManager,
							arg0[0]);
			return packageFontResList.get(0);
		}

		@Override
		protected void onPostExecute(FontResource result) {
			try{
				FontResource fontRes = result;
				FontResUtil.updateSysteFontConfiguration(fontRes);
				FontResUtil.saveSystemFontRes(mContext, fontRes);
	
				if (!SharedPreferencesHelper.isFontApplied(mContext,fontRes.getPackageName())) {
					SharedPreferencesHelper.addToApplied(mContext,fontRes.getPackageName());
					PointsHelper.spendPoints(mContext,FontDetailActivity.NEED_POINTS);
				}
	
				final ProgressDialog lProgress = mProgress.get();
				Activity activity = getActivity();
				boolean isFinish = false;
				if(null != activity){
					isFinish = activity.isFinishing();
				}
				if (lProgress != null && lProgress.isShowing() && !isFinish) {
					lProgress.dismiss();
					Toast.makeText(
							mContext,
							getResources().getString(
									R.string.font_apply_success),
							Toast.LENGTH_SHORT).show();
				}				
				Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
			} catch(NoSuchFieldError error){
				Toast.makeText(mContext, R.string.font_apply_only_in_meitu2, Toast.LENGTH_LONG).show();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	class AllListAdapter extends BaseAdapter {

		public AllListAdapter() {
		}

		@Override
		public int getCount() {
			return mFontFiles.size();
		}

		@Override
		public Object getItem(int postion) {
			return mFontFiles.get(postion);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder = null;
			if (view == null) {
				view = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_font_all, parent, false);
				holder = new ViewHolder(view);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}
			FontFile fontFile = mFontFiles.get(position);
			holder.mFontName.setText(fontFile.getFontDisplayName());
			holder.mFontSize.setText(fontFile.getFontSize());
			//TODO
			if (fontFile.isDownloaded()){
				holder.mDownload.setVisibility(View.GONE);
				holder.mApply.setVisibility(View.VISIBLE);
			} else {
				holder.mDownload.setVisibility(View.VISIBLE);
				holder.mApply.setVisibility(View.GONE);
			}
			FontItemOnClickListener fontListener = new FontItemOnClickListener(
					holder, position);
			holder.mPreview.setOnClickListener(fontListener);
			holder.mDownload.setOnClickListener(fontListener);
			holder.mApply.setOnClickListener(fontListener);
			return view;
		}
		
		class FontItemOnClickListener implements OnClickListener {

			private int mPostion;
			private ViewHolder mHolder;

			public FontItemOnClickListener(ViewHolder holder, int postion) {
				mPostion = postion;
				mHolder = holder;
			}

			@Override
			public void onClick(final View v) {
				final FontFile fontFile = mFontFiles.get(mPostion);
				switch (v.getId()) {
				case R.id.btn_preview:
					Intent preIntent = new Intent(getActivity(),PreviewActivity.class);
					preIntent.putExtra("previewUrl", fontFile.getFontNamePicUri());
					startActivity(preIntent);
					// TODO
					break;
				case R.id.btn_download:
					//删除代码，将两个按钮合并，在文件不存在的情况下，进行下载操作，不然直接应用字体
					/*
					// Toast.makeText(mContext, fontFile.getFontName(),
					// Toast.LENGTH_SHORT).show();
					DownloadFileAsync downloadTask = new DownloadFileAsync(
							mHolder);
//					downloadTask.execute(fontFile);
					downloadTask.executeOnExecutor(Executors.newSingleThreadExecutor(),fontFile);
					mHolder.mDownload.setVisibility(View.GONE);
					break;
					*/
				case R.id.btn_apply:
					
					String file = FileUtils.getSDCardPath()
							+ File.separatorChar + "download"
							+ File.separatorChar + fontFile.getFontName()
							+ ".apk";
					File fontApk = new File(file);
					if(!fontApk.exists()){
						DownloadFileAsync myDownloadTast = new DownloadFileAsync(
								mHolder);
						myDownloadTast.executeOnExecutor(Executors.newSingleThreadExecutor(),fontFile);
						mHolder.mDownload.setVisibility(View.GONE);
						mHolder.mApply.setVisibility(View.GONE);
						Toast.makeText(getActivity(), "正在下载...", Toast.LENGTH_SHORT).show();
						return;
					}
					PackageManager pm = mContext.getPackageManager();
					PackageInfo info = pm.getPackageArchiveInfo(file,
							PackageManager.GET_ACTIVITIES);
					String packageName = info.applicationInfo.packageName;
					int currentPoints = PointsHelper.getCurrentPoints(getActivity());
					if (!MainActivity.mConfig.isFree() && currentPoints < FontDetailActivity.NEED_POINTS && !SharedPreferencesHelper.isFontApplied(getActivity(), packageName)) {
						new AlertDialog.Builder(getActivity())
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
						ApplyFontAsyncTask applyFontTask = new ApplyFontAsyncTask();
//						applyFontTask.execute(fontFile);
						applyFontTask.executeOnExecutor(Executors.newSingleThreadExecutor(),fontFile);
					}
					

					break;

				default:
					break;
				}
			}
		}

		/**
		 * 
		 * @author
		 * 
		 */
		class ApplyFontAsyncTask extends AsyncTask<FontFile, Void, String> {

			private FontFile mFontFile;

			public ApplyFontAsyncTask() {
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(FontFile... fontFile) {
				mFontFile = fontFile[0];
				// get file packageName
				String file = FileUtils.getSDCardPath() + File.separatorChar
						+ "download" + File.separatorChar
						+ fontFile[0].getFontName() + ".apk";
				File f = new File(file);
				if (f.exists()){
					PackageManager pm = mContext.getPackageManager();
					PackageInfo info = pm.getPackageArchiveInfo(file,
							PackageManager.GET_ACTIVITIES);
					String packageName = info.applicationInfo.packageName;
					if (!ApkInstallHelper.checkProgramInstalled(mContext, packageName)){
						silentInstall(packageName, file);
					}
					return packageName;
				} else {
					return null;
				}
			}

			public void silentInstall(String packageName, String path) {
				try{
					Uri uri = Uri.fromFile(new File(path));
					PackageManager pm = mContext.getPackageManager();
					pm.installPackage(uri, null, 0, packageName);
				}catch (SecurityException e	){
					Log.e(TAG, e.getMessage());
					mHandler.sendEmptyMessage(MainActivity.NO_INSTALL_PERMISSION);
				} catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			protected void onPostExecute(String packageName) {
				super.onPostExecute(packageName);
				if (packageName != null){
					SharedPreferencesHelper.addToApplied(mContext,
							mFontFile.getFontName());
					if (ApkInstallHelper.checkProgramInstalled(mContext, packageName)){
						//应用安装过之后直接应用
						PackageManager pm = mContext.getPackageManager();
						List<PackageInfo> packegeInfoList = FontResUtil
								.getFontPackegeInfoList(pm);
						if (packageName!= null && packageName.contains("android.font")){
							Log.d(TAG, "font apk had installed ,applying it to system packageName=" + packageName);
							FontLoadTask task = new FontLoadTask(
									mContext.getPackageManager(), mContext, packegeInfoList);
//							task.execute(packageName);
							task.executeOnExecutor(Executors.newSingleThreadExecutor(),packageName);
						}
					}
				} else {
					Toast.makeText(mContext, "找不到对应的文件", Toast.LENGTH_SHORT).show();
				}
			}

		}


		/**
		 * 
		 * @author
		 * 
		 */
		class DownloadFileAsync extends AsyncTask<FontFile, String, String> {

			private ViewHolder mHolder;
			private FontFile mFontFile;

			public DownloadFileAsync(ViewHolder holder) {
				mHolder = holder;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mHolder.pb.setVisibility(View.VISIBLE);
			}

			@Override
			protected String doInBackground(FontFile... fontFile) {
				mFontFile = fontFile[0];
				FileOutputStream fos = null;
				try {
					// 连接地址
					URL u = new URL(fontFile[0].getFontUri());
					HttpURLConnection c = (HttpURLConnection) u
							.openConnection();
					// c.setRequestMethod("GET");
					// c.setDoOutput(true);
					// c.connect();

					// 计算文件长度
					int lenghtOfFile = c.getContentLength();

					String fileName = fontFile[0].getFontName() + ".apk.temp";
					File file = new File(FileUtils.getSDCardPath() + "/download");
					if (!file.exists()){
						file.mkdirs();
					}
					File fontFileTemp = new File(file.getAbsolutePath()+File.separatorChar+fileName);
					if(fontFileTemp.exists()){
						fontFileTemp.delete();
					}
					fos = new FileOutputStream(fontFileTemp); // TODO 文件处理细节

					InputStream in = c.getInputStream();

					// 下载的代码
					byte[] buffer = new byte[1024];
					int len = 0;
					long total = 0;

					while ((len = in.read(buffer)) > 0) {
						total += len; // total = total + len1
						publishProgress(""
								+ (int) ((total * 100) / lenghtOfFile));
						fos.write(buffer, 0, len);
					}

					fos.flush();
					
					InputStream stream = new FileInputStream(fontFileTemp);
					long size = stream != null? stream.available():0;
					if (total == size){
						boolean isSuccessed = fontFileTemp.renameTo(new File(file, fontFile[0].getFontName()+".apk"));
						if (isSuccessed){
							fontFileTemp.delete();
						}
					} else {
						fontFileTemp.delete();
					}
					if (stream !=null){
						stream.close();
					}

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
				}

				return null;
			}

			protected void onProgressUpdate(String... progress) {
				mHolder.pb.setProgress(Integer.parseInt(progress[0]));
			}

			@Override
			protected void onPostExecute(String unused) {
				// dismiss the dialog after the file was downloaded
				mHolder.pb.setVisibility(View.GONE);
				mHolder.mApply.setVisibility(View.VISIBLE);
				
			}

		}

		class ViewHolder {
			TextView mFontName, mFontSize;
			Button mPreview, mDownload, mApply;
			ProgressBar pb;

			public ViewHolder(View v) {
				mFontName = (TextView) v.findViewById(R.id.txt_font_name);
				mFontSize = (TextView) v.findViewById(R.id.txt_font_size);
				mPreview = (Button) v.findViewById(R.id.btn_preview);
				mDownload = (Button) v.findViewById(R.id.btn_download);
				mApply = (Button) v.findViewById(R.id.btn_apply);
				pb = (ProgressBar) v.findViewById(R.id.pb_download);
			}
		}

	}
}
