package com.hly.fontxiu.fontall;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.hly.fontxiu.R;
import com.hly.fontxiu.task.AsynImageLoader;

public class PreviewActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String picUrl = getIntent().getStringExtra("previewUrl");
		setContentView(R.layout.preview_layout);
		ImageView imgPreview = (ImageView) findViewById(R.id.img_preview);
		AsynImageLoader asynImageLoader = new AsynImageLoader();
		asynImageLoader.showImageAsyn(imgPreview, picUrl, R.drawable.downloading_preview);
	}
}
