package com.tidemedia.nntv.sliding.fragment;

import java.util.HashMap;
import java.util.List;

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
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.adapter.NewsAdapter;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.model.NewsResponseModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.LogUtil;
import com.tidemedia.nntv.util.StringUtil;

public class FoodFragment extends BaseFragment{;

	private PullToRefreshListView mListView;
	private String mCid;
	private Context mContext;
	private int page = 1;
	private NewsAdapter newsAdapter;
	private LinearLayout loadingView;
	private boolean isRefresh = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mContext = this.getActivity();
		RelativeLayout view  = (RelativeLayout)inflater.inflate(R.layout.news_list_layout, null);
		loadingView = (LinearLayout)view.findViewById(R.id.loadingView);
		mListView = (PullToRefreshListView)view.findViewById(R.id.listView);
		mListView.setVisibility(View.INVISIBLE);
		mListView.getRefreshableView().setDivider(getActivity().getResources().getDrawable(R.drawable.divider_line));
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
		initView();
		page = 1;
		getRefreshData();
		return view;
	}
	
	private void getRefreshData(){
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("page", String.valueOf(page));
		postParameter.put("cat_id", "15563");
		LogUtil.i("page", String.valueOf(page));
		ThreadManager.exeTask(this, APIContants.FOOD, postParameter, Constants.FOOD);
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		loadingView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		mListView.onRefreshComplete();
		if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
			return;
		}
		
		NewsResponseModel newsResponseModel = StringUtil.fromJson(resultData, NewsResponseModel.class);
		if(newsResponseModel.getStatus() == 1){
			if(isRefresh){
				//下拉刷新
				newsAdapter = new NewsAdapter(mContext, newsResponseModel, mListView);
				newsAdapter.setFrom("food");
				mListView.setAdapter(newsAdapter);
			}else{
				//加载更多
				newsAdapter.notifyDataSetChanged(newsResponseModel.getResult().getList());
			}
			page ++;
		}
	}

	@Override
	public void onCancelFromThread(String msg, int taskId) {
		// TODO Auto-generated method stub
		
	}
	
	private void initView(){

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				List<NewsModel> list = newsAdapter.getList();
				NewsModel newsModel = list.get(position - 2);
				if(null != newsModel){
					Intent intent = new Intent(mContext, NewsActivity.class);
					intent.putExtra("newsId", newsModel.getId());
					intent.putExtra("toFlag", "food");
					startActivity(intent);
				}
			}
		});
	}
}
