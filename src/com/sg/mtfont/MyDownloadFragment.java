package com.sg.mtfont;

import java.util.List;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sg.mtfont.fontmanager.FontResUtil;
import com.sg.mtfont.task.FontLoadTask;
import com.sg.mtfont.utils.ApkInstallHelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyDownloadFragment extends Fragment {
	
	private SparseArray<DisplayData> mServerPackages;
	
	private SparseArray<DisplayData>  mSparse;
	
	public MyDownloadFragment() {
	}
	
	
	public MyDownloadFragment(JSONArray jsonArray) {
		if (jsonArray == null) {
			return;
		} 
		mServerPackages = new SparseArray<DisplayData>();
		try {
			int k = 0;
			for (int i = 0; i < jsonArray.length(); i++ ){
				JSONObject jo = jsonArray.getJSONObject(i);
				String type = jo.getString("type");
				if (!type.contains("image")){
					DisplayData data = new DisplayData();
					data.fontName = jo.getString("name");
					data.packageName = jo.getString("packageName");
					mServerPackages.put(k++, data);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final List<PackageInfo> infos = FontResUtil.getFontPackegeInfoList(getActivity().getPackageManager());
		ListView v = (ListView)inflater.inflate(R.layout.listview, null);
		mSparse = new SparseArray<DisplayData>();
		final MyDownloadAdapter adapter = new MyDownloadAdapter(inflater,mSparse);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int k = 0;
				for (PackageInfo pinfo : infos){
					for (int i = 0; i < mServerPackages.size(); i ++){
						DisplayData data = mServerPackages.get(i);
						if (pinfo.packageName.equals(data.packageName)){
							mSparse.put(k++, data);
						}
					}
				}
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
				
			}
		}).start();
		v.setAdapter(adapter);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}

class DisplayData{
	String fontName;
	String packageName;
}

class MyDownloadAdapter extends BaseAdapter{
	
	SparseArray<DisplayData> mSparse;
	LayoutInflater mInflater;

	public MyDownloadAdapter(LayoutInflater inflater, SparseArray<DisplayData> sparse) {
		mSparse = sparse;
		mInflater = inflater;
	}

	@Override
	public int getCount() {
		return mSparse.size();
	}

	@Override
	public Object getItem(int position) {
		return mSparse.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.download_item, null);
		TextView tv = (TextView)v.findViewById(R.id.font_name);
		Button btn = (Button)v.findViewById(R.id.download_apply_btn);
		final DisplayData data = mSparse.get(position);
		tv.setText(data.fontName);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				String packageName = data.packageName;
				if (ApkInstallHelper.checkProgramInstalled(context, packageName)) {
	                // 应用安装过之后直接应用
	                PackageManager pm = context.getPackageManager();
	                List<PackageInfo> packegeInfoList = FontResUtil
	                        .getFontPackegeInfoList(pm);
	                if (packageName != null && packageName.contains("android.font")) {
	                    Log.d("","mydownload fragment font apk had installed ,applying it to system packageName=" + packageName);
	                    FontLoadTask task = new FontLoadTask(
	                            context.getPackageManager(), context,
	                            packegeInfoList);
	                    // task.execute(packageName);
	                    task.executeOnExecutor(Executors.newSingleThreadExecutor(),
	                            packageName);
	                }
	            }
			}
		});
		return v;
	}
	
}
