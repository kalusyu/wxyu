package com.hly.fontxiu.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Environment;
import android.util.Xml;

import com.hly.fontxiu.bean.FontFile;

public class FileUtils {

	private static String mPath = Environment.getExternalStorageDirectory()
			+ "/fontxiuxiu";;

	public static String getSDCardPath() {
		return mPath;
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public static File creatSDFile(String fileName) throws IOException {
		File file = new File(mPath + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public static File creatSDDir(String dirName) {
		File dir = new File(mPath + dirName);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(mPath + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public static File write2SDFromInput(String path, String fileName,
			InputStream is) {
		File file = null;
		FileOutputStream os = null;
		try {
			creatSDDir(path);
			file = creatSDFile(path + File.separatorChar + fileName);
			os = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			int len;
			while ((len = is.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 
	 * @return
	 */
	public static List<FontFile> parseXmlFile(InputStream is) {
		XmlPullParser parser = Xml.newPullParser();
		List<FontFile> mList = null;
		try {
			parser.setInput(is, "UTF-8");
			int eventType = parser.getEventType();
			FontFile fontFile = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					mList = new ArrayList<FontFile>();
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("fontType")) {
						fontFile = new FontFile();
					} else if (parser.getName().equals("fontName")) {
						eventType = parser.next();
						fontFile.setFontName(parser.getText());
					} else if (parser.getName().equals("fontSize")) {
						eventType = parser.next();
						fontFile.setFontSize(parser.getText());
					} else if (parser.getName().equals("fontNamePic")) {
						eventType = parser.next();
						fontFile.setFontNamePic(parser.getText());
					} else if (parser.getName().equals("fontNamePicUri")) {
						eventType = parser.next();
						fontFile.setFontNamePicUri(parser.getText());
					} else if (parser.getName().equals("pictureNames")) {
						eventType = parser.next();
						if (parser.getText() != null) {
							fontFile.setPictureNames(parser.getText()
									.split(";"));
						} else {
							fontFile.setPictureNames(null);
						}
					} else if (parser.getName().equals("fontUri")) {
						eventType = parser.next();
						fontFile.setFontUri(parser.getText());
					} else if (parser.getName().equals("pictureUri")) {
						eventType = parser.next();
						fontFile.setPictureUri(parser.getText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("fontType")) {
						mList.add(fontFile);
						fontFile = null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mList;
	}
	
	/**
	 * 
	 * @param url
	 * @param downloadPath
	 * @param isShowProgressBar
	 */
	public static void downloadFile(String url,String downloadPath,boolean isShowProgressBar){
		//TODO
	}
}