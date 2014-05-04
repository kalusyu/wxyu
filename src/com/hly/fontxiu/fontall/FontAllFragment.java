package com.hly.fontxiu.fontall;

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
import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
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

import com.hly.fontxiu.R;
import com.hly.fontxiu.bean.FontFile;
import com.hly.fontxiu.fontmanager.FontResUtil;
import com.hly.fontxiu.fontmanager.FontResource;
import com.hly.fontxiu.utils.FileUtils;
import com.hly.fontxiu.utils.SharedPreferencesHelper;

public class FontAllFragment extends ListFragment {

	private BaseAdapter mAdapter;

	private Context mContext;

	private ArrayList<String> mFonts = new ArrayList<String>();

	private List<FontFile> mFontFiles;

	private WeakReference<ProgressDialog> mProgress;
	
	private static final String TAG = "FontAllFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_font_all, container,
				false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		loadFontRes(mContext);
		loadInstalledFont();
		if (mFontFiles != null) {
			mAdapter = new AllListAdapter(mFontFiles.size());
		}
		setListAdapter(mAdapter);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		getActivity().registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String pkgName = intent.getDataString();
			PackageManager pm = mContext.getPackageManager();
			List<PackageInfo> packegeInfoList = FontResUtil
					.getFontPackegeInfoList(pm);
			if (pkgName.contains("android.font")){
				Log.d(TAG, "font apk had installed ,applying it to system packageName=" + pkgName);
				int index = pkgName.indexOf(":");
				String packageName = pkgName.substring(index + 1, pkgName.length());
				FontLoadTask task = new FontLoadTask(
						mContext.getPackageManager(), mContext, packegeInfoList);
				task.execute(packageName);
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
				boolean isInstalled = SharedPreferencesHelper.isFontInstalled(
						mContext, fontFile.getFontName());
				fontFile.setInstalled(isInstalled);
			}
		}
	}

	/**
	 * 
	 * @param context
	 */
	private void loadFontRes(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String fontPath = path + File.separatorChar
					+ context.getResources().getString(R.string.font_path);
			File file = new File(fontPath);
			file.mkdirs();
			if (file.exists()) {
				for (File child : file.listFiles()) {
					if (child.getName().equals("fontlist.xml")) {
						try {
							mFontFiles = FileUtils
									.parseXmlFile(new FileInputStream(child));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						mFonts.add(child.getAbsolutePath());
					}
				}
			} else {
				Toast.makeText(getActivity(),
						R.string.font_download_file_is_not_exist,
						Toast.LENGTH_SHORT).show();
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
		protected FontResource doInBackground(String... arg0) {
			List<FontResource> packageFontResList = FontResUtil
					.assembleFontResourceFromPackage(mPackageManager,
							arg0[0]);
			return packageFontResList.get(0);
		}

		@Override
		protected void onPostExecute(FontResource result) {
			FontResource fontRes = result;
			FontResUtil.updateSysteFontConfiguration(fontRes);
			FontResUtil.saveSystemFontRes(mContext, fontRes);
			// SharedPreferencesHelper.addToInstall(mContext,
			// fontRes.getPackageName());
			// PointsHelper.spendPoints(mContext, NEED_POINTS);
			Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
		}
	}

	class AllListAdapter extends BaseAdapter {
		int mFontCount;

		public AllListAdapter(int fontCount) {
			mFontCount = fontCount;
		}

		@Override
		public int getCount() {
			return mFontCount;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
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
			holder.mFontName.setText(fontFile.getFontName());
			holder.mFontSize.setText(fontFile.getFontSize());
			//TODO
			if (fontFile.isInstalled()){
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
					// TODO
					break;
				case R.id.btn_download:
					// Toast.makeText(mContext, fontFile.getFontName(),
					// Toast.LENGTH_SHORT).show();
					DownloadFileAsync downloadTask = new DownloadFileAsync(
							mHolder);
					downloadTask.execute(fontFile);
					mHolder.mDownload.setVisibility(View.GONE);
					break;
				case R.id.btn_apply:
					ApplyFontAsyncTask applyFontTask = new ApplyFontAsyncTask();
					applyFontTask.execute(fontFile);

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
				mProgress = new WeakReference<ProgressDialog>(
						ProgressDialog
								.show(mContext,
										null,
										getResources().getString(
												R.string.font_applying), true,
										false));
			}

			@Override
			protected String doInBackground(FontFile... fontFile) {
				mFontFile = fontFile[0];
				// get file packageName
				String file = FileUtils.getSDCardPath() + File.separatorChar
						+ "download" + File.separatorChar
						+ fontFile[0].getFontName() + ".apk";
				PackageManager pm = mContext.getPackageManager();
				PackageInfo info = pm.getPackageArchiveInfo(file,
						PackageManager.GET_ACTIVITIES);
				String packageName = info.applicationInfo.packageName;
				silentInstall(packageName, file);
				return packageName;
			}

			public void silentInstall(String packageName, String path) {
				Uri uri = Uri.fromFile(new File(path));
				PackageManager pm = mContext.getPackageManager();
				pm.installPackage(uri, null, 0, packageName);
			}

			@Override
			protected void onPostExecute(String packageName) {
				super.onPostExecute(packageName);

				final ProgressDialog lProgress = mProgress.get();
				if (lProgress != null && lProgress.isShowing()
						&& !getActivity().isFinishing()) {
					lProgress.dismiss();
					Toast.makeText(
							mContext,
							getResources().getString(
									R.string.font_apply_success),
							Toast.LENGTH_SHORT).show();
				}				
				SharedPreferencesHelper.addToInstall(mContext,
						mFontFile.getFontName());
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

					String fileName = fontFile[0].getFontName() + ".apk";
					fos = new FileOutputStream(new File(
							FileUtils.getSDCardPath() + "/download/", fileName)); // TODO
																					// 文件处理细节

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
