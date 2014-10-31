package com.sg.mtfont.fontquality;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
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
	
	public static final String EXTRA_SELECTED_URL = "selected_url";
	public static final String EXTRA_PICTURE_URLS = "all_urls";
	public static final String EXTRA_FONT_URLS = "font_apk_urls";
	
	BoutiqueFragmentListener mListener;
	
	PullToRefreshView mPullToRefreshView;
	GridView mGridView;
	private LayoutInflater mInflater;
	private List<Integer> listDrawable = new ArrayList<Integer>();
	private GridViewAdapter adapter;
//	private ArrayList<String> mPictureUris = new ArrayList<String>();
//	private ArrayList<String> mFontApkUris = new ArrayList<String>();
	
	private OnClickListener mGridItemOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch(view.getId()){
			//TODO goto detail page
			case R.id.img_thumbnail:
			case R.id.txt_font_name:
				Intent it = new Intent(getActivity(),FontDetailActivity.class);
				it.putExtra(EXTRA_SELECTED_URL, (String)view.getTag());
//				it.putExtra(EXTRA_PICTURE_URLS, mPictureUris);
//				it.putExtra(EXTRA_FONT_URLS, mFontApkUris);
				startActivity(it);
				break;
			//TODO 数据变化，刷新数据，与服务器交互
			case R.id.txt_love_font_numbers:
				break;
			//TODO
			case R.id.txt_download_font_numbers:
				break;
				default:
					;
			}
		}
	};

	
	private DisplayImageOptions options;

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.test_image)
        .showImageForEmptyUri(R.drawable.feed_back)
        .showImageOnFail(R.drawable.ic_launcher)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_font_boutique, null,
				false);
		mInflater = inflater;
		
		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.main_pull_refresh_view);
		mGridView = (GridView) view.findViewById(R.id.gridview);
		JSONArray json = mListener.getFontJson();
		
		adapter = new GridViewAdapter(getActivity(),json);
		mGridView.setAdapter(adapter);
		
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		Button btn = (Button) view.findViewById(R.id.btn_recover_system_font);
		btn.setOnClickListener(mRecoverSystemFontClickListener);
		
		
		return view;
	}

	/**
	 * launch thread to init uris
	 * @author Kalus Yu
	 * @param mFontFiles2
	 * 2014年10月7日 下午4:25:54
	 */
	private void initUris(final List<FontFile> mFontFiles) {
	    new Thread(){
	        public void run() {
	            for (FontFile f : mFontFiles){
//	                mPictureUris.add(f.getFontNamePicUri() + f.getFontNamePic());
//	                mFontApkUris.add(f.getFontUri() + f.getFontDisplayName());
	            }
	        };
	    }.start();
    }

    OnClickListener mRecoverSystemFontClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BoutiqueFragmentListener) activity;
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

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
	
    public interface BoutiqueFragmentListener{
        JSONArray getFontJson();
    }
	
	class GridViewAdapter extends BaseAdapter{
		
	    JSONArray mJson;
	    Context mContext;
	    int mCount;

	    /**
	     * 
	     * @param context
	     * @param json
	     */
		public GridViewAdapter(Context context,JSONArray json) {
		    mContext = context;
		    mJson = json;
		    mCount = json.length();
        }

        @Override
		public int getCount() {
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			try {
                return mJson.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
			return null;
		}

		@Override
		public long getItemId(int position) {
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
			try {
			JSONObject jo = mJson.getJSONObject(position);
			if (jo.getString("type").contains("image")){
			    String url = jo.getString("downloadUrl");
			    holder.mThumnailImage.setTag(url);
			    loadThumbnailImage(holder,url);//uri should be http:// format
			}
			//TODO
//			holder.mFontNameCh.setText(fontFile.getFontDisplayName());
//			holder.mLoveNumbers.setText(fontFile.getLoveNumbers());
//			holder.mDownloadNumbers.setText(fontFile.getDownloadNumbers());
			} catch (JSONException e){
			    e.printStackTrace();
			}
			// set click listener
			holder.mThumnailImage.setOnClickListener(mGridItemOnclickListener);
			holder.mFontNameCh.setOnClickListener(mGridItemOnclickListener);
			holder.mLoveNumbers.setOnClickListener(mGridItemOnclickListener);
			holder.mDownloadNumbers.setOnClickListener(mGridItemOnclickListener);
			
			return view;
		}
		
		public void loadThumbnailImage(ViewHolder holder,String url){
		    ImageLoader.getInstance()
            .displayImage(url, holder.mThumnailImage, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
//                    holder.progressBar.setProgress(0);
//                    holder.progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    //holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //holder.progressBar.setVisibility(View.GONE);
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    //holder.progressBar.setProgress(Math.round(100.0f * current / total));
                }
            });
		}
		
	}
	
	class ViewHolder{
		ImageView mThumnailImage;
		TextView mFontNameCh;
		TextView mLoveNumbers;
		TextView mDownloadNumbers;
	}
}
