package com.tidemedia.nntv.activity;

import android.content.Intent;
import android.os.Bundle;

import com.tidemedia.nntv.model.ColumnResponseModel.Column;
import com.tidemedia.nntv.sliding.fragment.NoSingleFragment;

public class NoSingleActivity extends BaseActivity {
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		Column column = new Column();
		column.setId(id);
		NoSingleFragment fragment = new NoSingleFragment(column);
		getFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content, fragment)
		.commit();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra("id");
		}
	}
}
