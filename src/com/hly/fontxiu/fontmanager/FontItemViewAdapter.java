package com.hly.fontxiu.fontmanager;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.hly.fontxiu.R;

public class FontItemViewAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<FontResource> mFontResList = null;
    private LayoutInflater mLayoutInflater = null;

    public FontItemViewAdapter(Context context, LayoutInflater layoutInflater, List<FontResource> fontResList) {
        mContext = context;
        mFontResList = fontResList;
        mLayoutInflater = layoutInflater;
    }

    public int getCount() {
        return mFontResList.size();
    }

    public Object getItem(int position) {
        return mFontResList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View fontListItemView = convertView;
        if (fontListItemView == null) {
            fontListItemView = mLayoutInflater
                    .inflate(R.layout.font_item, null);
        }
        TextView fontNameView = (TextView) fontListItemView
                .findViewById(R.id.font_name_view);

        FontResource fontRes = mFontResList.get(position);
        Typeface typeface = fontRes.getTypeface();
        if (typeface != null) {
            fontNameView.setTypeface(typeface);
        }
        fontNameView.setText(fontRes.getDisplayName());

        CheckBox fontItemCheck = (CheckBox) fontListItemView.findViewById(R.id.font_item_check);
        fontItemCheck.setChecked(fontRes.isSelected());
        return fontListItemView;
    }

    public void setFontData(List<FontResource> fontResList) {
        mFontResList = fontResList;
    }

    public List<FontResource> getFontData() {
        return mFontResList;
    }
}
