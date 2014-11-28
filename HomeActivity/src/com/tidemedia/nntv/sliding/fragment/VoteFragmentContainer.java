package com.tidemedia.nntv.sliding.fragment;

import java.io.File;
import java.util.List;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slidingmenu.lib.SlidingMenu;
import com.tidemedia.nntv.R;
import com.tidemedia.nntv.adapter.VotePagerAdapter;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.common.Preferences;
import com.tidemedia.nntv.model.VoteListResponseModel;
import com.tidemedia.nntv.model.VoteListResponseModel.VoteEntity;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.PreferencesUtil;
import com.tidemedia.nntv.util.StringUtil;
import com.viewpagerindicator.CirclePageIndicator;

public class VoteFragmentContainer extends BaseFragment {
	private List<VoteEntity> list;
	private VotePagerAdapter votePagerAdapter;
	
	private View viewPanel;
	private ViewPager votePager;
	private CirclePageIndicator indicator;
	private SlidingMenu mSlidingMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


		@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewPanel = inflater.inflate(R.layout.vote_fragment_container, container, false);
		votePager = (ViewPager) viewPanel.findViewById(R.id.votePager);
		indicator = (CirclePageIndicator) viewPanel.findViewById(R.id.votePagerIndicator);
		ThreadManager.exeTask(this, APIContants.GET_VOTE_LIST, null, Constants.GET_VOTE_LIST);
		if (list == null || list.size() == 0) {
			viewPanel.setVisibility(View.INVISIBLE);
		} else {
			viewPanel.setVisibility(View.VISIBLE);
		}
		return viewPanel;
	}
	
	public VoteFragmentContainer(SlidingMenu pSlidingMenu){
		this.mSlidingMenu = pSlidingMenu;
	}
	
	ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
        	PreferencesUtil.savePreference(VoteFragmentContainer.this.getActivity(), Preferences.VOTE_TYPE, Preferences.VOTE_KEY, String.valueOf(list.get(position).id));
            switch (position) {
                case 0:
                	mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
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
			VoteListResponseModel voteResponse = StringUtil.fromJson(resultData, VoteListResponseModel.class);
			if (voteResponse != null && voteResponse.result != null) {
				viewPanel.setVisibility(View.VISIBLE);
				list = voteResponse.result;
				votePagerAdapter = new VotePagerAdapter(getFragmentManager(), list);
				votePager.setAdapter(votePagerAdapter);
				indicator.setViewPager(votePager);
				indicator.setOnPageChangeListener(onPageChangeListener);
				votePagerAdapter.notifyDataSetChanged();
				//PreferencesUtil.savePreference(VoteFragmentContainer.this.getActivity(), Preferences.VOTE_TYPE, Preferences.VOTE_KEY, String.valueOf(list.get(0).id));
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
