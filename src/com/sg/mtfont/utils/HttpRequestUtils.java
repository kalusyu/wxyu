
package com.sg.mtfont.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.sg.mtfont.bean.DeviceInfo;

/**
 * @author Kalus Yu
 */
public class HttpRequestUtils {
    static final String TAG = "HttpRequestUtils";

    static final int ERROR = -101;

    /**
     * @author Kalus Yu
     * @param info
     * @return 2014年8月25日 上午10:45:46
     */
    /*
     * public static int sendDeviceInfo(DeviceInfo info) { String urlStr =
     * Constant.sUrl+Constant.methodSaveDeviceInfoPath; URL url = null; try {
     * url = new URL(urlStr); HttpURLConnection urlConn = (HttpURLConnection)
     * url.openConnection(); urlConn.setDoInput(true);
     * urlConn.setDoOutput(true); urlConn.setRequestMethod("POST");
     * urlConn.setUseCaches(false); urlConn.setRequestProperty("Content-Type",
     * "application/x-www-form-urlencoded");
     * urlConn.setRequestProperty("Charset", "utf-8"); DataOutputStream dop =
     * new DataOutputStream(urlConn.getOutputStream()); Gson gson = new Gson();
     * String dinfo = gson.toJson(info); dop.writeBytes(dinfo); dop.flush();
     * dop.close(); return urlConn.getResponseCode(); } catch (Exception e) {
     * Log.e(TAG, "sendDeviceInfo "+e.getMessage()); return ERROR; } }
     */

    public static int sendDeviceInfo(DeviceInfo info) {
        String urlStr = Constant.sUrl + Constant.methodSaveDeviceInfoPath;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urlStr);
            Gson gson = new Gson();
            String dinfo = gson.toJson(info);
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            nameValuePair.add(new BasicNameValuePair("jinfo", dinfo));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            HttpResponse res = httpClient.execute(httpPost);
            return res.getStatusLine().getStatusCode();
        } catch (Exception e) {
            Log.e(TAG, "sendDeviceInfo " + e.getMessage());
            return ERROR;
        }
    }

}
