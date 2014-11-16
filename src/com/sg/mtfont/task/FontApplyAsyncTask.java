package com.sg.mtfont.task;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sg.mtfont.MainActivity;
import com.sg.mtfont.R;
import com.sg.mtfont.bean.FontFile;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.utils.ApkInstallHelper;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.FileUtils;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

/**
 * 
 * @author Kalus Yu
 * 
 */
public class FontApplyAsyncTask extends AsyncTask<String, Void, String> {

	public static final String TAG = FontApplyAsyncTask.class.getSimpleName();

	private String mPath;
	private Context mContext;

	public FontApplyAsyncTask(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... path) {
		try {
			mPath = path[0];
			// get file packageName
			String file = path[0];
			File f = new File(file);
			if (f.exists()) {
				PackageManager pm = mContext.getPackageManager();
				PackageInfo info = pm.getPackageArchiveInfo(file,
						PackageManager.GET_ACTIVITIES);
				String packageName = info.applicationInfo.packageName;
				if (!ApkInstallHelper.checkProgramInstalled(mContext,
						packageName)) {
					ApkInstallHelper.silentInstall(mContext, packageName, file,
							MainActivity.mHandler);
				}
				return packageName;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG,"install font apk failed, fail message is e.getMessage()="+ e.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(String packageName) {
		super.onPostExecute(packageName);
		if (packageName != null) {
			File f = new File(mPath);
			SharedPreferencesHelper.addToApplied(mContext, f.getName());
		} else {
			Toast.makeText(mContext, "找不到对应的文件", Toast.LENGTH_SHORT).show();
		}
	}

}
