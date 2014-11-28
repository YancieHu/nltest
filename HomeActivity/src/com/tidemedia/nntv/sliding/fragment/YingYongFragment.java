package com.tidemedia.nntv.sliding.fragment;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.slidingmenu.lib.SlidingMenu;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.GongjiaoActivity;
import com.tidemedia.nntv.activity.JiayouzhanActivity;
import com.tidemedia.nntv.activity.LukuangActivity;
import com.tidemedia.nntv.activity.WeatherActivity;
import com.tidemedia.nntv.model.VoteListResponseModel;
import com.tidemedia.nntv.util.StringUtil;

public class YingYongFragment extends BaseFragment {

	private View viewPanel;
	private SlidingMenu mSlidingMenu;
	private ImageView weatherImageView, lukuangImageView,busImageView,jiayouzhanImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewPanel = inflater.inflate(R.layout.yingyong_fragment, container,
				false);
		weatherImageView = (ImageView) viewPanel
				.findViewById(R.id.yingyong_weather);
		weatherImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), WeatherActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("column", list.get(position - 1));
				// intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		lukuangImageView = (ImageView) viewPanel
				.findViewById(R.id.yingyong_lukuang);
		lukuangImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LukuangActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("column", list.get(position - 1));
				// intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		busImageView = (ImageView) viewPanel
				.findViewById(R.id.yingyong_bus);
		busImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), GongjiaoActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("column", list.get(position - 1));
				// intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		jiayouzhanImageView = (ImageView) viewPanel
				.findViewById(R.id.yingyong_jiayouzhan);
		jiayouzhanImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						JiayouzhanActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable("column", list.get(position - 1));
				// intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		return viewPanel;
	}

	public YingYongFragment(SlidingMenu pSlidingMenu) {
		this.mSlidingMenu = pSlidingMenu;
	}

	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			switch (position) {
			case 0:
				mSlidingMenu
						.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				break;
			default:
				mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				break;
			}
		}

	};

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}

		if (!StringUtil.isEmpty(resultData)) {
			VoteListResponseModel voteResponse = StringUtil.fromJson(
					resultData, VoteListResponseModel.class);
			if (voteResponse != null && voteResponse.result != null) {
				viewPanel.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		File dir = getActivity().getCacheDir();
		File[] cacheFiles = dir.listFiles();
		for (File file : cacheFiles) {
			if (file.isFile()) {
				file.delete();
			}
		}
	}
}
