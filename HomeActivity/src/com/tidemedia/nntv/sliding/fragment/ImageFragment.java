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
import com.tidemedia.nntv.activity.ImageDetailActivity;
import com.tidemedia.nntv.adapter.ImgeAdapter;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.ImgResonseModel;
import com.tidemedia.nntv.model.ImgResonseModel.ImgModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.view.CustomListView.OnLoadMoreListener;

public class ImageFragment extends BaseFragment {
	private PullToRefreshListView mListView;
	private String mIid;
	private Context mContext;
	private int page = 1;
	private ImgeAdapter imgAdapter;
	private boolean isRefresh = true;
	private LinearLayout loadingView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	public ImageFragment(String pIid) {
        this.mIid = pIid;
    }
	
	public String getText(){
        return mIid;
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = this.getActivity();
		RelativeLayout baseLayout = (RelativeLayout)inflater.inflate(R.layout.image_list_layout, null);
		mListView = (PullToRefreshListView)baseLayout.findViewById(R.id.listView);
		loadingView = (LinearLayout)baseLayout.findViewById(R.id.loadingView);
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
		return baseLayout;
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		
		loadingView.setVisibility(View.GONE);
		mListView.onRefreshComplete();
		if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ImgResonseModel newsResponseModel = StringUtil.fromJson(resultData, ImgResonseModel.class);
		if(newsResponseModel.getStatus() == 1){
			if(isRefresh){
				//下拉刷新
				imgAdapter = new ImgeAdapter(mContext, newsResponseModel.getResult(), mListView);
				mListView.setAdapter(imgAdapter);
				mListView.setVisibility(View.VISIBLE);
			}else{
				//加载更多
				if(null != newsResponseModel.getResult()){
					imgAdapter.notifyDataSetChanged(newsResponseModel.getResult());
				}
			}
			page ++;
		}
	}
	
	private void getRefreshData(){
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("cat_id", mIid);
		postParameter.put("page", String.valueOf(page));
		ThreadManager.exeTask(this, APIContants.GET_IMG_LIST, postParameter, Constants.GET_IMG_LIST);
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
				// 此处传回来的position和mAdapter.getItemId()获取的一致;
				List<ImgModel> list = imgAdapter.getList();
				ImgModel imgModel = list.get(position - 1);
				if(null != imgModel){
					Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
					intent.putExtra("id", imgModel.getId());
					intent.putExtra("imageItem", imgModel);
					startActivity(intent);
				}
			}
		});
	}
}
