package com.tidemedia.nntv.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.sliding.fragment.JingdianFragment;

public class JingdianActivity extends BaseActivity {
	private Fragment fragment;
	String name, url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			name = extras.getString("weathername");
			url = extras.getString("weatherurl");
		}
		fragment = new JingdianFragment(url);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((TextView) findViewById(R.id.title)).setText(name);
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
