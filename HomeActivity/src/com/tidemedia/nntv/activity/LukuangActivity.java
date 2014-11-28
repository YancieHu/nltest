package com.tidemedia.nntv.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.common.TvApplication;

public class LukuangActivity extends BaseActivity {
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_yingyong_lukuang);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		LatLng p = new LatLng(22.80656, 108.296048);
		TvApplication app = (TvApplication) getApplication();
		BDLocation location = app.getLocation();
		if (location != null) {
			p = new LatLng(location.getLatitude(), location.getLongitude());
			mBaiduMap.setMyLocationEnabled(true);
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius()).direction(100)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
		}
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder().target(p).build());
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.setTrafficEnabled(true);

		// title
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
		((TextView) findViewById(R.id.title)).setText("交通路况");

		View view = findViewById(R.id.lukuangButton);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						LukuanglistActivity.class);
				startActivity(intent);
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
