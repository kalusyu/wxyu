package com.hly.fontxiu;

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

import com.hly.fontxiu.xml.Config;
import com.hly.fontxiu.xml.XmlUtils;

public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
//		AQuery aq = new AQuery(this);
//		aq.id(R.id.img_splash).image("http://www.vikispot.com/z/images/vikispot/android-w.png");//TODO
		ReadAsyncTask task = new ReadAsyncTask(this);
		task.execute();
		
	}
	
	
}

class ReadAsyncTask extends AsyncTask<Void, Void, Config>{
	
	ProgressDialog pd;
	Context mContext;
	
	public ReadAsyncTask(Context ctx) {
		mContext = ctx;
		pd = new ProgressDialog(ctx);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (pd != null){
			pd.setTitle("");
			pd.setMessage("");
			pd.show();
		}
	}

	@Override
	protected Config doInBackground(Void... params) {
		return readConfig();
	}
	
	private Config readConfig() {
		Config cfg = new Config();
		String path = Environment.getRootDirectory().getPath();
		File file = new File(path + File.pathSeparatorChar + "config.txt");
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
		if (pd != null){
			pd.dismiss();
			pd = null;
		}
		Intent it = new Intent(mContext, MainActivity.class);
		it.putExtra("config", result);
		mContext.startActivity(it);
	}
	
}
