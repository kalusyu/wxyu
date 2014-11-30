package com.sg.mtfont.task;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sg.mtfont.R;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;

public class FontLoadTask extends AsyncTask<String, Void, FontResource> {

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
                if (!false) { // 如果不是免费版则需要消耗积分 TODO
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
        } catch (NoSuchFieldError error) {
            Toast.makeText(mContext, R.string.font_apply_only_in_meitu2,
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}