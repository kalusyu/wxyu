package com.sg.mtfont.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.sg.mtfont.mail.MailSenderInfo;
import com.sg.mtfont.mail.SimpleMailSender;



/**
 * 
 * @author "biaowen.yu"
 * @date 2014-4-6 下午10:37:44
 */
public class CommonUtils {

	public static final String FontXiu = "FontXiu";
	
	public static final String CURRENT_POINTS = "CURRENT_POINTS";
	
	
	public static void toastText(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}


	public static void toastText(Context context, int adTabTitle) {
		Toast.makeText(context, adTabTitle, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 
	 * @author "biaowen.yu"
	 * @date 2014-4-8 下午10:56:35
	 * @description 
	 * @return
	 */
	public static MailSenderInfo initEmail() {
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("dnfpublic@163.com");
		mailInfo.setPassword("dnfgzs123456");// 您的邮箱密码
		mailInfo.setFromAddress("dnfpublic@163.com");
		mailInfo.setToAddress("741470894@qq.com");
		return mailInfo;
	}
	
	/**
	 * 
	 * @author "biaowen.yu"
	 * @date 2014-4-12 上午9:36:05
	 * @description 
	 * @param context
	 * @return
	 */
	public static String getUserInfo(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		String imei = mTelephonyMgr.getDeviceId();
		StringBuilder sb = new StringBuilder();
		sb.append("用户手机信息：\n").append("imsi=").append(imsi).append("\n").append("imei=").append(imei).append("\n");
		sb.append("\n\n\n\n");
		sb.append("产品PRODUCT: ").append(android.os.Build.PRODUCT).append("\n");// /
		sb.append("品牌BRAND: ").append(android.os.Build.BRAND).append("\n");// /
		sb.append("制造商MANUFACTURER: ").append(android.os.Build.MANUFACTURER)
		.append("\n");
		sb.append("设备DEVICE: ").append(android.os.Build.DEVICE).append("\n");
		sb.append("SDK: ").append(android.os.Build.VERSION.SDK_INT);// //
		
		sb.append("BOARD: ").append(android.os.Build.BOARD).append("\n");// /
		sb.append("DISPLAY: ").append(android.os.Build.DISPLAY).append("\n");
		sb.append("HOST: ").append(android.os.Build.HOST).append("\n"); // //
		sb.append("MODEL: ").append(android.os.Build.MODEL).append("\n");// /
		sb.append("TIME: ").append(android.os.Build.TIME).append("\n");
		sb.append("ANDROID VERSION: ").append(android.os.Build.VERSION.RELEASE);// //
		
		return sb.toString();
	}
	
	
	public static String getPhoneInfo(Context ctx) {
		try{
			TelephonyManager telephonyManager= (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			StringBuilder sb = new StringBuilder();
			String imei=telephonyManager.getDeviceId();
			sb.append("\n imei="+imei);
			sb.append("\n Subscriber imsi=").append(telephonyManager.getSubscriberId()).append("\n");
			sb.append("\n 手机型号model=").append(Build.MODEL);
			sb.append("\n").append("手机号码:"+telephonyManager.getLine1Number());
			sb.append("\n").append("手机网络类型："+telephonyManager.getNetworkType());
			sb.append("\n").append("运营商："+telephonyManager.getSimOperatorName());
			sb.append("\n").append("是否漫游状态:"+telephonyManager.isNetworkRoaming());
			sb.append("\nSimCountryIso = " + telephonyManager.getSimCountryIso());  
	        sb.append("\nSimOperator = " + telephonyManager.getSimOperator());  
	        sb.append("\nSimSerialNumber = " + telephonyManager.getSimSerialNumber());  
	        sb.append("\nSimState = " + telephonyManager.getSimState());
			return sb.toString();
		}catch (Exception e){
			return "";
		}
	}
	
	private static SimpleMailSender sms;
	
	private static SimpleMailSender getInstance(){
		return sms;
	}
	
	public static void sendEmail(MailSenderInfo mailInfo){
		if (sms == null){
			sms = new SimpleMailSender();
		}
		sms.sendTextMail(mailInfo);
	}
	
}
