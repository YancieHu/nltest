package com.tidemedia.nntv.sliding.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.JingdianActivity;
import com.tidemedia.nntv.adapter.WeatherJingdianAdapter;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.WeatherModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.StringUtil;

public class WeatherJingdianFragment extends BaseFragment {

	private ListView mListView;
	private Context mContext;
	private WeatherJingdianAdapter newsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = this.getActivity();
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.layout_weather_list, null);
		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setDivider(getActivity().getResources().getDrawable(
				R.drawable.divider_line));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				List<WeatherModel> list = newsAdapter.getList();
				WeatherModel newsModel = list.get(position);
				if (null != newsModel) {
					Intent intent = new Intent(mContext, JingdianActivity.class);
					intent.putExtra("weathername", newsModel.getName());
					intent.putExtra("weatherurl", newsModel.getUrl());
					startActivity(intent);
				}
			}
		});
		getRefreshData();
		return view;
	}

	private void getRefreshData() {
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("cat_id", "15565");
		ThreadManager.exeTask(this, APIContants.TRAVEL, postParameter,
				Constants.GET_WEATHER_LIST);
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		mListView.setVisibility(View.VISIBLE);
		if (StringUtil.isEmpty(resultData)) {
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
			return;
		}

		List<WeatherModel> list = new ArrayList<WeatherModel>();
		try {
			JSONArray jsonArray = new JSONArray(resultData);
			if (jsonArray != null) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					WeatherModel model = new WeatherModel();
					model.setName(jsonObject.getString("name"));
					model.setUrl(jsonObject.getString("url"));
					list.add(model);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		newsAdapter = new WeatherJingdianAdapter(mContext, list, mListView);
		mListView.setAdapter(newsAdapter);
	}

	@Override
	public void onCancelFromThread(String msg, int taskId) {
		// TODO Auto-generated method stub

	}
}
