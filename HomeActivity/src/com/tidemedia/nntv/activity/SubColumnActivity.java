package com.tidemedia.nntv.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.model.ColumnResponseModel.Column;
import com.tidemedia.nntv.sliding.fragment.NoSingleFragment;
import com.tidemedia.nntv.sliding.fragment.SingleFragment;

public class SubColumnActivity extends BaseActivity {
	private Fragment fragment;
	private Column column;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getData();
		if(column.isHassingle()){
			fragment = new SingleFragment(column);
		}else{
			fragment = new NoSingleFragment(column);
		}
		getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.title)).setText(column.getTitle());
	}
	
	private void getData(){
		column = (Column)this.getIntent().getSerializableExtra("column");
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
