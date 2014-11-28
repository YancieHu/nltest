package com.tidemedia.nntv.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.sliding.fragment.DiscloseDetailFragment;

public class DiscloseDetailActivity extends BaseActivity {
	private static final String TAG = "DiscloseDetailActivity";
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		DiscloseDetailFragment fragment = new DiscloseDetailFragment(id);
		getFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content, fragment)
		.commit();
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.title)).setText("爆料详情");
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra("id");
		}
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
