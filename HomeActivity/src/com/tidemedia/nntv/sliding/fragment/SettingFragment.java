package com.tidemedia.nntv.sliding.fragment;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.AboutActivity;
import com.tidemedia.nntv.activity.FeedbackActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

public class SettingFragment extends BaseFragment implements OnClickListener {
	
	private TextView feedback, about;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting_layout, null);
		feedback = (TextView)view.findViewById(R.id.feedback);
		about = (TextView)view.findViewById(R.id.about);
		feedback.setOnClickListener(this);
		about.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feedback:
			startActivity(new Intent(getActivity(), FeedbackActivity.class));
			break;
		case R.id.about:
			startActivity(new Intent(getActivity(), AboutActivity.class));
		 	 break;
		default:
			break;
		}
	}
}
