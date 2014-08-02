package com.sg.mtfont;

import com.sg.mtfont.utils.SharedPreferencesHelper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
// TODO have not used
public class FontDownloadReceiver extends BroadcastReceiver{

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
			}
		} else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
			 Toast.makeText(context, "通知", Toast.LENGTH_LONG).show();
		}
	}
}
