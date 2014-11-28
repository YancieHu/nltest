package com.tidemedia.nntv.sliding.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.slidingmenu.lib.SlidingMenu;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.adapter.HomeNewsAdapter;
import com.tidemedia.nntv.adapter.NewsGalleryPagerAdapter;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.HomeResponseModel;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.MainActivity;
import com.tidemedia.nntv.util.DimenUtil;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.view.FullHeightGridView;
import com.viewpagerindicator.CirclePageIndicator;

public class HomeFragment extends BaseFragment implements OnItemClickListener,
		OnPageChangeListener {
	private static final String TAG = HomeFragment.class.getSimpleName();
	private static final int AUTO_SWITCH_INTERVAL = 3000;
	private Context context;
	private View layout;
	private PullToRefreshScrollView pullToRefreshView;
	private ViewPager newsGallery;
	private NewsGalleryPagerAdapter newsGalleryAdapter;
	private CirclePageIndicator indicator;
	private GridView newsGridView;
	private HomeNewsAdapter newsGridAdapter;
	private SlidingMenu slidingMenu;
	private boolean skipAutoSwitch;
	private Handler handler = new Handler();
	private Runnable newsGalleryAutoSwitch = new Runnable() {

		@Override
		public void run() {
			if (!skipAutoSwitch) {
				int currentItem = newsGallery.getCurrentItem();
				int count = newsGalleryAdapter.getCount();
				newsGallery.setCurrentItem((currentItem + 1) % count);
			}
			handler.postDelayed(this, AUTO_SWITCH_INTERVAL);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		this.slidingMenu = ((MainActivity) context).getSlidingMenu();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		layout = inflater.inflate(R.layout.home_layout, container, false);
		layout.setVisibility(View.INVISIBLE);
		pullToRefreshView = (PullToRefreshScrollView) layout
				.findViewById(R.id.pullToRefreshView);
		pullToRefreshView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity()
								.getApplicationContext(), System
								.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						requestHome();
					}
				});
		newsGallery = (ViewPager) layout.findViewById(R.id.newsGallery);
		indicator = (CirclePageIndicator) layout.findViewById(R.id.indicator);
		indicator.setVisibility(View.GONE);
		newsGridView = (FullHeightGridView) layout
				.findViewById(R.id.newsGridView);
		requestHome();
		return layout;
	}

	private void requestHome() {
		String url = Constants.GET_HOME_LIST;
		Log.i(TAG, "home list url : " + url);
		ThreadManager.exeTask(this, 0, null, url);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NewsModel news = newsGridAdapter.getItem(position);
		Intent intent = new Intent(context, NewsActivity.class);
		intent.putExtra("newsId", news.getId());
		intent.putExtra("toFlag", "home");
		intent.putExtra("from", "home_list");
		startActivity(intent);
		// Toast.makeText(getActivity(), "id:" + news.getId() + "title:" +
		// news.getTitle() , Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			Log.i(TAG, "fragment is detached, so we ignore this response.");
			return;
		}
		
		Log.i(TAG, "home list result : " + resultData);
		if (!StringUtil.isEmpty(resultData)) {
			HomeResponseModel json = (HomeResponseModel) StringUtil.fromJson(
					resultData, HomeResponseModel.class);
			if (json != null && json.getResult() != null) {
				if (json.getResult().getTop() != null) {
					newsGalleryAdapter = new NewsGalleryPagerAdapter(
							getFragmentManager(), json.getResult().getTop());
					newsGallery.setAdapter(newsGalleryAdapter);
					if (newsGalleryAdapter.getCount() > 1) {
						indicator.setVisibility(View.VISIBLE);
					}
					indicator.setViewPager(newsGallery);
					indicator.setOnPageChangeListener(this);
					newsGallery.setOnTouchListener(new View.OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							int action = event.getAction();
							switch (action) {
							case MotionEvent.ACTION_DOWN:
							case MotionEvent.ACTION_MOVE:
								skipAutoSwitch = true;
								break;
							case MotionEvent.ACTION_CANCEL:
							case MotionEvent.ACTION_UP:
								skipAutoSwitch = false;
								break;
							}
							return false;
						}
					});
					newsGallery.requestFocus();
					// modify by lee 20140830 start
					handler.removeCallbacks(newsGalleryAutoSwitch);
					handler.postDelayed(newsGalleryAutoSwitch,
							AUTO_SWITCH_INTERVAL);
					// modify by lee 20140830 end
				}
				if (json.getResult().getList() != null) {
					List<NewsModel> list = json.getResult().getList();
					newsGridAdapter = new HomeNewsAdapter(context, list);
					newsGridView.setAdapter(newsGridAdapter);
					newsGridView.setOnItemClickListener(this);
					final ViewTreeObserver vb = newsGridView
							.getViewTreeObserver();
					vb.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							handler.postDelayed(new Runnable() {
								public void run() {
									ScrollView sv = pullToRefreshView.getRefreshableView();
									sv.smoothScrollTo(0, 0);
								}
							}, 200);
							vb.removeGlobalOnLayoutListener(this);
						}
					});
					newsGallery.requestFocus();
				}
				layout.setVisibility(View.VISIBLE);
				if (pullToRefreshView.isRefreshing()) {
					pullToRefreshView.onRefreshComplete();
				}
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			break;
		default:
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			break;
		}
	}
}
