package com.sg.mtfont.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.sg.mtfont.bean.FontFile;
import com.sg.mtfont.task.AsynImageLoader;


public class FileUtils {

	public static final String TAG = "FileUtils";
	
	private static String mPath = Environment.getExternalStorageDirectory()
			+ "/fontxiuxiu";

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
	public static File createSDDir(String dirName) {
		File dir = new File(mPath + File.separatorChar + dirName);
		if (!dir.exists()) { 
			dir.mkdirs();
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
			createSDDir(path);
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
					} else if (parser.getName().equals("fontDisplayName")) {
						eventType = parser.next();
						fontFile.setFontDisplayName(parser.getText());
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
	
	
	public static File getCacheFile(String imageUri){  
        File cacheFile = null;  
        try {  
            if (Environment.getExternalStorageState().equals(  
                    Environment.MEDIA_MOUNTED)) {  
                File sdCardDir = Environment.getExternalStorageDirectory();  
                String fileName = getFileName(imageUri);  
                File dir = new File(sdCardDir.getCanonicalPath()  
                        + AsynImageLoader.CACHE_DIR);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                cacheFile = new File(dir, fileName);  
                Log.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir + ",file:" + fileName);  
            }    
        } catch (IOException e) {  
            e.printStackTrace();  
            Log.e(TAG, "getCacheFileError:" + e.getMessage());  
        }  
          
        return cacheFile;  
    }  
      
    public static String getFileName(String path) {  
        int index = path.lastIndexOf("/");  
        return path.substring(index + 1);  
    }  
    
    // add 
	public static final String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath() + File.separator;
	private static final String IMAGE_FOLDER_NAME = "images";

	public static final String NEW_LINE = System.getProperty("line.separator",
			"\n");

	public static void saveBitmap(Bitmap bm, String fileName, Context context) {
		if (null == bm || isEmpty(fileName) || null == context)
			return;
//		String sdPath = getAppName(context);
//		if (isEmpty(sdPath))
//			return;
		File dirFile = new File(mPath + File.separator + IMAGE_FOLDER_NAME
				+ File.separator);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File myCaptureFile = new File(mPath + File.separator
				+ IMAGE_FOLDER_NAME + File.separator + fileName + "bak");
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
			bos.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bos) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		myCaptureFile.renameTo(new File(mPath + File.separator
				+ IMAGE_FOLDER_NAME + File.separator + fileName));
	}

	public static String getAppName(Context context) {
		String packageName = context.getPackageName();
		if (packageName == null)
			return null;
		int index = packageName.lastIndexOf(".");
		if (index <= 0)
			return null;
		return packageName.substring(index + 1, packageName.length());
	}

	public static String getAppSDCardPath(Context context) {
		String appName = getAppName(context);
		if (appName == null)
			return null;
		return SDCARD_PATH + appName;
	}

	public static String getImagePath(Context context) {
		// return getAppSDCardPath(context) + File.separator + IMAGE_FOLDER_NAME
		// + File.separator;
		return mPath + File.separator + IMAGE_FOLDER_NAME + File.separator;
	}

	public static String getNameByUrl(String url) {
		if (isEmpty(url))
			return null;
		return url.substring(url.lastIndexOf("/") + 1, url.length())
				.replace(".jpg", "").replace(".png", "");
	}

	public static boolean isSdCardExist() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	private static boolean isEmpty(String str) {
		if (null == str || str.length() == 0) {
			return true;
		}
		return false;
	}
}