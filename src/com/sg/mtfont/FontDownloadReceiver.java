package com.sg.mtfont;

import java.util.List;
import java.util.concurrent.Executors;

import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontquality.FontBoutiqueFragment;
import com.sg.mtfont.task.FontLoadTask;
import com.sg.mtfont.utils.ApkInstallHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
// TODO have not used
public class FontDownloadReceiver extends BroadcastReceiver{
	
	public static final String TAG = FontDownloadReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0); // TODO
			boolean isDownload = SharedPreferencesHelper.getSharepreferences(context).getBoolean(String.valueOf(id), false);
			if (isDownload){
//				ViewHolder holder = sBgMaps.get(id);
//				holder.mApply.setVisibility(View.VISIBLE);
				//TODO 下载完成做的处理
				Toast.makeText(context, "下载完成，正在安装字体...", Toast.LENGTH_SHORT).show();
				
			}
		} else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
			 Toast.makeText(context, "通知", Toast.LENGTH_LONG).show();
		} else if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_CHANGED)){
			String packageName = intent.getData().getSchemeSpecificPart();
			if (ApkInstallHelper.checkProgramInstalled(context, packageName)) {
                // 应用安装过之后直接应用
                PackageManager pm = context.getPackageManager();
                List<PackageInfo> packegeInfoList = FontResUtil
                        .getFontPackegeInfoList(pm);
                if (packageName != null && packageName.contains("android.font")) {
                    Log.d(TAG,
                            "font apk had installed ,applying it to system packageName="
                                    + packageName);
                    FontLoadTask task = new FontLoadTask(
                            context.getPackageManager(), context,
                            packegeInfoList);
                    // task.execute(packageName);
                    task.executeOnExecutor(Executors.newSingleThreadExecutor(),
                            packageName);
                }
            }
		}
	}

	public void setup(FontBoutiqueFragment fontBoutiqueFragment) {
		
	}
}
