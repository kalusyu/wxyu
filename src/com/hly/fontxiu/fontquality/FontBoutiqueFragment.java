package com.hly.fontxiu.fontquality;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hly.fontxiu.R;
import com.hly.fontxiu.fontmanager.FontResUtil;
import com.hly.fontxiu.fontmanager.FontResource;

public class FontBoutiqueFragment extends Fragment implements OnClickListener {

	public static final String FONT_DETAIL_RESOURCE = "fontDetailResource";
	public static final String FONT_FILE_PATCH_RESOURCE = "fontFilePatchResource";
	
	public static final String FONT_FILENAME ="fontFileName";

	private List<View> fontsImageView = new ArrayList<View>();
	
	private WeakReference<ProgressDialog> mProgress;
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1){
//				final ProgressDialog lProgress = mProgress.get();
//				if (lProgress != null && lProgress.isShowing()) {
//					lProgress.dismiss();
//				}
				//Toast.makeText(getActivity(), "恢复成功", Toast.LENGTH_SHORT).show();
			}
		};
	};

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
		v1.setBackgroundResource(R.drawable.xiaonaipao);
		fontsImageView.add(v1);
		View v2 = view.findViewById(R.id.iv_font_2);
		v2.setBackgroundResource(R.drawable.cuojuehuiyi);
		fontsImageView.add(v2);
		View v3 = view.findViewById(R.id.iv_font_3);
		v3.setBackgroundResource(R.drawable.wuyunkuaizoukai);
		fontsImageView.add(v3);
		View v4 = view.findViewById(R.id.iv_font_4);
		v4.setBackgroundResource(R.drawable.jiangnandiao);
		fontsImageView.add(v4);
		View v5 = view.findViewById(R.id.iv_font_5);
		v5.setBackgroundResource(R.drawable.zhihualuo);
		fontsImageView.add(v5);
		for (View v : fontsImageView) {
			v.setOnClickListener(this);
		}
		Button btn = (Button)view.findViewById(R.id.btn_recover_system_font);
		btn.setOnClickListener(mRecoverSystemFontClickListener);
		return view;
	}
	
	OnClickListener mRecoverSystemFontClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			
//			mProgress = new WeakReference<ProgressDialog>(
//					ProgressDialog
//							.show(getActivity(),
//									null,
//									getResources().getString(
//											R.string.font_applying), true,
//									false));
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					FontResource fontRes = new FontResource("", "", "", null);
					FontResUtil.updateSysteFontConfiguration(fontRes);
					FontResUtil.saveSystemFontRes(getActivity(), fontRes);
					mHandler.sendEmptyMessage(1);
				}
			});
		}
	};

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(getActivity(), FontDetailActivity.class);
		int resoureceId = -1;
		String fontFilePath = null;
		String fontFileName = null;
		
		String[] str = new String[] { 
				"com.monotype.android.font.wuyunkuaizoukai",
				"com.monotype.android.font.xiaonaipaozhongwen",
				"com.monotype.android.font.zhihualuo",
				"com.monotype.android.font.cuojuehuiyi",
				"com.monotype.android.font.jiangnandiao" };
		String[] fileName = new String[]{
				 "wuyunkuaizoukai.apk",
				 "xiaonaipaozhongwen.apk",
				 "zhihualuo.apk",	
				 "cuojuehuiyi.apk",
				 "jiangnandiao.apk"
		};
		switch (arg0.getId()) {

		case R.id.iv_font_1:
			resoureceId = R.drawable.xiaonaipao;
			fontFilePath = str[4];
			fontFileName = fileName[4];
			break;
		case R.id.iv_font_2:
			resoureceId = R.drawable.cuojuehuiyi;
			fontFilePath = str[3];
			fontFileName = fileName[3];
			break;
		case R.id.iv_font_3:
			resoureceId = R.drawable.wuyunkuaizoukai;
			fontFilePath = str[2];
			fontFileName = fileName[2];
			break;
		case R.id.iv_font_4:
			resoureceId = R.drawable.jiangnandiao;
			fontFilePath = str[1];
			fontFileName = fileName[1];
			break;
		case R.id.iv_font_5:
			resoureceId = R.drawable.zhihualuo;
			fontFilePath = str[0];
			fontFileName = fileName[0];
			break;
		default:
			resoureceId = R.drawable.jiangnandiao;
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
