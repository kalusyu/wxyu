package com.hly.fontxiu.fontquality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hly.fontxiu.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FontBoutiqueFragment extends Fragment implements OnClickListener {

	public static final String FONT_DETAIL_RESOURCE = "fontDetailResource";
	public static final String FONT_FILE_PATCH_RESOURCE = "fontFilePatchResource";
	
	public static final String FONT_FILENAME ="fontFileName";

	private List<View> fontsImageView = new ArrayList<View>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_font_boutique, null,
				false);
		View v1 = view.findViewById(R.id.iv_font_1);
		v1.setBackgroundResource(R.drawable.font_small1);
		fontsImageView.add(v1);
		View v2 = view.findViewById(R.id.iv_font_2);
		v2.setBackgroundResource(R.drawable.font_small2);
		fontsImageView.add(v2);
		View v3 = view.findViewById(R.id.iv_font_3);
		v3.setBackgroundResource(R.drawable.font_small3);
		fontsImageView.add(v3);
		View v4 = view.findViewById(R.id.iv_font_4);
		v4.setBackgroundResource(R.drawable.font_small4);
		fontsImageView.add(v4);
		View v5 = view.findViewById(R.id.iv_font_5);
		v5.setBackgroundResource(R.drawable.font_small5);
		fontsImageView.add(v5);
		for (View v : fontsImageView) {
			v.setOnClickListener(this);
		}
		return view;
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(getActivity(), FontDetailActivity.class);
		int resoureceId = -1;
		String fontFilePath = null;
		String fontFileName = null;
		
		String[] str = new String[] { 
				"com.hly.android.font.huakang",
				"com.hly.android.font.wawa",
				"com.hly.android.font.tianranmeng",
				"com.hly.android.font.qingniao",
				"com.hly.android.font.jiandaiyu" };
		String[] fileName = new String[]{
				 "Font_Huakangshaonv.apk",
				 "Font_Waiwai.apk",
				 "TianRanMeng.apk",	
				 "QingNiao.apk",
				 "MiniJianDaiyu.apk"
		};
		switch (arg0.getId()) {

		case R.id.iv_font_1:
			resoureceId = R.drawable.font_small1;
			fontFilePath = str[4];
			fontFileName = fileName[4];
			break;
		case R.id.iv_font_2:
			resoureceId = R.drawable.font_small2;
			fontFilePath = str[3];
			fontFileName = fileName[3];
			break;
		case R.id.iv_font_3:
			resoureceId = R.drawable.font_small3;
			fontFilePath = str[2];
			fontFileName = fileName[2];
			break;
		case R.id.iv_font_4:
			resoureceId = R.drawable.font_small4;
			fontFilePath = str[1];
			fontFileName = fileName[1];
			break;
		case R.id.iv_font_5:
			resoureceId = R.drawable.font_small5;
			fontFilePath = str[0];
			fontFileName = fileName[0];
			break;
		default:
			resoureceId = R.drawable.font_small1;
			fontFilePath = str[0];
			fontFileName = fileName[0];
			break;
		}
		intent.putExtra(FONT_DETAIL_RESOURCE, resoureceId);
		intent.putExtra(FONT_FILE_PATCH_RESOURCE, fontFilePath);
		intent.putExtra(FONT_FILENAME, fontFileName);
		startActivity(intent);
	}
}
