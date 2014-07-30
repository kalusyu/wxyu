package com.sg.mtfont;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * 
 * only for toast
 *
 */
public class FontHandler extends Handler{
	Context mContext;
	public FontHandler(Context ctx) {
		this.mContext = ctx;
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (msg.what == MainActivity.NO_INSTALL_PERMISSION){
			Toast.makeText(mContext, R.string.font_apply_only_in_meitu2, Toast.LENGTH_LONG).show();
		}
	}
}