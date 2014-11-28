package com.tidemedia.nntv.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.adapter.JiayouzhanlistAdapter;
import com.tidemedia.nntv.common.TvApplication;

public class JiayouzhanlistActivity extends BaseActivity implements
		OnGetPoiSearchResultListener {

	private ListView mListView;
	private JiayouzhanlistAdapter newsAdapter;
	private PoiSearch mPoiSearch = null;
	private List<PoiInfo> poiList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_jiayouzhan_list);

		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		if (JiayouzhanActivity.result != null) {
			poiList = JiayouzhanActivity.result.getAllPoi();
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
		((TextView) findViewById(R.id.title)).setText("加油站");
		final View viewJiayouzhanPinpai = findViewById(R.id.layoutJiayouzhanPinpai);
		viewJiayouzhanPinpai.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JiayouzhanPinpaiDialog dialog = new JiayouzhanPinpaiDialog(
						JiayouzhanlistActivity.this, R.style.myDialogTheme);
				// dialog.show();
				Window dialogWindow = dialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				dialogWindow
						.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
				// 显示的坐标
				lp.x = 0;
				lp.y = getActionBar().getHeight()
						+ viewJiayouzhanPinpai.getHeight();
				lp.width = viewJiayouzhanPinpai.getWidth();
				lp.height = viewJiayouzhanPinpai.getHeight();
				dialogWindow.setAttributes(lp);
				dialog.show();
			}
		});
		final View viewJiayouzhanFanwei = findViewById(R.id.layoutJiayouzhanFanwei);
		viewJiayouzhanFanwei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(getApplicationContext(),
				// JiayouzhanPinpaiActivity.class);
				// intent.putExtra("weathername", newsModel.getName());
				// intent.putExtra("weatherurl", newsModel.getUrl());
				// startActivity(intent);
				JiayouzhanFanweiDialog dialog = new JiayouzhanFanweiDialog(
						JiayouzhanlistActivity.this, R.style.myDialogTheme);
				// dialog.show();
				Window dialogWindow = dialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				dialogWindow
						.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
				// 显示的坐标
				lp.x = 0;
				lp.y = getActionBar().getHeight()
						+ viewJiayouzhanFanwei.getHeight();
				lp.width = viewJiayouzhanFanwei.getWidth();
				lp.height = viewJiayouzhanFanwei.getHeight();
				dialogWindow.setAttributes(lp);
				dialog.show();
			}
		});
		findViewById(R.id.action_map).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JiayouzhanActivity.isback = true;
				finish();
			}
		});

		mListView = (ListView) findViewById(R.id.listView);
		mListView.setDivider(getResources()
				.getDrawable(R.drawable.divider_line));
		newsAdapter = new JiayouzhanlistAdapter(this, poiList, mListView);
		mListView.setAdapter(newsAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				List<PoiInfo> list = newsAdapter.getList();
				PoiInfo newsModel = list.get(position);
				if (null != newsModel) {
					// TODO 地图上显示该点
					finish();
					// Intent intent = new Intent(mContext,
					// JingdianActivity.class);
					// intent.putExtra("weathername", newsModel.getName());
					// intent.putExtra("weatherurl", newsModel.getUrl());
					// startActivity(intent);
				}
			}
		});
		// getRefreshData();
	}

	public class JiayouzhanPinpaiDialog extends Dialog {
		Context mContext;
		ListView mListView;

		public JiayouzhanPinpaiDialog(Context context) {
			super(context);
			this.mContext = context;
		}

		public JiayouzhanPinpaiDialog(Context context, int mydialogtheme) {
			super(context, mydialogtheme);
			this.mContext = context;
		}

		private List<String> getData() {
			List<String> data = new ArrayList<String>();
			data.add("百度图片");
			data.add("腾讯图片");
			data.add("360图片");
			return data;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// setContentView(R.layout.layout_jiayouzhan_pinpai);
			mListView = new ListView(mContext);
			BaseAdapter mListBaseAdapter = new ArrayAdapter<String>(mContext,
					android.R.layout.simple_list_item_1, getData());
			mListView.setAdapter(mListBaseAdapter);
			setContentView(mListView);
			// View pingpaizhongguoshihua =
			// findViewById(R.id.pingpaizhongguoshihua);
			// pingpaizhongguoshihua
			// .setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// dismiss();
			// TvApplication app = (TvApplication) getApplication();
			// BDLocation location = app.getLocation();
			// if (location != null) {
			// LatLng p = new LatLng(location.getLatitude(),
			// location.getLongitude());
			// mPoiSearch
			// .searchNearby(new PoiNearbySearchOption()
			// .location(p).radius(50000)
			// .keyword("中石化加油站")
			// .pageCapacity(20).pageNum(1));
			// }
			// }
			// });
			// View pingpaizhongguoshiyou =
			// findViewById(R.id.pingpaizhongguoshiyou);
			// pingpaizhongguoshiyou
			// .setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// dismiss();
			// TvApplication app = (TvApplication) getApplication();
			// BDLocation location = app.getLocation();
			// if (location != null) {
			// LatLng p = new LatLng(location.getLatitude(),
			// location.getLongitude());
			// mPoiSearch
			// .searchNearby(new PoiNearbySearchOption()
			// .location(p).radius(50000)
			// .keyword("中石油加油站")
			// .pageCapacity(20).pageNum(1));
			// }
			// }
			// });
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			newsAdapter.notifyDataSetChanged(result.getAllPoi());
			// mBaiduMap.clear();
			// PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			// mBaiduMap.setOnMarkerClickListener(overlay);
			// overlay.setData(result);
			// overlay.addToMap();
			// overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
		}

	}

	public class JiayouzhanFanweiDialog extends Dialog {
		Context mContext;

		public JiayouzhanFanweiDialog(Context context) {
			super(context);
			this.mContext = context;
		}

		public JiayouzhanFanweiDialog(Context context, int mydialogtheme) {
			super(context, mydialogtheme);
			this.mContext = context;
		}

		// private ListView mListView;
		// private JiayouzhanlistAdapter newsAdapter;
		// private List<PoiInfo> poiList;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.layout_jiayouzhan_fanwei);
			View fanweijiangnanqu = findViewById(R.id.fanweijiangnanqu);
			fanweijiangnanqu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					mPoiSearch.searchInCity(new PoiCitySearchOption()
							.city("南宁").keyword("江南区 加油站").pageCapacity(20)
							.pageNum(1));
				}
			});
			View fanweiqingxiuqu = findViewById(R.id.fanweiqingxiuqu);
			fanweiqingxiuqu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					mPoiSearch.searchInCity(new PoiCitySearchOption()
							.city("南宁").keyword("清秀区 加油站").pageCapacity(20)
							.pageNum(1));
				}
			});
			View fanweixingningqu = findViewById(R.id.fanweixingningqu);
			fanweixingningqu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					mPoiSearch.searchInCity(new PoiCitySearchOption()
							.city("南宁").keyword("兴宁区 加油站").pageCapacity(20)
							.pageNum(1));
				}
			});
			View fanweixixiangtangqu = findViewById(R.id.fanweixixiangtangqu);
			fanweixixiangtangqu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					mPoiSearch.searchInCity(new PoiCitySearchOption()
							.city("南宁").keyword("西乡塘区 加油站").pageCapacity(20)
							.pageNum(1));
				}
			});
		}
	}

}
