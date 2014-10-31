package com.sg.mtfont.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 
 * @author KaluYu
 * 
 */
public class FontRestClient {

	public static final String BASE_URL = Constant.sUrl;

	private static AsyncHttpClient client = new AsyncHttpClient();

	/**
	 * 
	 * TODO
	 * KaluYu
	 * @param url
	 * @param params
	 * @param responseHandler
	 * 2014年10月30日 下午11:44:33
	 */
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 
	 * TODO
	 * KaluYu
	 * @param url
	 * @param params
	 * @param responseHandler
	 * 2014年10月30日 下午11:44:37
	 */
	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 
	 * TODO
	 * KaluYu
	 * @param relativeUrl
	 * @return
	 * 2014年10月30日 下午11:44:41
	 */
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

}
