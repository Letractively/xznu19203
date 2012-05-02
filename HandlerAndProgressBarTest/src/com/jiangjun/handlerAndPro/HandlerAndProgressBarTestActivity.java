package com.jiangjun.handlerAndPro;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class HandlerAndProgressBarTestActivity extends Activity {
	
	private Button start = null;
	private ProgressBar myBar = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        start = (Button) findViewById(R.id.start);
        myBar = (ProgressBar) findViewById(R.id.myBar);
        
        start.setOnClickListener(new ButtonListener());
    }
    
    class ButtonListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			myBar.setVisibility(View.VISIBLE);
			updateHandler.post(updateThread);
		}
    	
    }
    
    //使用handler的匿名内部类，来复写handleMessage的方法
    Handler updateHandler = new Handler(){
    	
    	@Override
    	public void handleMessage(Message msg){
    		myBar.setProgress(msg.arg1);
    		updateHandler.post(updateThread);
    	}
    };
    
    //线程类、使用匿名内部类
    Runnable updateThread = new Runnable() {
		int i = 0;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("Begin Thread");
			i = i + 10;
			//得到一个消息对象，Message类是有Android操作系统提供
			Message msg = updateHandler.obtainMessage();
			msg.arg1 = i;//将msg对象的arg1参数设置为i的值,用arg1和arg2这两个成员变量传递消息的优点是系统性能消耗比较少
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			updateHandler.sendMessage(msg);
			if(i == 100)
			{
				updateHandler.removeCallbacks(updateThread);
			}
		}
	};
}