package com.hly.fontxiu.bean;

public class FontFile {
	
	/**
	 * 字体名称 中文全称
	 */
	private String fontDisplayName;

	/**
	 * 字体名称 pinyin全称
	 */
	private String fontName;
	
	/**
	 * 字体名称对应的图片
	 */
	private String fontNamePic;
	
	/**
	 * 字体名称对应的图片uri
	 */
	private String fontNamePicUri;
	
	/**
	 * 预览图片名称
	 */
	private String[] pictureNames;
	
	/**
	 * 字体大小
	 */
	private String fontSize;
	
	/**
	 * 字体uri
	 */
	private String fontUri;
	
	/**
	 * 预览图uri
	 */
	private String pictureUri;
	
	private String fontLocalPath;
	private String pictureLocalPath;
	
	private boolean isApplied;
	
	private boolean isDownloaded;
	
	private String packageName;
	
	public FontFile() {
	}

	

	public FontFile(String fontName, String fontNamePic, String fontNamePicUri,
			String[] pictureNames, String fontSize, String fontUri,
			String pictureUri) {
		super();
		this.fontName = fontName;
		this.fontNamePic = fontNamePic;
		this.fontNamePicUri = fontNamePicUri;
		this.pictureNames = pictureNames;
		this.fontSize = fontSize;
		this.fontUri = fontUri;
		this.pictureUri = pictureUri;
	}



	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String[] getPictureNames() {
		return pictureNames;
	}

	public void setPictureNames(String[] pictureNames) {
		this.pictureNames = pictureNames;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontUri() {
		return fontUri;
	}

	public void setFontUri(String fontUri) {
		this.fontUri = fontUri;
	}

	public String getPictureUri() {
		return pictureUri;
	}

	public void setPictureUri(String pictureUri) {
		this.pictureUri = pictureUri;
	}

	public String getFontNamePic() {
		return fontNamePic;
	}

	public void setFontNamePic(String fontNamePic) {
		this.fontNamePic = fontNamePic;
	}

	public String getFontNamePicUri() {
		return fontNamePicUri;
	}

	public void setFontNamePicUri(String fontNamePicUri) {
		this.fontNamePicUri = fontNamePicUri;
	}



	public String getFontLocalPath() {
		return fontLocalPath;
	}

	public void setFontLocalPath(String fontLocalPath) {
		this.fontLocalPath = fontLocalPath;
	}

	public String getPictureLocalPath() {
		return pictureLocalPath;
	}

	public void setPictureLocalPath(String pictureLocalPath) {
		this.pictureLocalPath = pictureLocalPath;
	}

	public boolean isApplied() {
		return isApplied;
	}

	public void setApplied(boolean isApplied) {
		this.isApplied = isApplied;
	}

	public boolean isDownloaded() {
		return isDownloaded;
	}

	public void setDownloaded(boolean isDownloaded) {
		this.isDownloaded = isDownloaded;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFontDisplayName() {
		return fontDisplayName;
	}

	public void setFontDisplayName(String fontDisplayName) {
		this.fontDisplayName = fontDisplayName;
	}
	
	

	
	
	
	
	
}
