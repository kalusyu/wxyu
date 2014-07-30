package com.sg.mtfont.fontquality;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sg.mtfont.R;
import com.sg.mtfont.bean.FontFile;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.view.PullToRefreshView;
import com.sg.mtfont.view.PullToRefreshView.OnFooterRefreshListener;
import com.sg.mtfont.view.PullToRefreshView.OnHeaderRefreshListener;

public class FontBoutiqueFragment extends Fragment implements OnClickListener,OnHeaderRefreshListener, OnFooterRefreshListener {

	public static final String FONT_DETAIL_RESOURCE = "fontDetailResource";
	public static final String FONT_FILE_PATCH_RESOURCE = "fontFilePatchResource";

	public static final String FONT_FILENAME = "fontFileName";

	private List<View> fontsImageView = new ArrayList<View>();

	private WeakReference<ProgressDialog> mProgress;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// final ProgressDialog lProgress = mProgress.get();
				// if (lProgress != null && lProgress.isShowing()) {
				// lProgress.dismiss();
				// }
				// Toast.makeText(getActivity(), "恢复成功",
				// Toast.LENGTH_SHORT).show();
			}
		};
	};


	
	PullToRefreshView mPullToRefreshView;
	GridView mGridView;
	private LayoutInflater mInflater;
	private List<Integer> listDrawable = new ArrayList<Integer>();
	private GridViewAdapter adapter;
	private List<FontFile> mFontFiles;
	
	private OnClickListener mGridItemOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch(view.getId()){
			case R.id.img_thumbnail:
			case R.id.txt_font_name:
				Intent it = new Intent(getActivity(),FontDetailActivity.class);
				//TODO
				startActivity(it);
				break;
			case R.id.txt_love_font_numbers:
				//TODO 数据变化，刷新数据，与服务器交互
				break;
			case R.id.txt_download_font_numbers:
				//TODO
				break;
				default:
					;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_font_boutique, null,
				false);
		mInflater = inflater;
		listDrawable.add(R.drawable.test_image);
		listDrawable.add(R.drawable.test_image);
		
		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.main_pull_refresh_view);
		mGridView = (GridView) view.findViewById(R.id.gridview);
		adapter = new GridViewAdapter();
		mGridView.setAdapter(adapter);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		Button btn = (Button) view.findViewById(R.id.btn_recover_system_font);
		btn.setOnClickListener(mRecoverSystemFontClickListener);
		return view;
	}

	OnClickListener mRecoverSystemFontClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			// mProgress = new WeakReference<ProgressDialog>(
			// ProgressDialog
			// .show(getActivity(),
			// null,
			// getResources().getString(
			// R.string.font_applying), true,
			// false));
			// TODO CommonUtils.isRooted();
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
				"com.monotype.android.font.xiaonaipaozhongwen",
				"com.monotype.android.font.cuojuehuiyi",
				"com.monotype.android.font.wuyunkuaizoukai",
				"com.monotype.android.font.jiangnandiao",
				"com.monotype.android.font.zhihualuo"

		};
		String[] fileName = new String[] { "wuyunkuaizoukai.apk",
				"xiaonaipaozhongwen.apk", "zhihualuo.apk", "cuojuehuiyi.apk",
				"jiangnandiao.apk" };

		intent.putExtra(FONT_DETAIL_RESOURCE, resoureceId);
		intent.putExtra(FONT_FILE_PATCH_RESOURCE, fontFilePath);
		intent.putExtra(FONT_FILENAME, fontFileName);
		startActivity(intent);
	}

//	viewHolder.fontName = (TextView) view
//			.findViewById(R.id.txt_font_name);
//	viewHolder.fontLoveNumbers = (TextView) view
//			.findViewById(R.id.txt_love_font_numbers);
//	viewHolder.fontDownloadNumbers = (TextView) view
//			.findViewById(R.id.txt_download_font_numbers);
//	viewHolder.imageThumbnail = (ImageView) view
//			.findViewById(R.id.img_thumbnail);

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				System.out.println("上拉加载");
				listDrawable.add(R.drawable.test_image);
				adapter.notifyDataSetChanged();
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 1000);		
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 设置更新时间
				// mPullToRefreshView.onHeaderRefreshComplete("最近更新:01-23 12:01");
				System.out.println("下拉更新");
				listDrawable.add(R.drawable.test_image);
				adapter.notifyDataSetChanged();
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);
		
	}
	
	private class GridViewAdapter extends BaseAdapter{
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listDrawable.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listDrawable.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder = null;
			if (view == null) {
                view = mInflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.mThumnailImage = (ImageView)view
                        .findViewById(R.id.img_thumbnail);
                holder.mFontNameCh = (TextView) view.findViewById(R.id.txt_font_name);
                holder.mLoveNumbers = (TextView) view.findViewById(R.id.txt_love_font_numbers);
                holder.mDownloadNumbers = (TextView) view.findViewById(R.id.txt_download_font_numbers);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }
			// TODO function
			// set data
			FontFile fontFile = mFontFiles.get(position);
//			holder.mThumnailImage.setImageDrawable();
			holder.mFontNameCh.setText(fontFile.getFontDisplayName());
			holder.mLoveNumbers.setText(fontFile.getLoveNumbers());
			holder.mDownloadNumbers.setText(fontFile.getDownloadNumbers());
			
			// set click listener
			holder.mThumnailImage.setOnClickListener(mGridItemOnclickListener);
			holder.mFontNameCh.setOnClickListener(mGridItemOnclickListener);
			holder.mLoveNumbers.setOnClickListener(mGridItemOnclickListener);
			holder.mDownloadNumbers.setOnClickListener(mGridItemOnclickListener);
			
			return view;
		}
		
	}
	
	class ViewHolder{
		ImageView mThumnailImage;
		TextView mFontNameCh;
		TextView mLoveNumbers;
		TextView mDownloadNumbers;
	}
}
