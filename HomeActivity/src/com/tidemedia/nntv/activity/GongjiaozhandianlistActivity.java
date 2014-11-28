package com.tidemedia.nntv.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.adapter.GongjiaozhandianlistAdapter;

public class GongjiaozhandianlistActivity extends BaseActivity implements
		OnGetBusLineSearchResultListener, OnGetPoiSearchResultListener {

	private ListView mListView;
	private GongjiaozhandianlistAdapter newsAdapter;
	private BusLineSearch mBusLineSearch = null;
	private String title = "";
	private PoiSearch mSearch = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_gongjiaozhandianlist);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title = extras.getString("keyword");
			if (title != null) {
				mSearch = PoiSearch.newInstance();
				mSearch.setOnGetPoiSearchResultListener(this);
				mSearch.searchInCity((new PoiCitySearchOption()).city("南宁")
						.keyword(title).pageCapacity(100));
				// mBusLineSearch.searchBusLine(new BusLineSearchOption().city(
				// "南宁").uid(uid));
			}
		}

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
		((TextView) findViewById(R.id.title)).setText(title);

		mListView = (ListView) findViewById(R.id.listView);
		mListView.setDivider(getResources()
				.getDrawable(R.drawable.divider_line));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// mBusLineSearch.destroy();
	}

	@Override
	public void onCancelFromThread(String msg, int taskId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetBusLineResult(BusLineResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(GongjiaozhandianlistActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}

		// setContentView(R.layout.layout_gongjiaozhandianlist);
		// mListView = (ListView) findViewById(R.id.listView);
		// mListView.setDivider(getResources()
		// .getDrawable(R.drawable.divider_line));
		// newsAdapter = new GongjiaoluxianAdapter(getApplicationContext(),
		// result.getStations(), mListView);
		// mListView.setAdapter(newsAdapter);
		// mListView.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // 此处传回来的position和mAdapter.getItemId()获取的一致;
		// List<BusStation> list = newsAdapter.getList();
		// BusStation newsModel = list.get(position);
		// if (null != newsModel) {
		// // Intent intent = new Intent(mContext,
		// // JingdianActivity.class);
		// // intent.putExtra("weathername", newsModel.getName());
		// // intent.putExtra("weatherurl", newsModel.getUrl());
		// // startActivity(intent);
		// }
		// }
		// });
		// Toast.makeText(GongjiaozhandianlistActivity.this,
		// result.getBusLineName(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(GongjiaozhandianlistActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		List<String> poilist = new ArrayList<String>();
		// 遍历所有poi，找到类型为公交线路的poi
		for (PoiInfo poi : result.getAllPoi()) {
			if (poi.type == PoiInfo.POITYPE.BUS_STATION) {
				if (poi.address != null && poi.address.length() > 0) {
					String[] split = poi.address.split(";");
					if (split != null) {
						for (String string : split) {
							poilist.add(string);
						}
					}
				}
			}
		}
		newsAdapter = new GongjiaozhandianlistAdapter(getApplicationContext(),
				poilist, mListView);
		mListView.setAdapter(newsAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				List<String> list = newsAdapter.getList();
				String newsModel = list.get(position);
				if (null != newsModel) {
					Intent intent = new Intent(getApplicationContext(),
							GongjiaoluxianActivity.class);
					intent.putExtra("flag", 1);
					intent.putExtra("name", newsModel);
					startActivity(intent);
				}
			}
		});
	}
}
