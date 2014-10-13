package com.sg.mtfont;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sg.mtfont.bean.DeviceInfo;
import com.sg.mtfont.bean.FontFile;
import com.sg.mtfont.utils.CommonUtils;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.HttpRequestUtils;
import com.sg.mtfont.xml.Config;
import com.sg.mtfont.xml.XmlUtils;



public class SplashActivity extends Activity{
	
	public static final String SHARE_PREFER_KEYS = "share_prefer_keys";
	public static final String LAUNCH_APP_FIRST = "launch_app_first";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
		checkIfFirstTimeLaunchApp();
		//TODO request need data
		GetFontFileAsyncTask fontTask = new GetFontFileAsyncTask(this);
		fontTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		//TODO progress tips
	}
	
	/**
	 * 
	 * check application first launch
	 * KaluYu
	 * 2014年10月13日 下午10:29:10
	 */
	private void checkIfFirstTimeLaunchApp() {
		SharedPreferences sp = getSharedPreferences(SHARE_PREFER_KEYS, Context.MODE_PRIVATE);
		boolean launchFirstTime = sp.getBoolean(LAUNCH_APP_FIRST, true);
		// first launch application
		if (launchFirstTime){
			SendInfoAsyncTask t = new SendInfoAsyncTask(this);
			t.executeOnExecutor(Executors.newSingleThreadExecutor(), CommonUtils.getDeviceInfo(this));
			sp.edit().putBoolean(LAUNCH_APP_FIRST, false).apply();
		}		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
}

/**
 * 
 * @author Kalus Yu
 *
 */
class GetFontFileAsyncTask extends AsyncTask<Void, Void, ArrayList<FontFile>>{
    Context mContext;
    
    public GetFontFileAsyncTask(Context ctx) {
        mContext = ctx;
    }

    @Override
    protected ArrayList<FontFile> doInBackground(Void... arg0) {
        return HttpRequestUtils.getFontFileLists();
    }
    
    @Override
    protected void onPostExecute(ArrayList<FontFile> result) {
        Intent it = new Intent(mContext, MainActivity.class);
        it.putExtra(Constant.FONTFILE, result);
        mContext.startActivity(it);
        if (mContext instanceof SplashActivity){
            ((SplashActivity)mContext).finish();
        }
    }
    
}

/**
 * 
 * @author Kalus Yu
 *
 */
class SendInfoAsyncTask extends AsyncTask<DeviceInfo, Void, Integer>{
    Context mContext;
    
    public SendInfoAsyncTask(Context ctx) {
        mContext = ctx;
    }

    @Override
    protected Integer doInBackground(DeviceInfo... arg0) {
        DeviceInfo info = arg0[0];
        int responseCode = HttpRequestUtils.sendDeviceInfo(info);
        return responseCode;
    }
    
    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }
    
}


/**
 * 
 * @author Kalus Yu
 * NO USE NOW  for reference
 */
class ReadAsyncTask extends AsyncTask<Void, Void, Config>{
	
	Context mContext;
	
	public ReadAsyncTask(Context ctx) {
		mContext = ctx;

	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}
	

	@Override
	protected Config doInBackground(Void... params) {
		return readConfig();
	}
	
	private Config readConfig() {
		Config cfg = new Config();
		String path = mContext.getFilesDir().getPath();
		File file = new File(path + File.separatorChar + "config.txt");
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			XmlUtils.readConfig(cfg,fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return cfg;
		
	}

	@Override
	protected void onPostExecute(Config result) {
		super.onPostExecute(result);
		Intent it = new Intent(mContext, MainActivity.class);
		it.putExtra("config", result);
		mContext.startActivity(it);
		if (mContext instanceof SplashActivity){
			((SplashActivity)mContext).finish();
		}
	}
	
}
