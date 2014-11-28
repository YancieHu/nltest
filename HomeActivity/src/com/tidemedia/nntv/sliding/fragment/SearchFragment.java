package com.tidemedia.nntv.sliding.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.DiscloseDetailActivity;
import com.tidemedia.nntv.activity.ImageDetailActivity;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.adapter.SearchListAdapter;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.SearchResponseModel;
import com.tidemedia.nntv.model.SearchResponseModel.SearchResultItem;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.ViewUtils;

public class SearchFragment extends BaseFragment implements OnClickListener, OnItemClickListener {
	private static final String TAG = SearchFragment.class.getSimpleName();
	
	private Activity context;
	private EditText searchInput;
	private ImageView searchBtn;
	private PullToRefreshListView searchResultListView;
	private SearchListAdapter adapter;
	private List<SearchResultItem> searchResultList = new ArrayList<SearchResultItem>();
	private TextView noResult;
	private View loadingBar;
	private OnItemClickListener listener;
	private int taskId;
	private int pageIndex;
	private int pageSize = 10;
	private String searchKey;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_layout, container, false);
		searchInput = (EditText) view.findViewById(R.id.searchInput);
		searchBtn = (ImageView) view.findViewById(R.id.searchBtn);
		searchResultListView = (PullToRefreshListView) view.findViewById(R.id.searchResultListView);
		searchResultListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex++;
				String url = getUrl();
				Log.i(TAG, "search url : " + url);
				ThreadManager.exeTask(SearchFragment.this, taskId, null, url);
			}
		});
		adapter = new SearchListAdapter(context, searchResultList);
		searchResultListView.setAdapter(adapter);
		noResult = (TextView) view.findViewById(R.id.noResult);
		loadingBar = view.findViewById(R.id.loadingBar);
		searchBtn.setOnClickListener(this);
		searchResultListView.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchBtn:
			search();
			break;
		}
	}
	
	private void search() {
		String searchKey = searchInput.getText().toString().trim();
		if (searchKey.length() == 0) {
			Toast.makeText(context, context.getString(R.string.prompt_search_input_is_none), Toast.LENGTH_LONG).show();
			return;
		}
		
		this.searchKey = searchKey;
		searchResultList.clear();
		searchResultListView.setVisibility(View.GONE);
		pageIndex = 1;
		String url = getUrl();
		Log.i(TAG, "search url : " + url);
		ThreadManager.exeTask(this, ++taskId, null, url);
		loadingBar.setVisibility(View.VISIBLE);
		searchResultListView.setVisibility(View.GONE);
		noResult.setVisibility(View.GONE);
		ViewUtils.collapseSoftInputMethod(context, searchInput);
	}
	
	private String getUrl() {
		return Constants.GET_SEARCH_RESULT + "?keyword=" + searchKey + "&page=" + pageIndex + "&pagesize=" + pageSize;
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		
		if (taskId != this.taskId) {
			return;
		}
		Log.i(TAG, "search result : " + resultData);
		
		loadingBar.setVisibility(View.GONE);
		if (!StringUtil.isEmpty(resultData)) {
			SearchResponseModel searchResponse = StringUtil.fromJson(resultData, SearchResponseModel.class);
			if (searchResponse != null && searchResponse.result != null) {
				if (searchResponse.result.size() == 0) {
					showToast("没有更多数据了");
				} else {
					searchResultList.addAll(searchResponse.result);
					adapter.notifyDataSetChanged();
					searchResultListView.setVisibility(View.VISIBLE);
					if (searchResultListView.isRefreshing()) {
						searchResultListView.onRefreshComplete();
					}
				}
			} else {
				showNoResultPrompt();
			}
		} else {
			showNoResultPrompt();
		}
	}
	
	private void showNoResultPrompt() {
		searchResultListView.setVisibility(View.GONE);
		loadingBar.setVisibility(View.GONE);
		noResult.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SearchResultItem item = searchResultList.get(position - 1);
		int type = item.type;
		switch (type) {
		case 1:
			//新闻
			Intent intent = new Intent(getActivity(), NewsActivity.class);
			intent.putExtra("newsId", item.id);
			intent.putExtra("toFlag", "news");
			startActivity(intent);
			break;
		case 2:
			//图集	
			Intent imageIntent = new Intent(getActivity(), ImageDetailActivity.class);
			imageIntent.putExtra("id", item.id);
			startActivity(imageIntent);
			break;
		case 3:
			//整档
			Intent wholeIntent = new Intent(getActivity(), NewsActivity.class);
			wholeIntent.putExtra("toFlag", "column");
			wholeIntent.putExtra("newsId", item.id);
			startActivity(wholeIntent);
			break;
		case 4:
			//单条
			Intent singleIntent = new Intent(getActivity(), NewsActivity.class);
			singleIntent.putExtra("toFlag", "column");
			singleIntent.putExtra("newsId", item.id);
			startActivity(singleIntent);
			break;
		case 5:
			//爆料
			Intent discloseInteng = new Intent(getActivity(), DiscloseDetailActivity.class);
			discloseInteng.putExtra("id", item.id);
			startActivity(discloseInteng);
			break;
		default:
			break;
		}
	}
	
}
