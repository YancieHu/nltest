package com.tidemedia.nntv.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.sliding.fragment.WeatherFragment;

public class WeatherActivity extends BaseActivity {
	private WeatherFragment fragment;
	private Button buttonWeather, buttonKongqi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragment = new WeatherFragment();
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back2);
		findViewById(R.id.action_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		buttonWeather = (Button) findViewById(R.id.buttonWeather);
		buttonKongqi = (Button) findViewById(R.id.buttonKongqi);
		buttonWeather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.setStatus(1);
				buttonWeather.setBackgroundColor(0xffff0000);
				buttonWeather.setTextColor(0xffffffff);
				buttonKongqi.setTextColor(0xff000000);
				buttonKongqi.setBackgroundColor(0xffcccccc);
			}
		});

		buttonKongqi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				fragment.setStatus(2);
				buttonKongqi.setBackgroundColor(0xffff0000);
				buttonKongqi.setTextColor(0xffffffff);
				buttonWeather.setTextColor(0xff000000);
				buttonWeather.setBackgroundColor(0xffcccccc);
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
