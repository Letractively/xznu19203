package com.jiangjun.voicerecognition;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class VoicerecognitionTestActivity extends Activity implements
		OnClickListener {
/**
 * 修改修改修改
 */
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private ListView mList;
	private Button speekButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		speekButton = (Button) findViewById(R.id.speekButton);
		mList = (ListView) findViewById(R.id.mList);

		PackageManager pm = getPackageManager();
		List activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		if (activities.size() != 0) {
			speekButton.setOnClickListener(this);
		} else {
			speekButton.setEnabled(false);
			speekButton.setText("Recognizer not present");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.speekButton) {
			startVoiceRecognitionActivity();
		}
	}

	private void startVoiceRecognitionActivity() {
		try {
			// 通过Intent传递语音识别的模式
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			// 语言模式和自由形式的语音识别
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			// 提示语音开始
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
					"Speech recognition demo");
			// 开始执行我们的Intent，语音识别
			startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		} catch (Exception e) {
			// TODO: handle exception
			//找不到语音设置装备
			Toast.makeText(this, "ActivityNotFoundException", Toast.LENGTH_LONG).show();
		}
		
	}

	/**
	 * 当语音结束时的回调函数
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			// 取得语音的字符
			ArrayList matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			mList.setAdapter(new ArrayAdapter(this,
					R.layout.my_simple_list_item,matches));
			for(int i=0;i<matches.size();i++)
			{
				System.out.println("=======================" + matches.get(i));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

}