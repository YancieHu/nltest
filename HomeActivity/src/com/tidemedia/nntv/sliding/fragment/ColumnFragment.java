package com.tidemedia.nntv.sliding.fragment;

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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.SubColumnActivity;
import com.tidemedia.nntv.adapter.ColumnFirstAdapter;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.ColumnResponseModel;
import com.tidemedia.nntv.model.ColumnResponseModel.Column;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.StringUtil;

public class ColumnFragment extends BaseFragment {
	
	private LinearLayout processBar;
	private PullToRefreshListView mListView;
	private Context mContext;
	private ColumnFirstAdapter columnFirstAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContext = this.getActivity();
		RelativeLayout baseLayout = (RelativeLayout)inflater.inflate(R.layout.colum_list_layout, null);
		processBar = (LinearLayout)baseLayout.findViewById(R.id.processBar);
		mListView = (PullToRefreshListView)baseLayout.findViewById(R.id.listView);
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getRefreshData();
			}
		});
		getRefreshData();
		return baseLayout;
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
		
		ColumnResponseModel columnResponseModel = StringUtil.fromJson(resultData, ColumnResponseModel.class);
		if(columnResponseModel.getStatus() == 1){
			final List<Column> list = columnResponseModel.getResult();
			columnFirstAdapter = new ColumnFirstAdapter(mContext, list, mListView);
			mListView.setAdapter(columnFirstAdapter);
			processBar.setVisibility(View.GONE);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					Intent intent = new Intent(getActivity(), SubColumnActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("column", list.get(position - 1));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
		}
	}
	
	private void getRefreshData(){
		ThreadManager.exeTask(this, APIContants.GET_COLUMN_LIST, null, Constants.GET_COLUMN_LIST);
	}
}
