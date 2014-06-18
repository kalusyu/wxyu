package com.sg.mtfont.fontall;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.sg.mtfont.R;
import com.sg.mtfont.task.AsynImageLoader;


public class PreviewActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String picUrl = getIntent().getStringExtra("previewUrl");
		setContentView(R.layout.preview_layout);
		ImageView imgPreview = (ImageView) findViewById(R.id.img_preview);
		AsynImageLoader asynImageLoader = new AsynImageLoader(this);
		asynImageLoader.showImageAsyn(imgPreview, picUrl, R.drawable.downloading_preview);
	}
}
