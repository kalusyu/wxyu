package com.sg.mtfont.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/***
 * 
 * @author Kalus Yu
 *
 */
public class FontDateUtils {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static DateFormat df;
    
    public static String getDateString(){
        return getDateString(DEFAULT_FORMAT);
    }
    
    public static String getDateString(String format){
        df = new SimpleDateFormat(format);
        return df.format(new Date());
    }
}
