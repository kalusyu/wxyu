package com.hly.fontxiu.setting;

import net.youmi.android.AdManager;
import net.youmi.android.dev.AppUpdateInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * 这里示例一个调用更新应用接口的工具类，由开发者自定义，继承自 AsyncTask
 */
public class UpdateHelper extends AsyncTask<Void, Void, AppUpdateInfo> {
	private Context mContext;

	public UpdateHelper(Context context) {
		mContext = context;
	}

	@Override
	protected AppUpdateInfo doInBackground(Void... params) {
		try {
			// 在 doInBackground 中调用 AdManager 的 checkAppUpdate 即可从有米服务器获得应用更新信息。
			return AdManager.getInstance(mContext).syncCheckAppUpdate();
			// 此方法务必在非 UI 线程调用，否则有可能不成功。
		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(final AppUpdateInfo result) {
		super.onPostExecute(result);
		try {
			if (result == null || result.getUrl() == null) {
				// 如果 AppUpdateInfo 为 null 或它的 url 属性为 null，则可以判断为没有新版本。
				Toast.makeText(mContext, "当前版本已经是最新版", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			// 这里简单示例使用一个对话框来显示更新信息
			new AlertDialog.Builder(mContext)
					.setTitle("发现新版本")
					.setMessage(result.getUpdateTips())
					// 这里是版本更新信息
					.setNegativeButton("马上升级",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Intent.ACTION_VIEW, Uri
													.parse(result.getUrl()));
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									mContext.startActivity(intent);
									// TODO
									// ps：这里示例点击“马上升级”按钮之后简单地调用系统浏览器进行新版本的下载，
									// 但强烈建议开发者实现自己的下载管理流程，这样可以获得更好的用户体验。
								}
							})
					.setPositiveButton("下次再说",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).create().show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}