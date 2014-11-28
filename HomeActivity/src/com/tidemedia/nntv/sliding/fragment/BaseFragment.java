package com.tidemedia.nntv.sliding.fragment;

import android.app.Fragment;
import android.widget.Toast;

import com.tidemedia.nntv.net.ThreadCallBack;

public class BaseFragment extends Fragment implements ThreadCallBack{

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancelFromThread(String msg, int taskId) {
		// TODO Auto-generated method stub
		
	}
	
	protected void showToast(String msg){
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

}
