package com.sg.mtfont.fontquality;


import java.io.File;
import java.util.concurrent.Executors;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sg.mtfont.FontDownloadReceiver;
import com.sg.mtfont.R;
import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.fontmanager.FontResource;
import com.sg.mtfont.task.FontApplyAsyncTask;
import com.sg.mtfont.utils.Constant;
import com.sg.mtfont.utils.FileUtils;
import com.sg.mtfont.utils.FontDateUtils;
import com.sg.mtfont.utils.FontRestClient;
import com.sg.mtfont.utils.PointsHelper;
import com.sg.mtfont.utils.SharedPreferencesHelper;
import com.sg.mtfont.view.PullToRefreshView;
import com.sg.mtfont.view.PullToRefreshView.OnFooterRefreshListener;
import com.sg.mtfont.view.PullToRefreshView.OnHeaderRefreshListener;
/**
 * 
 * @author Kalus Yu
 *
 */
public class FontBoutiqueFragment extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener {

    public static final String TAG = FontBoutiqueFragment.class.getSimpleName();
	public static final String EXTRA_SELECTED_URL = "selected_url";
	public static final String EXTRA_PICTURE_URLS = "all_urls";
	public static final String EXTRA_FONT_URLS = "font_apk_urls";
	
	private BoutiqueFragmentListener mListener;
	
	private PullToRefreshView mPullToRefreshView;
	private GridView mGridView;
	private LayoutInflater mInflater;
	private GridViewAdapter mGridAdapter;
	
	private int mCurrentPage = 1;
	private int mTotalPage;
	
	private FontDownloadReceiver mDownloadReceiver;
	
	private OnClickListener mGridItemOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(final View view) {
			switch(view.getId()){
			//TODO goto detail page
			case R.id.img_thumbnail:
			case R.id.txt_font_name:
				break;
			//TODO 数据变化，刷新数据，与服务器交互
			case R.id.txt_love_font_numbers:
			    JSONObject tag = (JSONObject)view.getTag();
			    try{
    			    int fileId = tag.getInt("id");//获取文件id，根据文件ID增加相应的喜欢数
    			    FontRestClient.post(Constant.updateLoveNumber + "2-"+fileId, null, new JsonHttpResponseHandler(){
    			        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        			            try {
                                    ((TextView)view).setText(response.getLong("loveNum")+"");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
        			            view.postInvalidate();
    			        }
    			    });
			    } catch (JSONException e){
			        Log.e(TAG, "mGridItemOnclickListener txt_love_font_numbers JSONException e.getMessage()="+e.getMessage());
			    }
				break;
			//TODO
			case R.id.txt_download_font_numbers:
			    tag = (JSONObject)view.getTag();
			    try{
                    int fileId = tag.getInt("id");
                    FontRestClient.post(Constant.updateDownloadNumber + "2-"+fileId, null, new JsonHttpResponseHandler(){
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    ((TextView)view).setText(response.getLong("downloadNum")+"");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                view.postInvalidate();
                        }
                    });
			    } catch (JSONException e){
			        Log.e(TAG, "mGridItemOnclickListener txt_love_font_numbers JSONException e.getMessage()="+e.getMessage());
			    }
                try {
                    downloadFile(mApkSparse.get(tag.getInt("groupId")));//根据之前的groupId获取apk文件json
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        .showImageOnLoading(R.drawable.default_image)
        .showImageForEmptyUri(R.drawable.feed_back)
        .showImageOnFail(R.drawable.ic_launcher)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();
		
		mDownloadReceiver = new FontDownloadReceiver();
		mDownloadReceiver.setup(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		getActivity().registerReceiver(mDownloadReceiver, filter);
		
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		getActivity().registerReceiver(mDownloadReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mDownloadReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_font_boutique, null,
				false);
		mInflater = inflater;
		
		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.main_pull_refresh_view);
		clearSparseArray();
		mGridView = (GridView) view.findViewById(R.id.gridview);
		JSONArray json = mListener.getFontJson();
		handleJson(json);
		mGridAdapter = new GridViewAdapter(getActivity(),mImageSparse);
		mGridView.setAdapter(mGridAdapter);
		
//		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		Button btn = (Button) view.findViewById(R.id.btn_recover_system_font);
		btn.setOnClickListener(mRecoverSystemFontClickListener);
		
		
		return view;
	}
	
	private void clearSparseArray() {
	    mImageSparse.clear();
	    mApkSparse.clear();
    }
	
	private void downloadFile(JSONObject json) throws JSONException{
	    String fileName = json.getString("name");
	    String file = FileUtils.getSDCardPath()
                + File.separatorChar + "download"
                + File.separatorChar + fileName;
        File fontApk = new File(file);
        if(!fontApk.exists()){
            DownloadManager dm = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(Constant.sUrl + json.getString("relativeUrl").replace("\\", "/"));
            Request request = new Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationInExternalFilesDir(getActivity(), null, file);//TODO 考虑sd卡问题，可以考虑先下载安装完再删除存到应用files下面
            request.setDestinationInExternalPublicDir("fontxiuxiu/download", fileName);
            long id = dm.enqueue(request);
            SharedPreferences sp = SharedPreferencesHelper.getSharepreferences(getActivity());
            sp.edit().putBoolean(String.valueOf(id), true).commit();
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(View.VISIBLE);
            request.setTitle("字体下载");
            request.setDescription(fileName+"下载中");
            
            Toast.makeText(getActivity(), "后台正在下载...", Toast.LENGTH_SHORT).show();
            return;
        }
        
	}

    public void onStart() {
	    super.onStart();
	    FontRestClient.post(Constant.getPageInfo, null, new JsonHttpResponseHandler(){
	        @Override
	        public void onSuccess(int statusCode, Header[] headers,
	                JSONArray response) {
	            //{"pageSize":6,"pageNumber":1,"list":[{"id":7},{"id":8},{"id":9},{"id":10},{"id":11},{"id":12}],"totalRow":24,"totalPage":4}
	            try {
                    mTotalPage = response.getJSONObject(0).getInt("totalPage");
                } catch (JSONException e) {
                    Log.e(TAG, "onStart JSONException e.getMessage()="+e.getMessage());
                }
	        }
	    });
	};

	SparseArray<JSONObject> mImageSparse = new SparseArray<JSONObject>();
	SparseArray<JSONObject> mApkSparse = new SparseArray<JSONObject>();
	private void handleJson(JSONArray json) {
	    try {
	        int k = mImageSparse.size();
    	    for (int i = 0; i < json.length(); i++){
    	        JSONObject o = json.getJSONObject(i);
    	        int groupId = o.getInt("groupId");
    	        String type = o.getString("type");
    	        if (type.contains("image")){
    	            mImageSparse.put(k++, o);
    	        } else {
    	            mApkSparse.put(groupId, o);
    	        }
    	    }
	    } catch (JSONException e){
	        e.printStackTrace();
	    }
    }
	
	public void checkPointsOrApplyFont(String path){
		PackageManager pm = getActivity().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path,
                PackageManager.GET_ACTIVITIES);
        String packageName = info.applicationInfo.packageName;
        int currentPoints = PointsHelper.getCurrentPoints(getActivity());
        //TODO 
        if (/*!MainActivity.mConfig.isFree() &&*/ currentPoints < Constant.NEED_POINTS && !SharedPreferencesHelper.isFontApplied(getActivity(), packageName)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage(
                            "应用此字体需要200积分\n您当前的积分为" + currentPoints + "，是否获取积分")
                    .setPositiveButton("获取积分",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                        int arg1) {
                                    //TODO
                                    /*ViewPager viewPager = MainActivity
                                            .getViewPager();
                                    if (viewPager != null) {
                                        viewPager.setCurrentItem(2);
                                    }*/
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                        int arg1) {

                                }
                            }).show();
        } else {
            FontApplyAsyncTask applyFontTask = new FontApplyAsyncTask(getActivity());
            applyFontTask.executeOnExecutor(Executors.newSingleThreadExecutor(),path);
        }
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
	public void onFooterRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 设置更新时间
                mPullToRefreshView.onHeaderRefreshComplete("最近更新:"+ FontDateUtils.getDateString());
                
                if (mCurrentPage <= mTotalPage){
                    int start = mCurrentPage * Constant.PAGESIZE;
                    // TODO total num page
                    mCurrentPage += 1;
                    FontRestClient.post(Constant.getFontInfo+ start+"-"+Constant.PAGESIZE, null, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                JSONArray response) {
                            handleJson(response);
                            mGridAdapter.notifyDataSetChanged();
                            mPullToRefreshView.onFooterRefreshComplete();
                        }
                        
                        @Override
                        public void onFailure(int statusCode, Header[] headers,
                                String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            mCurrentPage -= 1;
                        }
                    });
                } else {
                    mPullToRefreshView.onFooterRefreshComplete();
                }
            }
        }, 1000);	
        
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
//		mPullToRefreshView.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				// 设置更新时间
//				mPullToRefreshView.onHeaderRefreshComplete("最近更新:"+ FontDateUtils.getDateString());
//				
//				int start = mCurrentPage * 6 + 1;
//				int end = (mCurrentPage + 1) * 6 + 1;
//				mCurrentPage += 1;
//				FontRestClient.post(Constant.getFontInfo+ start+"-"+end, null, new JsonHttpResponseHandler(){
//				    @Override
//				    public void onSuccess(int statusCode, Header[] headers,
//				            JSONArray response) {
//				        handleJson(response);
//				        mGridAdapter.notifyDataSetChanged();
//				        mPullToRefreshView.onHeaderRefreshComplete();
//				    }
//				    
//				    @Override
//				    public void onFailure(int statusCode, Header[] headers,
//				            String responseString, Throwable throwable) {
//				        super.onFailure(statusCode, headers, responseString, throwable);
//				        mCurrentPage -= 1;
//				    }
//				});
//			}
//		}, 1000);
		
	}
	
    public interface BoutiqueFragmentListener{
        JSONArray getFontJson();
    }
	
	class GridViewAdapter extends BaseAdapter{
		
	    Context mContext;

	    /**
	     * 
	     * @param context
	     * @param json
	     */
		public GridViewAdapter(Context context,SparseArray<JSONObject> image) {
		    mContext = context;
        }

        @Override
		public int getCount() {
			return mImageSparse.size();
		}

		@Override
		public Object getItem(int position) {
             return mImageSparse.get(position);
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
    			JSONObject jo = mImageSparse.get(position);
    			if (jo.getString("type").contains("image")){ // TODO more
    			    String url = jo.getString("relativeUrl");
    			    url = url.replace("\\", "/");
    			    holder.mThumnailImage.setTag(Constant.sUrl + url);
    			    loadThumbnailImage(holder,Constant.sUrl +url);//uri should be http:// format
    			}
    			//TODO
    //			holder.mFontNameCh.setText(fontFile.getFontDisplayName());
    			holder.mLoveNumbers.setText(jo.getLong("loveNum")+"");
    			holder.mDownloadNumbers.setText(jo.getLong("downloadNum")+"");
    			
    			holder.mLoveNumbers.setTag(jo);
    			holder.mDownloadNumbers.setTag(jo);
    			
    			// set click listener
    			holder.mThumnailImage.setOnClickListener(mGridItemOnclickListener);
    			holder.mFontNameCh.setOnClickListener(mGridItemOnclickListener);
    			holder.mLoveNumbers.setOnClickListener(mGridItemOnclickListener);
    			holder.mDownloadNumbers.setOnClickListener(mGridItemOnclickListener);
    			
			} catch (JSONException e){
                e.printStackTrace();
            }
            
			
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
