package com.sg.mtfont.points;

import org.apache.http.Header;
import org.json.JSONArray;

import net.youmi.android.offers.OffersManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sg.mtfont.MainActivity;
import com.sg.mtfont.R;
import com.sg.mtfont.utils.CommonUtils;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.FontRestClient;
import com.sg.mtfont.utils.PointsHelper;

public class EarnPointsFragment extends Fragment implements OnClickListener{
	
	public static final String TAG = "EarnPointsFragment";
	
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
		return v;
	}
	
	public void buyFree(View v){
		if (PointsHelper.getCurrentPoints(getActivity()) < 1000){
			Toast.makeText(getActivity(), R.string.no_much_points, Toast.LENGTH_SHORT).show();
		} else {
			PointsHelper.spendPoints(getActivity(), 1000);//消耗1000
			FontRestClient.post(Constant.buySoft + CommonUtils.getImei(getActivity()), null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
					Toast.makeText(getActivity(), R.string.buy_success_tips, Toast.LENGTH_SHORT).show();
					super.onSuccess(statusCode, headers, response);
				}
			});
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					CommonUtils.getDeviceInfo("for free version:",getActivity());//TODO
				}
			}).start();
			
			Intent i = getActivity().getPackageManager()  
			        .getLaunchIntentForPackage(getActivity().getPackageName());  
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			startActivity(i);
			getActivity().finish();
			
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
		.append("\n1.美图手机2字体福利到：只要您的积分大于等于1000分就可以购买永久免费版了！\n2.购买完成之后，退出程序，长按Home键，即中间的那个键，到多任务删除美图手机2字体应用，然后重新打开就可以享受免费无广告版的美图手机2换字体了")
		.append("\n3.如果卸载了应用请过一段时间重新下载新版本，则仍可以享受免费版~");
		mTextViewPoints.setText(sbP.toString());
		StringBuilder sb =  new StringBuilder();
		sb.append("\n\n积分规则：").append("\n\n")
		.append("1. 用户初次登陆应用奖励").append(MainActivity.AWARD_POINTS).append("积分，使用字体需要有足够的积分才可以使用。").append("\n\n")
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
