package com.hly.fontxiu.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

public class XmlUtils {

	public static final String TAG = "XmlUtils";

	public static void saveConfig(Config cfg, OutputStream out) {
		XmlSerializer serializer = Xml.newSerializer();
		try {
			Log.d(TAG, "out=" + out);
			if (out != null) {
				serializer.setOutput(out, "UTF-8");

				serializer.startDocument("UTF-8", true);
				String startTag = "config";
				serializer.startTag(null, startTag);

				serializer.startTag(null, ConfigTag.IS_FREE);
				serializer.text(String.valueOf(cfg.isFree()));
				serializer.endTag(null, ConfigTag.IS_FREE);

				serializer.endTag(null, startTag);
				serializer.endDocument();
				out.flush();
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void readConfig(Config cfg, InputStream is) {
		if (cfg == null) {
			cfg = new Config();
		}
		try {
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(is, "UTF-8"); // 为Pull解释器设置要解析的XML数据
			int event = pullParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (ConfigTag.IS_FREE.equals(pullParser.getName())) {
						cfg.setFree(Boolean.valueOf(pullParser.nextText()));
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = pullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
