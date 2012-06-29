package com.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MysttActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private ProgressDialog pd;
	private Button btn1;
	private Button btn2;
	
	private TextView mText;
	private SpeechRecognizer sr;
	private static final String TAG = "MyStt3Activity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button speakButton = (Button) findViewById(R.id.btn_speak);
        mText = (TextView) findViewById(R.id.text);
        
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        
        btn1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pd = new ProgressDialog(MysttActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置为圆形风格
//				pd.setTitle("提示");//设置标题  
//				pd.setIcon(R.drawable.voice);//设置图标
				pd.setMessage("请开始说话");  
				pd.setIndeterminate(false);//设置进度条是否为不明确  
				pd.setCancelable(true);//设置进度条是否可以按退回键取消  
				pd.setButton("确定", new DialogInterface.OnClickListener(){  
  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.cancel();  
                          
                    }  
                      
                });  
				pd.show();  
			}
		});

//        speakButton.setOnClickListener(this);
//
//		sr = SpeechRecognizer.createSpeechRecognizer(this);
//		sr.setRecognitionListener(new listener());
    }

	class listener implements RecognitionListener
	{
	    public void onReadyForSpeech(Bundle params) 
		{
			Log.d(TAG, "onReadyForSpeech");
		}
	    public void onBeginningOfSpeech()
		{
			Log.d(TAG, "onBeginningOfSpeech");
		}
	    public void onRmsChanged(float rmsdB)
		{
			Log.d(TAG, "onRmsChanged");
		}
	    public void onBufferReceived(byte[] buffer)
		{
			Log.d(TAG, "onBufferReceived");
		}
	    public void onEndOfSpeech() 
		{
			Log.d(TAG, "onEndofSpeech");
		}
	    public void onError(int error) 
		{
			Log.d(TAG,  "error " +  error);
			mText.setText("error " + error);
		}
	    public void onResults(Bundle results) 
		{
			String str = new String();
			Log.d(TAG, "onResults " + results);
			ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			for (int i = 0; i < data.size(); i++)
			{
				Log.d(TAG, "result " + data.get(i));
				str += data.get(i);
			}
			mText.setText(str);
		}
	    public void onPartialResults(Bundle partialResults) 
		{
			Log.d(TAG, "onPartialResults");
		}
	    public void onEvent(int eventType, Bundle params) 
		{
			Log.d(TAG, "onEvent " + eventType);
		}
	}

    public void onClick(View v) {
        if (v.getId() == R.id.btn_speak) {
        	
        	
        	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        	intent.putExtra("calling_package", "org.filsa.trivoice");

			sr.startListening(intent);
        }
    }
}