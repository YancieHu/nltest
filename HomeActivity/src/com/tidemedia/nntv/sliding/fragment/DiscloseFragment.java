package com.tidemedia.nntv.sliding.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.DiscloseDetailActivity;
import com.tidemedia.nntv.adapter.DisposeListAdapter;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.DiscloseListResponseModel;
import com.tidemedia.nntv.model.DiscloseListResponseModel.DiscloseEntity;
import com.tidemedia.nntv.net.ThreadCallBack;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.StringUtil;

public class DiscloseFragment extends BaseFragment implements ThreadCallBack, OnItemClickListener {
	private static final String TAG = "DiscloseFragment";
	private List<DiscloseEntity> discloseList = new ArrayList<DiscloseEntity>();
	private DisposeListAdapter adapter;
	private PullToRefreshListView discloseListView;
	private int pageIndex = 1;
	private int pageSize = 20;
	
	private void getDiscloseList() {
		String url = Constants.GET_DISCLOSE_LIST + "?page=" + pageIndex + "&pagesize=" + pageSize;
		Log.i(TAG, "url : " + url);
		ThreadManager.exeTask(this, APIContants.GET_DISCLOSE_LIST, null, url);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new DisposeListAdapter(getActivity(), discloseList);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getDiscloseList();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.disclose_list, container, false);
		discloseListView = (PullToRefreshListView) view.findViewById(R.id.discloseListView);
		discloseListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex++;
				getDiscloseList();
			}
		});
		ListView lv = discloseListView.getRefreshableView();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		return view;
	}
	
	public interface OnListItemClickListener {
		public void onListItemClick(ListView l, View v, int position, long id);
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		Log.i(TAG, "result : " + resultData);
		if (!StringUtil.isEmpty(resultData)) {
			DiscloseListResponseModel discloseResponse = StringUtil.fromJson(resultData, DiscloseListResponseModel.class);
			if (discloseResponse != null && discloseResponse.result != null) {
				discloseList.addAll(discloseResponse.result);
				adapter.notifyDataSetChanged();
				if (discloseListView.isRefreshing()) {
					discloseListView.onRefreshComplete();
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DiscloseEntity entity = (DiscloseEntity) parent.getAdapter().getItem(position);
		Intent intent = new Intent(getActivity(), DiscloseDetailActivity.class);
		intent.putExtra("id", entity.id);
		startActivity(intent);
	}
}
