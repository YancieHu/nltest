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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.adapter.GongjiaohuanchengAdapter;
import com.tidemedia.nntv.adapter.GongjiaolistAdapter;
import com.tidemedia.nntv.common.TvApplication;

/**
 * 此demo用来展示如何进行公交线路详情检索，并使用RouteOverlay在地图上绘制 同时展示如何浏览路线节点并弹出泡泡
 */
public class CopyOfGongjiaoActivity extends BaseActivity implements
		OnGetPoiSearchResultListener, BaiduMap.OnMapClickListener,
		OnGetRoutePlanResultListener {
	private PoiSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private ListView mListView;
	private GongjiaolistAdapter mGongjiaolistAdapter;
	private GongjiaohuanchengAdapter mGongjiaohuanchengAdapter;
	View layoutBusXianlu, layoutBusHuancheng, layoutBusZhandian;
	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v == layoutBusXianlu) {
				setContentView(R.layout.layout_search_bus_xianlu);
				layoutBusHuancheng = findViewById(R.id.layoutBusHuancheng);
				layoutBusHuancheng.setOnClickListener(mOnClickListener);
				layoutBusZhandian = findViewById(R.id.layoutBusZhandian);
				layoutBusZhandian.setOnClickListener(mOnClickListener);
			} else if (v == layoutBusHuancheng) {
				setContentView(R.layout.layout_search_bus_huancheng);
				layoutBusXianlu = findViewById(R.id.layoutBusXianlu);
				layoutBusXianlu.setOnClickListener(mOnClickListener);
				layoutBusZhandian = findViewById(R.id.layoutBusZhandian);
				layoutBusZhandian.setOnClickListener(mOnClickListener);
			} else if (v == layoutBusZhandian) {
				setContentView(R.layout.layout_search_bus_zhandian);
				layoutBusXianlu = findViewById(R.id.layoutBusXianlu);
				layoutBusXianlu.setOnClickListener(mOnClickListener);
				layoutBusHuancheng = findViewById(R.id.layoutBusHuancheng);
				layoutBusHuancheng.setOnClickListener(mOnClickListener);
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		((TextView) findViewById(R.id.title)).setText("公交查询");

		setContentView(R.layout.layout_search_bus_xianlu);
		layoutBusHuancheng = findViewById(R.id.layoutBusHuancheng);
		layoutBusHuancheng.setOnClickListener(mOnClickListener);
		layoutBusZhandian = findViewById(R.id.layoutBusZhandian);
		layoutBusZhandian.setOnClickListener(mOnClickListener);

		mSearch = PoiSearch.newInstance();
		mSearch.setOnGetPoiSearchResultListener(this);

		LatLng p = new LatLng(22.80656, 108.296048);
		TvApplication app = (TvApplication) getApplication();
		BDLocation location = app.getLocation();
		if (location != null) {
			p = new LatLng(location.getLatitude(), location.getLongitude());
			mSearch.searchNearby(new PoiNearbySearchOption().location(p)
					.radius(3000).keyword("公交站"));
		}
	}

	/**
	 * 发起检索
	 * 
	 * @param v
	 */
	public void clickSearchBusXianlu(View v) {
		EditText editSearchKey = (EditText) findViewById(R.id.searchInput);
		// 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
		mSearch.searchInCity((new PoiCitySearchOption()).city("南宁")
				.keyword(editSearchKey.getText().toString()).pageCapacity(100));
	}

	public void clickSearchBusHuancheng(View v) {
		EditText startSearchKey = (EditText) findViewById(R.id.editText_search_bus_huancheng1);
		EditText endSearchKey = (EditText) findViewById(R.id.editText_search_bus_huancheng2);
		// 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
		// mSearch.searchInCity((new PoiCitySearchOption()).city("南宁")
		// .keyword(endSearchKey.getText().toString()).pageCapacity(100));
		RoutePlanSearch mRoutePlanSearch = RoutePlanSearch.newInstance();
		mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("南宁",
				startSearchKey.getText().toString());
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("南宁", endSearchKey
				.getText().toString());
		mRoutePlanSearch.transitSearch((new TransitRoutePlanOption())
				.from(stNode).city("南宁").to(enNode));
	}

	public void clickSearchBusZhandian(View v) {
		EditText editSearchKey = (EditText) findViewById(R.id.editText_search_bus_zhandian);
		// 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
		String keyword = editSearchKey.getText().toString();
		if (keyword.length() > 0) {
			Intent intent = new Intent(getApplicationContext(),
					GongjiaozhandianlistActivity.class);
			intent.putExtra("keyword", keyword);
			startActivity(intent);
		} else {
			Toast.makeText(this, R.string.hint_search_bus_zhandian,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetPoiResult(PoiResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(CopyOfGongjiaoActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		List<PoiInfo> poilist = new ArrayList<PoiInfo>();
		// 遍历所有poi，找到类型为公交线路的poi
		for (PoiInfo poi : result.getAllPoi()) {
			if (poi.type == PoiInfo.POITYPE.BUS_LINE
					|| poi.type == PoiInfo.POITYPE.BUS_STATION
					|| poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
				// busLineIDList.add(poi.uid);
				poilist.add(poi);
			}
		}
		// SearchNextBusline(null);
		mListView = (ListView) findViewById(R.id.listView);
		mListView.setDivider(getResources()
				.getDrawable(R.drawable.divider_line));
		mGongjiaolistAdapter = new GongjiaolistAdapter(getApplicationContext(),
				poilist, mListView);
		mListView.setAdapter(mGongjiaolistAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				List<PoiInfo> list = mGongjiaolistAdapter.getList();
				PoiInfo newsModel = list.get(position);
				if (null != newsModel) {
					Intent intent = new Intent(getApplicationContext(),
							GongjiaoluxianActivity.class);
					intent.putExtra("uid", newsModel.uid);
					intent.putExtra("name", newsModel.name);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

	}

	@Override
	public void onMapClick(LatLng point) {
		// mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			List<TransitRouteLine> routeLines = result.getRouteLines();
			mListView = (ListView) findViewById(R.id.listView);
			mListView.setDivider(getResources().getDrawable(
					R.drawable.divider_line));
			mGongjiaohuanchengAdapter = new GongjiaohuanchengAdapter(
					getApplicationContext(), routeLines, mListView);
			mListView.setAdapter(mGongjiaohuanchengAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// 此处传回来的position和mAdapter.getItemId()获取的一致;
					List<TransitRouteLine> list = mGongjiaohuanchengAdapter
							.getList();
					TransitRouteLine newsModel = list.get(position);
					if (null != newsModel) {
						Intent intent = new Intent(getApplicationContext(),
								GongjiaoluxianActivity.class);
						intent.putExtra("name", newsModel.getTitle());
						startActivity(intent);
					}
				}
			});
		}
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub

	}
}
