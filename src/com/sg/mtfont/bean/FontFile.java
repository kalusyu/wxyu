package com.sg.mtfont.bean;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

import com.sg.mtfont.utils.FileUtils;


public class FontFile implements Serializable{
	
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
	
	private String fontThumnailPic;
	
	private String fontThumnailPicUri;
	
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
	
	private String loveNumbers;
	
	private String downloadNumbers;
	
	public FontFile() {
	}
	
	public FontFile(String fontDisplayName, String fontName,
			String fontNamePic, String fontNamePicUri, String fontThumnailPic,
			String fontThumnailPicUri, String[] pictureNames, String fontSize,
			String fontUri, String pictureUri, String fontLocalPath,
			String pictureLocalPath, boolean isApplied, boolean isDownloaded,
			String packageName) {
		super();
		this.fontDisplayName = fontDisplayName;
		this.fontName = fontName;
		this.fontNamePic = fontNamePic;
		this.fontNamePicUri = fontNamePicUri;
		this.fontThumnailPic = fontThumnailPic;
		this.fontThumnailPicUri = fontThumnailPicUri;
		this.pictureNames = pictureNames;
		this.fontSize = fontSize;
		this.fontUri = fontUri;
		this.pictureUri = pictureUri;
		this.fontLocalPath = fontLocalPath;
		this.pictureLocalPath = pictureLocalPath;
		this.isApplied = isApplied;
		this.isDownloaded = isDownloaded;
		this.packageName = packageName;
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
		String filePath = FileUtils.getSDCardPath() + File.separatorChar
				+ "download" + File.separatorChar + fontName + ".apk";
		File file = new File(filePath);
		if (file.exists()) {
			return true;
		}
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

	public String getFontThumnailPic() {
		return fontThumnailPic;
	}

	public void setFontThumnailPic(String fontThumnailPic) {
		this.fontThumnailPic = fontThumnailPic;
	}

	public String getFontThumnailPicUri() {
		return fontThumnailPicUri;
	}

	public void setFontThumnailPicUri(String fontThumnailPicUri) {
		this.fontThumnailPicUri = fontThumnailPicUri;
	}

	public String getLoveNumbers() {
		return loveNumbers;
	}

	public void setLoveNumbers(String loveNumbers) {
		this.loveNumbers = loveNumbers;
	}

	public String getDownloadNumbers() {
		return downloadNumbers;
	}

	public void setDownloadNumbers(String downloadNumbers) {
		this.downloadNumbers = downloadNumbers;
	}

    @Override
    public String toString() {
        return "FontFile [fontDisplayName=" + fontDisplayName + ", fontName="
                + fontName + ", fontNamePic=" + fontNamePic
                + ", fontNamePicUri=" + fontNamePicUri + ", fontThumnailPic="
                + fontThumnailPic + ", fontThumnailPicUri="
                + fontThumnailPicUri + ", pictureNames="
                + Arrays.toString(pictureNames) + ", fontSize=" + fontSize
                + ", fontUri=" + fontUri + ", pictureUri=" + pictureUri
                + ", fontLocalPath=" + fontLocalPath + ", pictureLocalPath="
                + pictureLocalPath + ", isApplied=" + isApplied
                + ", isDownloaded=" + isDownloaded + ", packageName="
                + packageName + ", loveNumbers=" + loveNumbers
                + ", downloadNumbers=" + downloadNumbers + "]";
    }
	
	
	

	
	
	
	
	
}
