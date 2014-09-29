package com.sg.mtfont;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sg.mtfont.bean.DeviceInfo;
import com.sg.mtfont.utils.CommonUtils;
import com.sg.mtfont.utils.HttpRequestUtils;
import com.sg.mtfont.xml.Config;
import com.sg.mtfont.xml.XmlUtils;



public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
//		ReadAsyncTask task = new ReadAsyncTask(this);
//		task.execute();
		SendInfoAsyncTask t = new SendInfoAsyncTask(this);
		//t.execute(CommonUtils.getDeviceInfo(this));
		t.executeOnExecutor(Executors.newSingleThreadExecutor(), CommonUtils.getDeviceInfo(this));
		//TODO request need data
		//TODO progress tips
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
class SendInfoAsyncTask extends AsyncTask<DeviceInfo, Void, Integer>{
    Context mContext;
    
    public SendInfoAsyncTask(Context ctx) {
        mContext = ctx;
    }

    @Override
    protected Integer doInBackground(DeviceInfo... arg0) {
        DeviceInfo info = arg0[0];
//        int responseCode = HttpRequestUtils.sendJsonToServer(info);
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
 *
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
