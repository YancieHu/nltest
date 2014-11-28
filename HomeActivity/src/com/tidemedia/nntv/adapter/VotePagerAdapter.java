package com.tidemedia.nntv.adapter;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.tidemedia.nntv.model.VoteListResponseModel.VoteEntity;
import com.tidemedia.nntv.sliding.fragment.VoteFragment;

public class VotePagerAdapter extends PagerAdapter {
	private FragmentManager mFragmentManager;
	private FragmentTransaction mTransaction = null;
	private List<VoteEntity> list;

	public VotePagerAdapter(FragmentManager fm, List<VoteEntity> list) {
		mFragmentManager = fm;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mTransaction == null) {
			mTransaction = mFragmentManager.beginTransaction();
		}
		String name = getTag(position);
		Fragment fragment = mFragmentManager.findFragmentByTag(name);
		if (fragment != null) {
			mTransaction.attach(fragment);
		} else {
			fragment = getItem(position);
			mTransaction.add(container.getId(), fragment, getTag(position));
		}
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mTransaction == null) {
			mTransaction = mFragmentManager.beginTransaction();
		}
		mTransaction.detach((Fragment) object);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (mTransaction != null) {
			mTransaction.commitAllowingStateLoss();
			mTransaction = null;
			mFragmentManager.executePendingTransactions();
		}
	}

	public Fragment getItem(int position) {
		VoteEntity entity = list.get(position);
		Fragment f = new VoteFragment();
		Bundle args = new Bundle();
		args.putSerializable("VoteEntity", entity);
		f.setArguments(args);
		return f;
	}

	public long getItemId(int position) {
		return position;
	}
	
	@Override  
	public int getItemPosition(Object object) {  
	    return POSITION_NONE;  
	} 

	protected String getTag(int position) {
		return list.get(position).title;
	}

}
