package com.hly.fontxiu.feedback;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hly.fontxiu.R;
import com.hly.fontxiu.mail.MailSenderInfo;
import com.hly.fontxiu.mail.SimpleMailSender;
import com.hly.fontxiu.utils.CommonUtils;

public class FeedBackActivity extends Activity{
	
	private EditText mTextContent,mTextContacts;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.feed_back_layout);
		initUI();
	}
	
	private void initUI() {
		mTextContent = (EditText) findViewById(R.id.edt_feed_back);
		mTextContacts = (EditText) findViewById(R.id.edt_contacts);
	}

	/**
	 * 
	 * @author "biaowen.yu"
	 * @date 2014-4-8 下午11:00:27
	 * @description 
	 * @param v
	 */
	public void submitFeedBack(View v){
		
		final MailSenderInfo mailInfo = CommonUtils.initEmail();
		mailInfo.setSubject("用户反馈");

		if (!mTextContent.getText().toString().equals("")){
			
			String mailContent = "反馈内容：\n\n";
			mailContent += mTextContent.getText().toString();
			
			mailContent += CommonUtils.getPhoneInfo(this);
			
			mailContent +="\n反馈人联系方式："+mTextContacts.getText().toString(); 
			mailInfo.setContent(mailContent);
			// 这个类主要来发送邮件
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					SimpleMailSender sms = new SimpleMailSender();
					sms.sendTextMail(mailInfo);//不能再UI线程执行
					
				}
			}).start();
		} 
		Toast.makeText(this, R.string.send_successed, Toast.LENGTH_SHORT).show();
		finish();
	}

	
}
