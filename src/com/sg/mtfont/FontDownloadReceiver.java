package com.sg.mtfont;

import java.util.List;
import java.util.concurrent.Executors;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontquality.FontBoutiqueFragment;
import com.sg.mtfont.task.FontLoadTask;
import com.sg.mtfont.utils.ApkInstallHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

// TODO have not used
public class FontDownloadReceiver extends BroadcastReceiver {

	public static final String TAG = FontDownloadReceiver.class.getSimpleName();

	private FontBoutiqueFragment mFontFragment;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			// TODO 其他下载完毕，非本应用下载完毕对此的影响
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0); // TODO
			boolean isDownload = SharedPreferencesHelper.getSharepreferences(context).getBoolean(String.valueOf(id), false);
			if (isDownload){
				//TODO 下载完成做的处理
				Toast.makeText(context, "下载完成，正在安装字体...", Toast.LENGTH_SHORT).show();
				Query query = new Query();
				query.setFilterById(id); 
				DownloadManager dm = (DownloadManager)mFontFragment.getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
				Cursor c = dm.query(query); 
				if(c.moveToFirst()) {
					int columnIndex = c .getColumnIndex(DownloadManager.COLUMN_STATUS);
					if (DownloadManager.STATUS_SUCCESSFUL == c .getInt(columnIndex)){
						String filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
						String realPath = filePath.replace("file://", "");
						mFontFragment.checkPointsOrApplyFont(realPath);
					}
				}
			}
		} else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
			 Toast.makeText(context, "通知", Toast.LENGTH_LONG).show();
		} else if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_CHANGED)){
			Toast.makeText(context, "安装完毕咯，正在华丽变身中...", Toast.LENGTH_SHORT).show();
			String packageName = intent.getData().getSchemeSpecificPart();
			if (ApkInstallHelper.checkProgramInstalled(context, packageName)) {
                // 应用安装过之后直接应用
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> packegeInfoList = FontResUtil
                        .getFontPackegeInfoList(pm);
                if (packageName != null && packageName.contains("android.font")) {
                    Log.d(TAG,"font apk had installed ,applying it to system packageName=" + packageName);
                    FontLoadTask task = new FontLoadTask(
                            context.getPackageManager(), context,
                            packegeInfoList);
                    // task.execute(packageName);
                    task.executeOnExecutor(Executors.newSingleThreadExecutor(),
                            packageName);
                }
            } else {
            	Toast.makeText(context, "未安装这种字体", Toast.LENGTH_SHORT).show();
            }
		}
	}

	public void setup(FontBoutiqueFragment fontBoutiqueFragment) {
		mFontFragment = fontBoutiqueFragment;
	}
}
