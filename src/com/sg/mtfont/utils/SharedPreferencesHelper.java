
package com.sg.mtfont.utils;

import android.content.Context;
import android.content.SharedPreferences;

public final class SharedPreferencesHelper {

    public static final String SHARED_PREFERENCES_NAME = "FontSharedPreferences";

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
