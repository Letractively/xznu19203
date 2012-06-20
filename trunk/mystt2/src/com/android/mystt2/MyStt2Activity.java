package com.android.mystt2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import android.speech.srec.Recognizer;
import android.speech.srec.MicrophoneInputStream;
import java.io.InputStream;
import java.io.IOException;
import android.util.Log;

public class MyStt2Activity extends Activity implements OnClickListener {

	private TextView mText;
	private static final String TAG = "MyStt3Activity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Button speakButton = (Button) findViewById(R.id.btn_speak);
		mText = (TextView) findViewById(R.id.text);

		speakButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_speak) {
			test();
		}
	}

	void test() {
		try {
			InputStream audio = new MicrophoneInputStream(11025, 11025 * 5);
			String cdir = Recognizer.getConfigDir(null);
			Recognizer recognizer = new Recognizer(cdir + "/baseline11k.par");
			Recognizer.Grammar grammar = recognizer.new Grammar(cdir
					+ "/grammars/VoiceDialer.g2g");
			grammar.setupRecognizer();
			grammar.resetAllSlots();
			grammar.compile();
			recognizer.start();
			while (true) {
				switch (recognizer.advance()) {
				case Recognizer.EVENT_INCOMPLETE:
				case Recognizer.EVENT_STARTED:
				case Recognizer.EVENT_START_OF_VOICING:
				case Recognizer.EVENT_END_OF_VOICING:
					continue;
				case Recognizer.EVENT_RECOGNITION_RESULT:
					for (int i = 0; i < recognizer.getResultCount(); i++) {
						String result = recognizer.getResult(i,
								Recognizer.KEY_LITERAL);
						Log.d(TAG, "result " + result);
						mText.setText(result);
					}
					break;
				case Recognizer.EVENT_NEED_MORE_AUDIO:
					recognizer.putAudio(audio);
					continue;
				default:
					break;
				}
				break;
			}
			recognizer.stop();
			recognizer.destroy();
			audio.close();
		} catch (IOException e) {
			Log.d(TAG, "error", e);
			mText.setText("error " + e);
		}
	}
}

