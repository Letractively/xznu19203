package com.jiangjun.handlerAndPro;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class HandlerTest2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		System.out.println("Activity ------>" + Thread.currentThread().getId());
		//生成一个HandlerThread对象，实现了使用Looper来处理消息队列的功能
		HandlerThread handlerThread = new HandlerThread("handler_thread");
		//在使用HandlerThread的getLooper()方法之前，必须先调用该类的start()方法
		handlerThread.start();
		MyHandler myHandler = new MyHandler(handlerThread.getLooper());
		Message msg = myHandler.obtainMessage();
		msg.obj = "abc";
		Bundle b = new Bundle();
		b.putInt("age", 20);
		b.putString("name", "Jhon");
		msg.setData(b);
		
		//将Msg发送到目标对象，目标对象就是生成该Msg对象的Handler的对象
		msg.sendToTarget();
	}

	class MyHandler extends Handler {
		
		public MyHandler(){}
		//一定要复写这个构造函数
		public MyHandler(Looper looper){super(looper);}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			System.out.println("Handler ------>" + Thread.currentThread().getId());
			System.out.println("HandlerMessage");
			String s = (String) msg.obj;
			System.out.println(s);
			Bundle b = msg.getData();
			int age = b.getInt("age");
			String name = b.getString("name");
			System.out.println("age -------->" + age + "\nname -------->" + name);
		}
		
	}
}
