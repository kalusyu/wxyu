
package com.sg.mtfont.utils;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

public final class SharedPreferencesHelper {

    public static final String SHARED_PREFERENCES_NAME = "FontSharedPreferences";
    
    public static final String DOWNLOAD_ID = "download_id";
    
    private static SharedPreferences mSp;
    
    
    public static String generateDownloadId(){
    	UUID uuid = UUID.randomUUID();
    	return DOWNLOAD_ID+uuid.toString();
    }
    
    
    public static SharedPreferences getSharepreferences(Context ctx){
    	if (mSp == null){
    		mSp = ctx.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    	}
    	return mSp;
    }

    public static void addToApplied(Context context, String fontName) {
        if(null == context){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(fontName, true);
        ed.commit();
    }

    public static boolean isFontApplied(Context context, String fontName) {
        if(null == context){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(fontName, false);
    }
}
