package com.tidemedia.nntv.activity;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineResult.BusStation;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.adapter.GongjiaolistAdapter;
import com.tidemedia.nntv.adapter.GongjiaoluxianAdapter;

public class GongjiaoluxianActivity extends BaseActivity implements
		OnGetBusLineSearchResultListener, OnGetPoiSearchResultListener {

	private ListView mListView;
	private GongjiaoluxianAdapter newsAdapter;
	private PoiSearch mSearch = null;
	private BusLineSearch mBusLineSearch = null;
	private int mFlag = 0;
	private String title = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gongjiaoluxian);

		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title = extras.getString("name");

			if (extras.containsKey("flag")) {
				mFlag = extras.getInt("flag");
			}
			if (mFlag == 1) {
				mSearch = PoiSearch.newInstance();
				mSearch.setOnGetPoiSearchResultListener(this);
				mSearch.searchInCity((new PoiCitySearchOption()).city("南宁")// .city("南宁")
						.keyword(title).pageCapacity(100));
			} else {
				String uid = extras.getString("uid");
				if (uid != null) {
					mBusLineSearch.searchBusLine(new BusLineSearchOption()
							.city("南宁").uid(uid));
				}
			}
		}

		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back4);
		findViewById(R.id.action_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((TextView) findViewById(R.id.title)).setText(title);

		ImageView imageView = (ImageView) findViewById(R.id.action_map);
		imageView.setImageResource(R.drawable.bus_line_03);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 地图显示
			}
		});

		View shoucangxianlu = findViewById(R.id.shoucangxianlu);
		shoucangxianlu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(this, "收藏线路成功", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(GongjiaoluxianActivity.this,
						LoginOrRegisterActivity.class);
				startActivity(intent);
			}
		});

		View gongjiaoxianlujiucuo = findViewById(R.id.gongjiaoxianlujiucuo);
		gongjiaoxianlujiucuo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GongjiaoluxianActivity.this,
						GongjiaoxianlujiucuoActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBusLineSearch.destroy();
	}

	@Override
	public void onCancelFromThread(String msg, int taskId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetBusLineResult(BusLineResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(GongjiaoluxianActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}

		TextView textViewName1 = (TextView) this
				.findViewById(R.id.textViewName1);
		textViewName1.setText(result.getBusLineName());
		TextView textViewName2 = (TextView) this
				.findViewById(R.id.textViewName2);
		textViewName2.setText("返程");
		textViewName2.setTextColor(Color.RED);
		TextView textViewName3 = (TextView) this
				.findViewById(R.id.textViewName3);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		textViewName3.setText("首末车：" + sdf.format(result.getStartTime()) + "~"
				+ sdf.format(result.getEndTime()));
		TextView textViewName4 = (TextView) this
				.findViewById(R.id.textViewName4);
		textViewName4.setText(result.getBusCompany());
		textViewName4.setTextColor(Color.RED);
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setDivider(getResources()
				.getDrawable(R.drawable.divider_line));
		newsAdapter = new GongjiaoluxianAdapter(getApplicationContext(),
				result.getStations(), mListView);
		mListView.setAdapter(newsAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				List<BusStation> list = newsAdapter.getList();
				BusStation newsModel = list.get(position);
				if (null != newsModel) {
					// Intent intent = new Intent(mContext,
					// JingdianActivity.class);
					// intent.putExtra("weathername", newsModel.getName());
					// intent.putExtra("weatherurl", newsModel.getUrl());
					// startActivity(intent);
				}
			}
		});

	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		for (PoiInfo poi : result.getAllPoi()) {
			if (poi.type == PoiInfo.POITYPE.BUS_LINE
					|| poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
				mBusLineSearch.searchBusLine(new BusLineSearchOption().city(
						"南宁").uid(poi.uid));
				break;
			}
		}
	}

}
