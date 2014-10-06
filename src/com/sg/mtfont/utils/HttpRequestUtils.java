
package com.sg.mtfont.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sg.mtfont.bean.DeviceInfo;
import com.sg.mtfont.bean.FontFile;

/**
 * @author Kalus Yu
 */
public class HttpRequestUtils {
    static final String TAG = "HttpRequestUtils";

    static final int ERROR = -101;


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
    
    /**
     * common request
     * @author Kalus Yu
     * @param gurl
     * @return
     * 2014年10月3日 下午7:54:53
     */
    public static String commonReuest(String gurl){
        String urlStr = gurl;
        InputStreamReader in = null;
        HttpURLConnection urlConn = null;
        try{
            URL url = new URL(urlStr);
            urlConn = (HttpURLConnection)url.openConnection();
            in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (urlConn != null){
                urlConn.disconnect();
            }
        }
        
        return null;
    }
    
    /**
     * 
     * @author Kalus Yu
     * @return
     * 2014年10月3日 下午5:30:20
     */
    public static ArrayList<FontFile> getFontFileLists(){
        String urlStr = Constant.sUrl + Constant.methodgetAllDownload;
        return handleResponse(commonReuest(urlStr));
    }

    /**
     * 
     * @author Kalus Yu
     * @param str
     * @return
     * 2014年10月3日 下午7:54:29
     */
    private static ArrayList<FontFile> handleResponse(String str) {
        JSONObject json;
        ArrayList<FontFile> lists = new ArrayList<FontFile>();
        try {
            if (str != null){
                json = new JSONObject(str);
                JSONArray ja = json.getJSONArray("fileinfo");
                Gson gson = new Gson();
                for (int i = 0; i < ja.length(); i++){
                    FontFile fontfile = gson.fromJson(ja.getJSONObject(i).toString(), FontFile.class);
                    lists.add(fontfile);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
    
    /**
     * 
     * @author Kalus Yu
     * @param imei
     * @return
     * 2014年10月3日 下午7:54:33
     */
    public static boolean checkIsFreeUser(String imei){
        String url = Constant.sUrl + Constant.methodgetisFreeUser + File.separatorChar + imei;
        String str = commonReuest(url);
        if (!TextUtils.isEmpty(str) && str.equals("true")){
            return true;
        }
        return false;
    }

}
