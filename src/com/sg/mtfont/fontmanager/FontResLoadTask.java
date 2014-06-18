package com.sg.mtfont.fontmanager;

import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

public class FontResLoadTask extends AsyncTask<Void, FontResource, Integer> {
    private List<PackageInfo> mFontPackegeInfoList = null;
    private IFontResDataReceiver mFontResDataReceiver = null;
    private PackageManager mPackageManager = null;
    
    public FontResLoadTask(PackageManager pm, List<PackageInfo> packegeInfoList, IFontResDataReceiver dataReceiver) {
        mPackageManager = pm;
        mFontPackegeInfoList = packegeInfoList;
        mFontResDataReceiver = dataReceiver;
    }

    @Override
    protected void onPreExecute() {
    }
    
    @Override
    protected Integer doInBackground(Void... params) {
        for(int i = 0; i < mFontPackegeInfoList.size(); i ++) {
            PackageInfo packageInfo = mFontPackegeInfoList.get(i);
            List<FontResource> packageFontResList = FontResUtil.assembleFontResourceFromPackage(mPackageManager, packageInfo.packageName);
            if (packageFontResList == null || packageFontResList.isEmpty()) {
                continue;
            }
            for (int j = 0; j < packageFontResList.size(); j++) {
                publishProgress(packageFontResList.get(j));
            }
        }
        return null;
    }
    
    @Override
    protected void onPostExecute(Integer result) {
        mFontResDataReceiver.onFontResLoadCompleted();
    }

    @Override
    protected void onCancelled(Integer result) {
    };

    @Override
    protected void onProgressUpdate(FontResource... values) {
        mFontResDataReceiver.receiveFontResData(values[0]);
    }
}
