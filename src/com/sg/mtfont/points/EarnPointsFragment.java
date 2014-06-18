package com.sg.mtfont.points;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import net.youmi.android.offers.OffersAdSize;
import net.youmi.android.offers.OffersBanner;
import net.youmi.android.offers.OffersManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sg.mtfont.MainActivity;
import com.sg.mtfont.R;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.xml.Config;
import com.sg.mtfont.xml.XmlUtils;

public class EarnPointsFragment extends Fragment implements OnClickListener{
	
	public static final String TAG = "EarnPointsFragment";
	
	/**
	 * 积分 Banner
	 */
	private OffersBanner mBanner;
	/**
	 * 积分 Mini Banner
	 */
	private OffersBanner mMiniBanner;
	
	/**
	 * 显示积分余额的控件
	 */
	TextView mTextViewPoints;
	TextView mTextPointsIndroduce;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.earn_points, container,false);
		Button btn = (Button)v.findViewById(R.id.btn_earn_points);
		btn.setOnClickListener(this);
		initUI(v);
		
		Button buy = (Button) v.findViewById(R.id.btn_buyfree);
		buy.setOnClickListener(this);
		
		
		// (可选)使用积分Mini Banner-一个新的积分墙入口点，随时随地让用户关注新的积分广告
		mMiniBanner = new OffersBanner(getActivity(), OffersAdSize.SIZE_MATCH_SCREENx32);//
		RelativeLayout layoutOffersMiniBanner = (RelativeLayout) v.findViewById(R.id.OffersMiniBannerLayout);
		layoutOffersMiniBanner.addView(mMiniBanner);

		// (可选)使用积分Banner-一个新的积分墙入口点，随时随地让用户关注新的积分广告
		mBanner = new OffersBanner(getActivity(), OffersAdSize.SIZE_MATCH_SCREENx60);
		RelativeLayout layoutOffersBanner = (RelativeLayout) v.findViewById(R.id.offersBannerLayout);
		layoutOffersBanner.addView(mBanner);
		
		return v;
	}
	
	public void buyFree(View v){
		if (PointsHelper.getCurrentPoints(getActivity()) < 1000){
			Toast.makeText(getActivity(), R.string.no_much_points, Toast.LENGTH_SHORT).show();
		} else {
			Config cfg =new Config();
			cfg.setFree(true);
			String path = Environment.getRootDirectory().getPath();
			File file = new File(path + File.pathSeparatorChar + "config.txt");
			OutputStream out = null;
			try {
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			XmlUtils.saveConfig(cfg, out);
//			mAQ.id(R.id.btn_earn_points).visibility(View.GONE);
//			mBanner.setVisibility(View.GONE);
			Toast.makeText(getActivity(), R.string.buy_success_tips, Toast.LENGTH_SHORT).show();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					MainActivity.sendPhoneInfo("for free version:",getActivity());
				}
			}).start();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.hly.fontxiu.showpoints");
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	private void fillText() {
		StringBuilder sbP = new StringBuilder();
		sbP.append("当前积分："+ PointsHelper.getCurrentPoints(getActivity()))
		.append("\n1.美图手机2字体福利到：只要您的积分大于等于1000分就可以购买永久免费版了！\n2.购买完成之后，退出程序，长安Home键，即中间的那个键，到多任务删除美图手机2字体应用，然后重新打开就可以享受免费无广告版的美图手机2换字体了");
		mTextViewPoints.setText(sbP.toString());
		StringBuilder sb =  new StringBuilder();
		sb.append("\n\n积分规则：").append("\n\n")
		.append("1. 用户初次登陆应用奖励").append(MainActivity.sAwardPoints).append("积分，使用字体需要有足够的积分才可以使用。").append("\n\n")
		.append("2. 获取积分途径:").append("\n\n")
		.append("   2.1) 通过点击页面显示的广告赚取.").append("\n")
		.append("   2.2) 通过点击获取更多积分按钮.").append("\n\n")
		.append("3. 点击进入下载页面，按照要求下载使用即可获取对应的积分。").append("\n\n")
		.append("4. 当有足够的积分时，每应用一种新字体都会消耗对应的积分，旧字体可任意切换。").append("\n\n")
		.append("\n").append("\n")
		.append("附：如您对我们的产品有任何疑问，请点击右上角的问题反馈，我们将及时采纳！").append("\n");
		
		mTextPointsIndroduce.setText(sb.toString());
       
	}

	private void initUI(View v) {
		mTextViewPoints = (TextView) v.findViewById(R.id.txt_points);
		mTextPointsIndroduce = (TextView) v.findViewById(R.id.txt_points_indroduce);
	}

	@Override
	public void onResume() {
		super.onResume();
		// OffersManager.getInstance(getActivity()).showOffersSpot();
		// 自定义插播的出现方式
		// OffersManager.getInstance(this).showOffersSpot(OffersManager.STYLE_SPOT_TOP_DOWN_REVERSE);
		fillText();
	}
	
	public void onDestroy() {
		Activity activity = getActivity();
		try {
			if (activity != null) {
				activity.unregisterReceiver(mReceiver);
			}
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage());
		}
		super.onDestroy();
	};

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			fillText();
		}
	};

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_earn_points){
			OffersManager.getInstance(getActivity()).showOffersWall();
		} else if (view.getId() == R.id.btn_buyfree){
			buyFree(view);
		}
		
	}
	
}
