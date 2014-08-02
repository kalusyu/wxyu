package com.sg.mtfont.task;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sg.mtfont.MainActivity;
import com.sg.mtfont.R;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.fontquality.FontDetailActivity;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

/**
 * 
 * @author Kalus Yu
 * change system font
 */
public class ChangeFontLoaderTask extends AsyncTask<String, Void, FontResource> {

	public static final String TAG = "ChangeFontLoaderTask";

	Context mContext;
	List<PackageInfo> mPackegeInfoList;
	PackageManager mPackageManager;

	public ChangeFontLoaderTask(PackageManager packageManager,
			FontDetailActivity fontDetailActivity,
			List<PackageInfo> packegeInfoList) {
		this.mContext = fontDetailActivity;
		this.mPackegeInfoList = packegeInfoList;
		this.mPackageManager = packageManager;
	}

	@Override
	protected FontResource doInBackground(String... arg0) {
		List<FontResource> packageFontResList = FontResUtil
				.assembleFontResourceFromPackage(mPackageManager, arg0[0]);
		return packageFontResList.get(0);
	}

	@Override
	protected void onPostExecute(FontResource result) {
		try {
			FontResource fontRes = result;
			FontResUtil.updateSysteFontConfiguration(fontRes);
			FontResUtil.saveSystemFontRes(mContext, fontRes);
			if (!SharedPreferencesHelper.isFontApplied(mContext,
					fontRes.getPackageName())) {
				SharedPreferencesHelper.addToApplied(mContext,
						fontRes.getPackageName());
				if (!MainActivity.mConfig.isFree()) {// 如果不是免费版则需要消耗积分
					PointsHelper.spendPoints(mContext, Constant.NEED_POINTS);
				}
			}
			Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
			if (mContext instanceof Activity) {
				((Activity) mContext).finish();
			}
		} catch (NoSuchFieldError e) {
			Log.e(TAG, e.getMessage());
			Toast.makeText(mContext, R.string.font_apply_only_in_meitu2,
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
