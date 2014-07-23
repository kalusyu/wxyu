package com.sg.mtfont;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import com.sg.mtfont.xml.Config;
import com.sg.mtfont.xml.XmlUtils;



public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		ReadAsyncTask task = new ReadAsyncTask(this);
		task.execute();
		//TODO request need data
		//TODO progress tips
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
}

class ReadAsyncTask extends AsyncTask<Void, Void, Config>{
	
//	ProgressDialog pd;
	Context mContext;
	
	public ReadAsyncTask(Context ctx) {
		mContext = ctx;
//		pd = new ProgressDialog(ctx);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
//		if (pd != null){
//			pd.setTitle("");
//			pd.setMessage("");
//			pd.show();
//		}
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
//		if (pd != null){
//			pd.dismiss();
//			pd = null;
//		}
		Intent it = new Intent(mContext, MainActivity.class);
		it.putExtra("config", result);
		mContext.startActivity(it);
		if (mContext instanceof SplashActivity){
			((SplashActivity)mContext).finish();
		}
	}
	
}
