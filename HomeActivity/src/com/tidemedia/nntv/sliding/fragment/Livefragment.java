package com.tidemedia.nntv.sliding.fragment;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.AnimationStyle;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.PlayerActivity;
import com.tidemedia.nntv.adapter.LiveAdapter;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.LiveResponseModel;
import com.tidemedia.nntv.model.LiveResponseModel.Live;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.ListUtil;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.view.CustomListView.OnLoadMoreListener;

public class Livefragment extends BaseFragment {
	private PullToRefreshListView mListView;
	private Context mContext;
	private int page = 1;
	private LiveAdapter liveAdapter;
	private boolean isRefresh = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mContext = this.getActivity();
		mListView = new PullToRefreshListView(mContext, Mode.BOTH, AnimationStyle.FLIP);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				Mode mode = mListView.getCurrentMode();
				switch (mode) {
				case PULL_FROM_START:
					isRefresh = true;
					page = 1;
					getRefreshData();
					break;
				case PULL_FROM_END:
					isRefresh = false;
					getRefreshData();
					break;
				}
			}
		});
		mListView.getRefreshableView().setDivider(getActivity().getResources().getDrawable(R.drawable.divider_line));
		initView();
		getRefreshData();
		return mListView;
	}
	
	private void getRefreshData(){
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("pagesize", "10");
		postParameter.put("page", String.valueOf(page));
		ThreadManager.exeTask(this, APIContants.GET_LIVE_LIST, postParameter, Constants.GET_LIVE_LIST);
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		
		mListView.onRefreshComplete();
		if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
			return;
		}
		
		LiveResponseModel liveResponseModel = StringUtil.fromJson(resultData, LiveResponseModel.class);
		if(liveResponseModel.getStatus() == 1){
			if(isRefresh){
				//下拉刷新
				liveAdapter = new LiveAdapter(mContext, liveResponseModel.getResult(), mListView);
				mListView.setAdapter(liveAdapter);
			}else{
				//加载更多
				if(ListUtil.isNotEmpty(liveResponseModel.getResult())){
					liveAdapter.notifyDataSetChanged(liveResponseModel.getResult());
				}else{
					Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
				}
			}
			page ++;
		}/*else{
			Toast.makeText(getActivity(), liveResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
		}*/
	}

	@Override
	public void onCancelFromThread(String msg, int taskId) {
		// TODO Auto-generated method stub
		
	}
	
	private void initView(){
//		mListView.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				// TODO 下拉刷新
//				isRefresh = true;
//				page = 1;
//				getRefreshData();
//			}
//		});
//
//		mListView.setOnLoadListener(new OnLoadMoreListener() {
//
//			@Override
//			public void onLoadMore() {
//				// TODO 加载更多
//				isRefresh = false;
//				getRefreshData();
//			}
//		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Live live = liveAdapter.getItem(position - 1);
				if (live != null) {
					Intent intent = new Intent(getActivity(), PlayerActivity.class);
					intent.putExtra("liveUrl", live.getVideo_url());
					// 直播参数
					intent.putExtra("from_live", true);
					if(live.getIsgb().equals("1")){
						intent.putExtra("photo", live.getGb_photo());
					}
					startActivity(intent);
				}
			}
		});
	}
}
