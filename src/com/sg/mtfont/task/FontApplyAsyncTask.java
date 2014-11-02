
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
public class FontApplyAsyncTask extends AsyncTask<JSONObject, Void, String> {

    public static final String TAG = FontApplyAsyncTask.class.getSimpleName();

    private JSONObject mJson;
    private Context mContext;

    public FontApplyAsyncTask() {}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... json) {
        try {
            mJson = json[0];
            // get file packageName
            String file = FileUtils.getSDCardPath() + File.separatorChar
                    + "download" + File.separatorChar + json[0].getString("name");
            File f = new File(file);
            if (f.exists()) {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(file,
                        PackageManager.GET_ACTIVITIES);
                String packageName = info.applicationInfo.packageName;
                if (!ApkInstallHelper.checkProgramInstalled(mContext, packageName)) {
                    ApkInstallHelper.silentInstall(mContext, packageName, file,
                            MainActivity.mHandler);
                }
                return packageName;
            }
            return null;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String packageName) {
        super.onPostExecute(packageName);
        if (packageName != null) {
            try {
                SharedPreferencesHelper.addToApplied(mContext,
                        mJson.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (ApkInstallHelper.checkProgramInstalled(mContext, packageName)) {
                // 应用安装过之后直接应用
                PackageManager pm = mContext.getPackageManager();
                List<PackageInfo> packegeInfoList = FontResUtil
                        .getFontPackegeInfoList(pm);
                if (packageName != null && packageName.contains("android.font")) {
                    Log.d(TAG,
                            "font apk had installed ,applying it to system packageName="
                                    + packageName);
                    FontLoadTask task = new FontLoadTask(
                            mContext.getPackageManager(), mContext,
                            packegeInfoList);
                    // task.execute(packageName);
                    task.executeOnExecutor(Executors.newSingleThreadExecutor(),
                            packageName);
                }
            }
        } else {
            Toast.makeText(mContext, "找不到对应的文件", Toast.LENGTH_SHORT).show();
        }
    }

}

class FontLoadTask extends AsyncTask<String, Void, FontResource> {

    Context mContext;
    List<PackageInfo> mPackegeInfoList;
    PackageManager mPackageManager;
    private WeakReference<ProgressDialog> mProgress;

    public FontLoadTask(PackageManager packageManager, Context context,
            List<PackageInfo> packegeInfoList) {
        this.mContext = context;
        this.mPackegeInfoList = packegeInfoList;
        this.mPackageManager = packageManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgress = new WeakReference<ProgressDialog>(ProgressDialog.show(
                mContext, null,
                mContext.getResources().getString(R.string.font_applying),
                true, false));
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
                if (!false) { // 如果不是免费版则需要消耗积分 TOOD
                    PointsHelper.spendPoints(mContext, Constant.NEED_POINTS);
                }
            }

            final ProgressDialog lProgress = mProgress.get();
            // Activity activity = getActivity();
            // boolean isFinish = false;
            // if(null != activity){
            // isFinish = activity.isFinishing();
            // }
            if (lProgress != null && lProgress.isShowing() /* && !isFinish */) {
                lProgress.dismiss();
                Toast.makeText(
                        mContext,
                        mContext.getResources().getString(
                                R.string.font_apply_success),
                        Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
        } catch (NoSuchFieldError error) {
            Toast.makeText(mContext, R.string.font_apply_only_in_meitu2,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
