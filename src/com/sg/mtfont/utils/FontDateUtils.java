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
    
    /**
     * 获取 给定格式的日期
     * @param format
     * @return String
     */
    public static String getDateString(String format){
        df = new SimpleDateFormat(format);
        return df.format(new Date());
    }
}
