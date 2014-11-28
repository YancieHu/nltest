package com.tidemedia.nntv.sliding.fragment;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.adapter.ColumnSecondAdapter;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.ColumnResponseModel.Column;
import com.tidemedia.nntv.model.SubColumnResponseModel;
import com.tidemedia.nntv.model.SubColumnResponseModel.SubColumn;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.ListUtil;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.view.CustomListView;
import com.tidemedia.nntv.view.CustomListView.OnLoadMoreListener;
import com.tidemedia.nntv.view.CustomListView.OnRefreshListener;

public class NoSingleFragment extends BaseFragment {
	
	private LinearLayout processBar;
	private Column mColumn;
	private CustomListView subList;
	private ColumnSecondAdapter columnSecondAdapter;
	private List<SubColumn> list;
	private int page = 1;
	private boolean isRefresh = true;

	public NoSingleFragment(Column pColumn) {
		this.mColumn = pColumn;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sub_column_layout, null);
		//final ImageView titleImage = (ImageView)view.findViewById(R.id.titleImage);
		//TextView title = (TextView)view.findViewById(R.id.title);
		processBar = (LinearLayout)view.findViewById(R.id.processBar);
		subList = (CustomListView)view.findViewById(R.id.subList);
		subList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				SubColumn subColumn = (SubColumn)columnSecondAdapter.getItem(position - 1);
				Intent intent = new Intent(getActivity(), NewsActivity.class);
				intent.putExtra("toFlag", "column");
				intent.putExtra("newsId", subColumn.getId());
				startActivity(intent);
			}
		});
		/*if(StringUtil.isNotEmpty(mColumn.getImage_url())){
			String url = mColumn.getImage_url();
			Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {
				@Override
				public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
					if(imageDrawable!=null && titleImage!=null){
						titleImage.setImageBitmap(imageDrawable);
					}
				}
			});
			if (bitmap == null) {
				titleImage.setImageResource(R.drawable.default_img);
			} else {
				titleImage.setImageBitmap(bitmap);
			}
		}
		title.setText(mColumn.getTitle());*/
		subList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				isRefresh = true;
				page = 1;
				getRefreshData(page);
			}
		});
		subList.setOnLoadListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				isRefresh = false;
				getRefreshData(page);
			}
		});
		getRefreshData(page);
		return view;
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		
		subList.onRefreshComplete();
		subList.onLoadMoreComplete();
		if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
			return;
		}
		SubColumnResponseModel subColumnResponseModel = StringUtil.fromJson(resultData, SubColumnResponseModel.class);
		if(subColumnResponseModel.getStatus() == 1){
			if(isRefresh){
				list = subColumnResponseModel.getResult().getList();
				columnSecondAdapter = new ColumnSecondAdapter(getActivity(), list, subList);
				subList.setAdapter(columnSecondAdapter);
				processBar.setVisibility(View.GONE);
				subList.setVisibility(View.VISIBLE);
			}else{
				List<SubColumn> list = subColumnResponseModel.getResult().getList();
				if(ListUtil.isNotEmpty(list)){
					columnSecondAdapter.notifyDataSetChanged(list);
				}
			}
			page++;
		}else{
			showToast(subColumnResponseModel.getMessage());
		}
	}
	
	private void getRefreshData(int page){
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("cat_id", mColumn.getId());
		postParameter.put("page", String.valueOf(page));
		postParameter.put("pagesize", "10");
		ThreadManager.exeTask(this, 0, postParameter, Constants.COLUMN_WHOLE);
	}
}
