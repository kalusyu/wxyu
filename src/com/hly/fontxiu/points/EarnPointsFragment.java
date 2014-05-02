package com.hly.fontxiu.points;

import net.youmi.android.offers.OffersAdSize;
import net.youmi.android.offers.OffersBanner;
import net.youmi.android.offers.OffersManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hly.fontxiu.MainActivity;
import com.hly.fontxiu.R;
import com.hly.fontxiu.utils.PointsHelper;

public class EarnPointsFragment extends Fragment implements OnClickListener{
	
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
		
		
		// (可选)使用积分Mini Banner-一个新的积分墙入口点，随时随地让用户关注新的积分广告
		mMiniBanner = new OffersBanner(getActivity(), OffersAdSize.SIZE_MATCH_SCREENx32);//
		RelativeLayout layoutOffersMiniBanner = (RelativeLayout) v.findViewById(R.id.OffersMiniBannerLayout);
		layoutOffersMiniBanner.addView(mMiniBanner);

		// (可选)使用积分Banner-一个新的积分墙入口点，随时随地让用户关注新的积分广告
		mBanner = new OffersBanner(getActivity(), OffersAdSize.SIZE_MATCH_SCREENx60);
		RelativeLayout layoutOffersBanner = (RelativeLayout) v.findViewById(R.id.offersBannerLayout);
		layoutOffersBanner.addView(mBanner);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.hly.fontxiu.showpoints");
		getActivity().registerReceiver(mReceiver, filter);
		return v;
	}
	
	private void fillText() {
		mTextViewPoints.setText("当前积分："+ PointsHelper.getCurrentPoints(getActivity()));
		StringBuilder sb =  new StringBuilder();
		sb.append("\n\n积分规则：").append("\n\n")
		.append("1. 用户初次登陆应用奖励").append(MainActivity.sAwardPoints).append("积分，使用字体需要有足够的积分才可以使用。").append("\n\n")
		.append("2. 获取积分途径:").append("\n\n")
		.append("   2.1) 通过点击页面显示的广告赚取.").append("\n")
		.append("   2.2) 通过点击获取更多积分按钮.").append("\n\n")
		.append("3. 点击进入下载页面，按照要求下载使用即可获取对应的积分。").append("\n\n")
		.append("4.当有足够的积分时，每应用一种新字体都会消耗对应的积分，旧字体可任意切换。").append("\n\n")
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
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
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
		}
		
	}
	
}
