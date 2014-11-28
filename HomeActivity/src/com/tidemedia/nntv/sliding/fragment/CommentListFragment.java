package com.tidemedia.nntv.sliding.fragment;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;

import com.tidemedia.nntv.adapter.CommentAdapter;
import com.tidemedia.nntv.model.CommentListResponseModel.Comment;

public class CommentListFragment extends ListFragment {
	private List<Comment> list;
	private CommentAdapter adapter;
	
	public CommentListFragment(List<Comment> list) {
		this.list = list;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new CommentAdapter(getActivity(), list);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(adapter);
	}
}
