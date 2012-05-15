package com.jiangjun.flashlight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * TODO 自定义的适配器
 * 
 * @author jun.jiang
 * @version C02 2012-5-15
 * @since OpenEye TAPS_AGENT V1R1C02
 */
public class MyAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private int[] color;
	private int[] text;

	public MyAdapter(Context context, int[] _color, int[] _text) {
		mInflater = LayoutInflater.from(context);
		color = _color;
		text = _text;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return text[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return color[position];
	}

	@Override
	public View getView(int position, View conventView, ViewGroup vg) {
		// TODO Auto-generated method stub
		TextView holder = null;
		if(conventView == null)
		{
			conventView = mInflater.inflate(R.layout.changecolor, null);
			holder = (TextView) conventView.findViewById(R.id.myText);
			conventView.setTag(holder);
		}
		else {
			holder = (TextView) conventView.getTag();
		}
		holder.setText(text[position]);
		holder.setBackgroundResource(color[position]);
		return conventView;
	}

}
