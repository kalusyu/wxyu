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
import android.util.Log;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sg.mtfont.bean.DeviceInfo;
import com.sg.mtfont.bean.FontFile;
import com.sg.mtfont.utils.CommonUtils;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.FontRestClient;
import com.sg.mtfont.utils.HttpUtils;
import com.sg.mtfont.xml.Config;
import com.sg.mtfont.xml.XmlUtils;

import org.apache.http.Header;
import org.json.JSONArray;


public class SplashActivity extends Activity implements IAsyncTaskHandler{
	
	public static final String TAG = SplashActivity.class.getSimpleName();
	
	public static final String SHARE_PREFER_KEYS = "share_prefer_keys";
	public static final String LAUNCH_APP_FIRST = "launch_app_first";
	
	private TextView mTxtProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		mTxtProgress = (TextView) findViewById(R.id.txt_progress);
		
		checkIfFirstTimeLaunchApp();
		//TODO request need data
//		GetFontFileAsyncTask fontTask = new GetFontFileAsyncTask(this);
//		fontTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		FontRestClient.post(Constant.getFontInfo + "0-4", null, new JsonHttpResponseHandler(){
    		@Override
    		public void onSuccess(int statusCode, Header[] headers,
    				JSONArray response) {
    			Log.d(TAG, "ybw:"+response.toString());
    		}
    		
    		@Override
    		public void onFailure(int statusCode, Header[] headers,
    				String responseString, Throwable throwable) {
    			// TODO Auto-generated method stub
    			super.onFailure(statusCode, headers, responseString, throwable);
    		}
    	});
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

	@Override
	public int receiveData(Object o) {
		if (o instanceof Integer){
			Integer i = (Integer) o;
			switch (i) {
			case 1:
				mTxtProgress.setText("初始化设置。。。");
				break;
			case 2:
				mTxtProgress.setText("获取数据。。。");
				break;
			case 3:
				mTxtProgress.setText("页面渲染中。。。");
				break;
			case 4:
				mTxtProgress.setText("加载完成！");
				break;

			default:
				break;
			}
		}
		return 0;
	}

	@Override
	public int onDataLoadCompleted(ArrayList<FontFile> result) {
		Intent it = new Intent(this, MainActivity.class);
        it.putExtra(Constant.FONTFILE, result);
        startActivity(it);
        finish();
		return 0;
	}
	
	
}

/**
 * 
 * @author Kalus Yu
 *
 */
class GetFontFileAsyncTask extends AsyncTask<Void, Integer, ArrayList<FontFile>>{
    IAsyncTaskHandler mHander;
    
    public GetFontFileAsyncTask(IAsyncTaskHandler handler) {
        mHander = handler;
    }

    @Override
    protected ArrayList<FontFile> doInBackground(Void... arg0) {
    	publishProgress(1);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	publishProgress(2);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	publishProgress(3);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	publishProgress(4);
        return HttpUtils.getFontFileLists();
    }
    
    @Override
    protected void onPostExecute(ArrayList<FontFile> result) {
        mHander.onDataLoadCompleted(result);
    }
    
    @Override
    protected void onProgressUpdate(Integer... values) {
    	mHander.receiveData(values[0]);
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
        int responseCode = HttpUtils.sendDeviceInfo(info);
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
