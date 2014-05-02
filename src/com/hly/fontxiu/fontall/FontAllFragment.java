
package com.hly.fontxiu.fontall;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hly.fontxiu.R;

public class FontAllFragment extends ListFragment {

    private BaseAdapter mMyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_font_all, container,
                false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyAdapter = new MyListAdapter(30);
        setListAdapter(mMyAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), "p:" + position, 1).show();
    }

    class MyListAdapter extends BaseAdapter {
        int mFontCount;

        public MyListAdapter(int fontCount) {
            mFontCount = fontCount;
        }

        @Override
        public int getCount() {
            return mFontCount;
        }

        @Override
        public Object getItem(int arg0) {
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_font_all, parent, false);
            }
            return view;
        }
    }
}
