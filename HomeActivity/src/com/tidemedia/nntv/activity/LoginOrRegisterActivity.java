package com.tidemedia.nntv.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.sliding.fragment.AccountFragment;

public class LoginOrRegisterActivity extends BaseActivity implements AccountFragment.OnLoginListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
		.add(android.R.id.content, new AccountFragment())
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
		((TextView) findViewById(R.id.title)).setText("登录");
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

	@Override
	public void onSuccess() {
		showToast("登录成功");
		finish();
	}

	@Override
	public void onFail() {
		
	}
}
