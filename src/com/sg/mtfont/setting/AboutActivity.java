package com.sg.mtfont.setting;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.sg.mtfont.R;


public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.about_layout);
		TextView tv = (TextView) findViewById(R.id.txt_version);
		
		PackageManager pm = getPackageManager();//context为当前Activity上下文 
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(getPackageName(), 0);
			String versionName = pi.versionName;
			tv.setText("版本号："+versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
