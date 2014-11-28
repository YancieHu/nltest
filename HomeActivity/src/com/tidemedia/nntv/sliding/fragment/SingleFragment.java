package com.tidemedia.nntv.sliding.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.adapter.ColumnSecondAdapter;
import com.tidemedia.nntv.common.APIContants;
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

public class SingleFragment extends BaseFragment {
	private TextView whole, single;
	private LinearLayout processBar;
	private ViewPager viewPager;
	private ArrayList<View> viewPagerList;
	private CustomListView wholeList, singleList;
	private List<SubColumn> wList = null;
	private List<SubColumn> sList = null;
	private ColumnSecondAdapter wSecondAdapter;
	private ColumnSecondAdapter sSecondAdapter;

	private Column mColumn;
	private boolean wIsRefresh = true;
	private boolean sIsRefresh = true;
	private int wpage = 1;
	private int spage = 1;

	public SingleFragment(Column pColumn) {
		this.mColumn = pColumn;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.double_sub_column_layout, null);
		whole = (TextView) view.findViewById(R.id.whole);
		whole.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				whole.setBackgroundResource(R.drawable.column_tab_bg);
				whole.setTextColor(Color.RED);
				single.setBackgroundColor(Color.WHITE);
				single.setTextColor(Color.BLACK);
				viewPager.setCurrentItem(0);
			}
		});
		
		single = (TextView) view.findViewById(R.id.single);
		single.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ListUtil.isEmpty(sList)){
					processBar.setVisibility(View.VISIBLE);
				}
				single.setBackgroundResource(R.drawable.column_tab_bg);
				single.setTextColor(Color.RED);
				whole.setBackgroundColor(Color.WHITE);
				whole.setTextColor(Color.BLACK);
				viewPager.setCurrentItem(1);
			}
		});
		processBar = (LinearLayout) view.findViewById(R.id.processBar);
		viewPager = (ViewPager) view.findViewById(R.id.cViewPager);
		
		viewPagerList = new ArrayList<View>();
		wholeList = new CustomListView(getActivity());
		wholeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				SubColumn subColumn = (SubColumn)wSecondAdapter.getItem(position - 1);
				Intent intent = new Intent(getActivity(), NewsActivity.class);
				intent.putExtra("toFlag", "column");
				intent.putExtra("newsId", subColumn.getId());
				startActivity(intent);
			}
			
		});
		wholeList.setFocusable(true);
		wholeList.setDivider(getActivity().getResources().getDrawable(R.drawable.divider_line));
		wholeList.setCacheColorHint(getResources().getColor(R.color.white));
		wholeList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				wIsRefresh = true;
				wpage = 1;
				getWholeData(wpage);
			}
		});
		wholeList.setOnLoadListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				wIsRefresh = false;
				getWholeData(wpage);
			}
		});
		
		
		singleList = new CustomListView(getActivity());
		singleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				SubColumn subColumn = (SubColumn)sSecondAdapter.getItem(position - 1);
				Intent intent = new Intent(getActivity(), NewsActivity.class);
				intent.putExtra("toFlag", "column");
				intent.putExtra("newsId", subColumn.getId());
				startActivity(intent);
			}
		});
		singleList.setFocusable(true);
		singleList.setDivider(getActivity().getResources().getDrawable(R.drawable.divider_line));
		singleList.setCacheColorHint(getResources().getColor(R.color.white));
		singleList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				sIsRefresh = true;
				spage = 1;
				getSingleData(spage);
			}
		});
		singleList.setOnLoadListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				sIsRefresh = false;
				getSingleData(spage);
			}
		});
		viewPagerList.add(wholeList);
		viewPagerList.add(singleList);
		
		viewPager.setAdapter(pageAdapter);
		viewPager.setOnPageChangeListener(onpageChangeListener);
		getWholeData(wpage);
		return view;
	}
	
	private void getWholeData(int page){
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("cat_id", mColumn.getId());
		postParameter.put("page", String.valueOf(page));
		postParameter.put("pagesize", "10");
		ThreadManager.exeTask(this, APIContants.GET_WHOLE, postParameter, Constants.COLUMN_WHOLE);
	}
	
	private void getSingleData(int page){
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("cat_id", mColumn.getId());
		postParameter.put("page", String.valueOf(page));
		postParameter.put("pagesize", "10");
		ThreadManager.exeTask(this, APIContants.GET_SINGLE, postParameter, Constants.COLUMN_SINGLE);
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		
		if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
			return;
		}
		SubColumnResponseModel subColumnResponseModel = StringUtil.fromJson(resultData, SubColumnResponseModel.class);
		if(subColumnResponseModel.getStatus() == 1){
			if(taskId == APIContants.GET_WHOLE){
				wholeList.onRefreshComplete();
				wholeList.onLoadMoreComplete();
				if(wIsRefresh){
					wList = subColumnResponseModel.getResult().getList();
					wSecondAdapter = new ColumnSecondAdapter(getActivity(), wList, wholeList);
					wholeList.setAdapter(wSecondAdapter);
					processBar.setVisibility(View.GONE);
				}else{
					List<SubColumn> list = subColumnResponseModel.getResult().getList();
					if(ListUtil.isNotEmpty(list)){
						wSecondAdapter.notifyDataSetChanged(list);
					}
				}
				wpage++;
			}else{
				singleList.onRefreshComplete();
				singleList.onLoadMoreComplete();
				if(sIsRefresh){
					sList = subColumnResponseModel.getResult().getList();
					sSecondAdapter = new ColumnSecondAdapter(getActivity(), sList, singleList);
					singleList.setAdapter(sSecondAdapter);
					processBar.setVisibility(View.GONE);
				}else{
					List<SubColumn> list = subColumnResponseModel.getResult().getList();
					if(ListUtil.isNotEmpty(list)){
						sSecondAdapter.notifyDataSetChanged(list);
					}
				}
				spage++;
			}
		}else{
			showToast(subColumnResponseModel.getMessage());
		}
	}
	
	private PagerAdapter pageAdapter = new PagerAdapter() {

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewPagerList.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(viewPagerList.get(position));
			return viewPagerList.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(viewPagerList.get(position));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnPageChangeListener onpageChangeListener = new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
			//pw.setPoint(arg0);			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			if(0 == arg0){
				whole.setBackgroundResource(R.drawable.column_tab_bg);
				whole.setTextColor(Color.RED);
				single.setBackgroundColor(Color.WHITE);
				single.setTextColor(Color.BLACK);
			}else if(1 == arg0){
				single.setBackgroundResource(R.drawable.column_tab_bg);
				single.setTextColor(Color.RED);
				whole.setBackgroundColor(Color.WHITE);
				whole.setTextColor(Color.BLACK);
				if(null == sList){
					getSingleData(spage);
				}
			}
			
		}
		
	};
}
