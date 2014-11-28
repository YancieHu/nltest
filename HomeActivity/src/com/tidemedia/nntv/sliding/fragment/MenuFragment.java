package com.tidemedia.nntv.sliding.fragment;

import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.slidingmenu.lib.SlidingMenu;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.DiscloseActivity;
import com.tidemedia.nntv.model.CategoryResponse.Category;
import com.tidemedia.nntv.sliding.DemoFragmentAdapter;
import com.tidemedia.nntv.sliding.MainActivity;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * menu fragment ，主要是用于显示menu菜单
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @since Mar 12, 2013
 * @version 1.0.0
 */
/**
 * menu fragment ，主要是用于显示menu菜单
 * 
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @since Mar 12, 2013
 * @version 1.0.0
 */
public class MenuFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	public int index = 0;
	public ViewPager mViewPager = null;
	public ViewPager mViewPager2 = null;
	private View voteListPanel;
	private ViewPager votePager;
	private CirclePageIndicator votePagerIndicator;
	public FrameLayout mFrameLayout = null;
	private MainActivity mActivity = null;
	private List<Category> nList;
	private List<Category> pList;
	public Button discloseBtn;
	private Bitmap bitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mActivity = (MainActivity) getActivity();
		mViewPager = (ViewPager) mActivity.findViewById(R.id.viewpager);
		mViewPager2 = (ViewPager) mActivity.findViewById(R.id.viewpager2);
		voteListPanel = mActivity.findViewById(R.id.voteListPanel);
		votePager = (ViewPager) mActivity.findViewById(R.id.votePager);
		votePagerIndicator = (CirclePageIndicator) mActivity
				.findViewById(R.id.votePagerIndicator);
		mFrameLayout = (FrameLayout) mActivity.findViewById(R.id.content);
		// set the preference xml to the content view
		addPreferencesFromResource(R.xml.menu);
		// add listener
		findPreference("home").setOnPreferenceClickListener(this);
		findPreference("news").setOnPreferenceClickListener(this);
//		findPreference("travel").setOnPreferenceClickListener(this);
//		findPreference("food").setOnPreferenceClickListener(this);
		findPreference("images").setOnPreferenceClickListener(this);
		findPreference("column").setOnPreferenceClickListener(this);
		findPreference("broadcastlive").setOnPreferenceClickListener(this);
		findPreference("yingyong").setOnPreferenceClickListener(this);
		findPreference("vote").setOnPreferenceClickListener(this);
		findPreference("disclose").setOnPreferenceClickListener(this);
		findPreference("search").setOnPreferenceClickListener(this);
		findPreference("setting").setOnPreferenceClickListener(this);
		findPreference("account").setOnPreferenceClickListener(this);
		this.getActivity().getWindow()
				.setBackgroundDrawableResource(R.drawable.menu_bg);
		discloseBtn = (Button) getActivity().findViewById(R.id.discloseBtn);
		discloseBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						DiscloseActivity.class);
				startActivity(intent);
			}
		});

		new Thread() {
			public void run() {
				DefaultHttpClient httpclient = new DefaultHttpClient();

				HttpGet httpget = new HttpGet(
						"http://180.141.89.20:2088/img/menu.png");

				try {
					HttpResponse resp = httpclient.execute(httpget);
					// 判断是否正确执行
					if (HttpStatus.SC_OK == resp.getStatusLine()
							.getStatusCode()) {
						// 将返回内容转换为bitmap
						HttpEntity entity = resp.getEntity();
						InputStream in = entity.getContent();
						bitmap = BitmapFactory.decodeStream(in);

						// 向handler发送消息，执行显示图片操作
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
					setTitle(e.getMessage());
				} finally {
					httpclient.getConnectionManager().shutdown();
				}
			};
		}.start();
		/*
		 * handler.post(new Runnable() {
		 * 
		 * @Override public void run() { DefaultHttpClient httpclient = new
		 * DefaultHttpClient();
		 * 
		 * HttpGet httpget = new
		 * HttpGet("http://180.141.89.20:2088/img/menu.png");
		 * 
		 * try { HttpResponse resp = httpclient.execute(httpget); //判断是否正确执行 if
		 * (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
		 * //将返回内容转换为bitmap HttpEntity entity = resp.getEntity(); InputStream in
		 * = entity.getContent(); bitmap = BitmapFactory.decodeStream(in);
		 * 
		 * //向handler发送消息，执行显示图片操作 Message msg = new Message(); msg.what = 1;
		 * handler.sendMessage(msg); }
		 * 
		 * } catch (Exception e) { e.printStackTrace();
		 * setTitle(e.getMessage()); } finally {
		 * httpclient.getConnectionManager().shutdown(); } } });
		 */

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				try {
					MenuFragment.this.getActivity().getWindow()
							.setBackgroundDrawable(new BitmapDrawable(bitmap));
				} catch (Exception e) {
				}

				break;
			}
		}
	};

	public MenuFragment() {

	}

	public MenuFragment(List<Category> nList, List<Category> pList) {
		this.nList = nList;
		this.pList = pList;
	}

	public void setTitle(String title) {
		((MainActivity) getActivity()).setActionTitle(title);
	}

	public boolean onPreferenceClick(Preference preference) {
		String key = preference.getKey();

		if (!"home".equals(key)) {
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionTitle).setVisibility(View.VISIBLE);
		}

		if ("home".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			// if the content view is that we need to show . show directly
			if (index == 0) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			// otherwise , replace the content view via a new Content fragment
			index = 0;
			// setTitle("首页");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionTitle).setVisibility(View.GONE);
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.VISIBLE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			HomeFragment home = new HomeFragment();
			fragmentManager.beginTransaction().replace(R.id.content, home)
					.commit();
		} else if ("news".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			if (index == 1) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}

			index = 1;
			setTitle("新闻");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.GONE);
			mViewPager.setVisibility(View.VISIBLE);
			mViewPager2.setVisibility(View.GONE);
			DemoFragmentAdapter demoFragmentAdapter = new DemoFragmentAdapter(
					mActivity.getFragmentManager(), false, nList);
			mViewPager.setOffscreenPageLimit(5);
			mViewPager.setAdapter(demoFragmentAdapter);
			mViewPager.setOnPageChangeListener(onPageChangeListener);

			ActionBar actionBar = mActivity.getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.removeAllTabs();
			for (Category category : nList) {
				actionBar.addTab(actionBar.newTab()
						.setText(category.getTitle())
						.setTabListener(tabListener));
			}
		} else if ("travel".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			if (index == 2) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}

			index = 2;
			setTitle("旅游");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			TravelFragment travelfragment = (TravelFragment) fragmentManager
					.findFragmentByTag("travel");
			fragmentManager
					.beginTransaction()
					.replace(
							R.id.content,
							travelfragment == null ? new TravelFragment()
									: travelfragment, "travel").commit();

		} else if ("food".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			if (index == 3) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}

			index = 3;
			setTitle("美食");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			FoodFragment foodfragment = (FoodFragment) fragmentManager
					.findFragmentByTag("food");
			fragmentManager
					.beginTransaction()
					.replace(
							R.id.content,
							foodfragment == null ? new FoodFragment()
									: foodfragment, "food").commit();

		} else if ("images".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			if (index == 2) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			index = 2;
			setTitle("图集");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.GONE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.VISIBLE);
			DemoFragmentAdapter demoFragmentAdapter = new DemoFragmentAdapter(
					mActivity.getFragmentManager(), true, pList);
			mViewPager2.setOffscreenPageLimit(5);
			mViewPager2.setAdapter(demoFragmentAdapter);
			mViewPager2.setOnPageChangeListener(onPageChangeListener);

			ActionBar actionBar = mActivity.getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.removeAllTabs();
			for (Category category : pList) {
				actionBar.addTab(actionBar.newTab()
						.setText(category.getTitle())
						.setTabListener(tabListener));
			}
		} else if ("column".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			if (index == 3) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			index = 3;
			setTitle("栏目");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			ColumnFragment columnFragment = (ColumnFragment) fragmentManager
					.findFragmentByTag("COLUMN");
			fragmentManager
					.beginTransaction()
					.replace(
							R.id.content,
							columnFragment == null ? new ColumnFragment()
									: columnFragment, "COLUMN").commit();
		} else if ("broadcastlive".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			if (index == 4) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			index = 4;
			setTitle("直播");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			Livefragment liveFragment = (Livefragment) fragmentManager
					.findFragmentByTag("LIVE");
			fragmentManager
					.beginTransaction()
					.replace(
							R.id.content,
							liveFragment == null ? new Livefragment()
									: liveFragment, "LIVE").commit();
		} else if ("yingyong".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			// if the content view is that we need to show . show directly
			if (index == 5) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			// otherwise , replace the content view via a new Content fragment
			index = 5;
			setTitle("应用");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			YingYongFragment yingyongFragment = new YingYongFragment(
					mActivity.getSlidingMenu());
			fragmentManager.beginTransaction()
					.replace(R.id.content, yingyongFragment, "YINGYONG").commit();
		} else if ("vote".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			// if the content view is that we need to show . show directly
			if (index == 6) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			// otherwise , replace the content view via a new Content fragment
			index = 6;
			setTitle("投票");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			VoteFragmentContainer voteFragment = new VoteFragmentContainer(
					mActivity.getSlidingMenu());
			fragmentManager.beginTransaction()
					.replace(R.id.content, voteFragment, "VOTE").commit();
		} else if ("disclose".equals(key)) {
			discloseBtn.setVisibility(View.VISIBLE);
			// if the content view is that we need to show . show directly
			if (index == 7) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			// otherwise , replace the content view via a new Content fragment
			index = 7;
			setTitle("爆料");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			DiscloseFragment discloseFragment = (DiscloseFragment) fragmentManager
					.findFragmentByTag("DISCLOSE");
			if (discloseFragment == null) {
				discloseFragment = new DiscloseFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.content, discloseFragment, "DISCLOSE")
					.commit();
		} else if ("search".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			// if the content view is that we need to show . show directly
			if (index == 8) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			// otherwise , replace the content view via a new Content fragment
			index = 8;
			setTitle("搜索");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			SearchFragment searchFragment = (SearchFragment) fragmentManager
					.findFragmentByTag("SEARCH");
			if (searchFragment == null) {
				searchFragment = new SearchFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.content, searchFragment, "SEARCH").commit();
		} else if ("setting".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			// if the content view is that we need to show . show directly
			if (index == 9) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			// otherwise , replace the content view via a new Content fragment
			index = 9;
			setTitle("关于/意见");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			SettingFragment settingFragment = (SettingFragment) fragmentManager
					.findFragmentByTag("SETTING");
			fragmentManager
					.beginTransaction()
					.replace(
							R.id.content,
							settingFragment == null ? new SettingFragment()
									: settingFragment, "SETTING").commit();
		} else if ("account".equals(key)) {
			discloseBtn.setVisibility(View.GONE);
			// if the content view is that we need to show . show directly
			if (index == 10) {
				((MainActivity) getActivity()).getSlidingMenu().toggle();
				return true;
			}
			// otherwise , replace the content view via a new Content fragment
			index = 10;
			setTitle("登陆/注册");
			getActivity().invalidateOptionsMenu();
			((MainActivity) getActivity()).getActionBar().getCustomView()
					.findViewById(R.id.actionImage).setVisibility(View.GONE);
			mFrameLayout.setVisibility(View.VISIBLE);
			mViewPager.setVisibility(View.GONE);
			mViewPager2.setVisibility(View.GONE);
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
			FragmentManager fragmentManager = ((MainActivity) getActivity())
					.getFragmentManager();
			AccountFragment accountFragment = (AccountFragment) fragmentManager
					.findFragmentByTag("ACCOUNT");
			fragmentManager
					.beginTransaction()
					.replace(
							R.id.content,
							accountFragment == null ? new AccountFragment()
									: accountFragment, "ACCOUNT").commit();
		}
		// anyway , show the sliding menu
		((MainActivity) getActivity()).getSlidingMenu().toggle();
		return false;
	}

	private SlidingMenu getSlidingMenu() {
		return mActivity.getSlidingMenu();
	}

	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			getActivity().getActionBar().setSelectedNavigationItem(position);
			switch (position) {
			case 0:
				getSlidingMenu().setTouchModeAbove(
						SlidingMenu.TOUCHMODE_FULLSCREEN);
				break;
			default:
				getSlidingMenu()
						.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				break;
			}
		}

	};
	ActionBar.TabListener tabListener = new ActionBar.TabListener() {

		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
			if (mViewPager.getVisibility() == View.VISIBLE
					&& mViewPager.getCurrentItem() != tab.getPosition()) {
				mViewPager.setCurrentItem(tab.getPosition());
			}
			if (mViewPager2.getVisibility() == View.VISIBLE
					&& mViewPager2.getCurrentItem() != tab.getPosition()) {
				mViewPager2.setCurrentItem(tab.getPosition());
			}
		}

		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

		}

		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

		}
	};

	// public void performClick(Preference preference) {
	// if (null == preference) {
	// return;
	// }
	// String key = preference.getKey();
	// if ("toutiao".equals(key)) {
	// // 首页
	// preference.setKey("home");
	// } else if ("xinwen".equals(key)) {
	// // 新闻
	// preference.setKey("news");
	// } else if ("lvyou".equals(key)) {
	// // 旅游
	// preference.setKey("travel");
	// } else if ("meishi".equals(key)) {
	// // 美食
	// preference.setKey("food");
	// } else if ("tuji".equals(key)) {
	// // 图集
	// preference.setKey("images");
	// }
	// onPreferenceClick(preference);
	// }

}
