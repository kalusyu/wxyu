package com.sg.mtfont.utils;

public class Constant {

	// private ImageView ivFontDetal;
	public static final int NEED_POINTS = 200;
	
	public static final String HOMEIP = "http://169.254.2.164";
	public static final String COMPANYIP = "http://192.168.43.189";
	public static final String DOMAIN_NAME = "http://kalusyu.oicp.net";
	public static final String WANGXI_JIA = "http://192.168.1.102";
	public static String sUrl = COMPANYIP;
	                             
	public static String methodSaveDeviceInfoPath = "admin/save";
	
	public static String methodgetAllDownload = "admin/getAllDownload";
	
	public static String methodgetisFreeUser = "admin/isFreeUser";
	
	public static String getFontInfo = "/mobile/getFontInfo/";
	
	public static String getPageInfo = "/mobile/getPageInfo/";
	
	public static String updateLoveNumber = "/mobile/updateLoveNumber/";
	
	public static String updateDownloadNumber = "/mobile/updateDownloadNumber/";
	
	public static String downloadFontFile = "/mobile/downloadFontFile/";
	
	
	public static final String FONTFILE = "fontfile";
	public static final int PAGESIZE = 6;
	
	interface BaseColumn {
	    //common key
	    public static final String ID = "id";
	}
	
	// NFile database key
	static final class NFile implements BaseColumn {
	    
	    public static final String GROUPID = "groupId";
    	public static final String NAME = "name";
    	public static final String SIZE = "size";
        public static final String TYPE = "type";
        public static final String DOWNLOADURL = "downloadUrl";
        public static final String RELATIVEURL = "relativeUrl";
	}
	
	static final class Count implements BaseColumn {
        public static final String ID = "id";
        public static final String LOVENUM = "loveNum";
        public static final String DOWNLOADNUM = "downloadNum";
        public static final String FILEID = "fileId";
    }
    
    class Group implements BaseColumn {
        public static final String ID = "id";
        public static final String GROUPNAME = "groupName";
    }
    class FreeUser implements BaseColumn {
        public static final String ID = "id";
        public static final String IMEI = "imei";
    }
    
    class DeviceInfo implements BaseColumn {
        
    }
   
} 
