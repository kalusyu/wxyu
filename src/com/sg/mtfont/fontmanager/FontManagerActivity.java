package com.sg.mtfont.fontmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.sg.mtfont.R;

public class FontManagerActivity extends Fragment  implements OnItemClickListener, IFontResDataReceiver{
	private static final String TAG = "FontManagerActivity";

	private ListView mFontsListView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragView = inflater.inflate(R.layout.main, container,false);
		Button button = (Button)fragView.findViewById(R.id.btn_cancel);
        button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
//                finish();//TODO
            }
        });
        
        button = (Button)fragView.findViewById(R.id.btn_ok);
        button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                FontItemViewAdapter adapter = (FontItemViewAdapter) mFontsListView.getAdapter();
                List<FontResource> fontResList = adapter.getFontData();
                FontResource fontRes = FontResUtil.getSelectedFontRes(fontResList);

                FontResUtil.updateSysteFontConfiguration(fontRes);
                FontResUtil.saveSystemFontRes(getActivity().getBaseContext(), fontRes);
//                finish();//TODO
            }
        });
        
        mFontsListView = (ListView)fragView.findViewById(R.id.font_list_view);
        mFontsListView.setOnItemClickListener(this);
        
        List<FontResource> fontResList = new ArrayList<FontResource>();
        FontItemViewAdapter adapter = new FontItemViewAdapter(getActivity().getBaseContext(), getActivity().getLayoutInflater(), fontResList);
        mFontsListView.setAdapter(adapter);

        PackageManager pm = getActivity().getPackageManager();
        List<PackageInfo> packegeInfoList = FontResUtil.getFontPackegeInfoList(pm);

        FontResLoadTask fontResLoadTask = new FontResLoadTask(pm, packegeInfoList, this);
        fontResLoadTask.execute();
		return fragView;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        
//        Button button = (Button)findViewById(R.id.btn_cancel);
//        button.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        
//        button = (Button)findViewById(R.id.btn_ok);
//        button.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View v) {
//                FontItemViewAdapter adapter = (FontItemViewAdapter) mFontsListView.getAdapter();
//                List<FontResource> fontResList = adapter.getFontData();
//                FontResource fontRes = FontResUtil.getSelectedFontRes(fontResList);
//
//                FontResUtil.updateSysteFontConfiguration(fontRes);
//                FontResUtil.saveSystemFontRes(getBaseContext(), fontRes);
//                finish();
//            }
//        });
//        
//        mFontsListView = (ListView)findViewById(R.id.font_list_view);
//        mFontsListView.setOnItemClickListener(this);
//        
//        List<FontResource> fontResList = new ArrayList<FontResource>();
//        FontItemViewAdapter adapter = new FontItemViewAdapter(getBaseContext(), getLayoutInflater(), fontResList);
//        mFontsListView.setAdapter(adapter);
//
//        PackageManager pm = getPackageManager();
//        List<PackageInfo> packegeInfoList = FontResUtil.getFontPackegeInfoList(pm);
//
//        FontResLoadTask fontResLoadTask = new FontResLoadTask(pm, packegeInfoList, this);
//        fontResLoadTask.execute();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FontItemViewAdapter adapter = (FontItemViewAdapter) mFontsListView.getAdapter();
        List<FontResource> fontResList = adapter.getFontData();
        fontResList = FontResUtil.selectFontRes(fontResList, position);
        adapter.setFontData(fontResList);
        adapter.notifyDataSetChanged();
    }

    public int receiveFontResData(FontResource fontRes) {
        FontItemViewAdapter adapter = (FontItemViewAdapter) mFontsListView.getAdapter();
        List<FontResource> fontResList = adapter.getFontData();
        fontResList.add(fontRes);
        adapter.setFontData(fontResList);
        adapter.notifyDataSetChanged();
        return 0;
    }

    public int onFontResLoadCompleted() {
        FontItemViewAdapter adapter = (FontItemViewAdapter) mFontsListView.getAdapter();
        List<FontResource> fontResList = adapter.getFontData();
        
        FontResource fontRes = FontResUtil.getSystemFontRes(getActivity().getBaseContext());
        FontResUtil.selectFontRes(fontResList, fontRes);
        
        adapter.setFontData(fontResList);
        adapter.notifyDataSetChanged();
        return 0;
    }
}