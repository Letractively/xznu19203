package com.jiangjun.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MyFlashLightActivity extends Activity {

	private int[] color = { R.drawable.white, R.drawable.blue, R.drawable.pink,
			R.drawable.green, R.drawable.orange, R.drawable.yellow };
	private int[] text = { R.string.white, R.string.blue, R.string.pink,
			R.string.green, R.string.orange, R.string.yellow };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 1, R.string.color);
		menu.add(0, 2, 2, R.string.exit);
		return super.onCreateOptionsMenu(menu);
	}

	// 当用户点击菜单项
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			new AlertDialog.Builder(MyFlashLightActivity.this).setTitle(
					getResources().getString(R.string.title)).setAdapter(
					new MyAdapter(this, color, text), adapterListener)
					.setPositiveButton("取消", null).show();
			System.out.println("颜色");
		} else {
			System.out.println("退出");
		}
		return super.onOptionsItemSelected(item);
	}

	OnClickListener adapterListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			// 更改背景颜色

		}
	};
}