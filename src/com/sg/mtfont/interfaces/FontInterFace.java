package com.sg.mtfont.interfaces;

import java.util.List;

import com.sg.mtfont.bean.DeviceInfo;
import com.sg.mtfont.bean.FontFile;

/**
 * 
 * @author Kalus Yu
 *
 */
public interface FontInterFace {

    /**
     * 存储手机基本信息
     * @author Kalus Yu
     * @param info
     * 2014年9月3日 下午6:51:24
     */
    void saveDeviceInfo(DeviceInfo info);
    
    /**
     * 获取字体信息
     * @author Kalus Yu
     * @return
     * 2014年9月3日 下午6:59:34
     */
    List<FontFile> queryFontResouces(int begin);
    
    /**
     * 
     * @author Kalus Yu
     * @return
     * 2014年9月3日 下午6:59:17
     */
    boolean buySoftForFree();
}
