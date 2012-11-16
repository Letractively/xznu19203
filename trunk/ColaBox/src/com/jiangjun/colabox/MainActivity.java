package com.jiangjun.colabox;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private Handler mHandler = new Handler();
	private ImageView imageView01 = null;
	private int alpha = 255;
	private int b = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        imageView01 = (ImageView) findViewById(R.id.imageView01);
        imageView01.setAlpha(alpha);
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				initApp();
				
				try {
					while (b < 2){
						if(b == 0){
							Thread.sleep(2000);
							b = 1;
						}else {
							Thread.sleep(50);
							updateApp();
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
		}).start();
        
        mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				imageView01.setAlpha(alpha);
				imageView01.invalidate();//使无效
				
			}
        	
        };
        
    }
    
    public void updateApp()
    {
    	alpha -= 5;
    	if(alpha <=0){
    		b = 2;
    		Intent intent = new Intent(this,com.jiangjun.colabox.ui.Frm_Addbills.class);
    		startActivity(intent);
    	}
    	mHandler.sendMessage(mHandler.obtainMessage());
    }
    
    public void initApp()
    {
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
