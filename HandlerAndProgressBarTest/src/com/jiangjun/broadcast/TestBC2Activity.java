package com.jiangjun.broadcast;

import android.app.Activity;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestBC2Activity extends Activity {

	private Button registerButton = null;
	private Button unregisterButton = null;
	private TestReceiver smsReceiver = null;
	
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVE";
	
	class RegisterReceiverListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//生成一个BroadcastReceiver对象
			smsReceiver = new TestReceiver();
			//生成一个IntentFilter对象
			IntentFilter filter = new IntentFilter();
			//为IntentFilter添加一个Action
			filter.addAction(SMS_ACTION);
			TestBC2Activity.this.registerReceiver(smsReceiver, filter);
		}
		
	}
	
	class UnregisterReceiverListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			//接触BroadcastReceiver的注册
			TestBC2Activity.this.unregisterReceiver(smsReceiver);
		}
		
	}
}
