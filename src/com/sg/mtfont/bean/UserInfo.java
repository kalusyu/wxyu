package com.sg.mtfont.bean;

import java.io.Serializable;

/**
 * 
 * @author Kalus Yu
 *
 */
public class UserInfo implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -6747779551491170831L;
    String imei;
    String firstInstallTime;
    String lastLoginTime;
    String uninstallTime;
    String installNumbers;
    String uninstallNumbers;
    String launchNumbers;
}
