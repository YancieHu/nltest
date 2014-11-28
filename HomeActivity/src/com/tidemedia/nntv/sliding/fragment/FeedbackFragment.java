package com.tidemedia.nntv.sliding.fragment;

import java.util.HashMap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.BaseResponseModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.ValidateUtil;

public class FeedbackFragment extends BaseFragment {
	private EditText content, contract;
	private Button submit;
	private String sContent;
	private String sContract;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.feedback_layout, null);
		content = (EditText)view.findViewById(R.id.content);
		contract = (EditText)view.findViewById(R.id.contract);
		submit = (Button)view.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!validate()){
					return;
				}
				toFeedBack();
			}
		});
		return view;
	}
	
	private void toFeedBack(){
		HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("yijian", sContent);
		postParameter.put("lxfs", sContract);
		ThreadManager.exeTask(this, APIContants.FEED_BACK, postParameter, Constants.FEED_BACK);
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "提交失败，请稍候尝试", Toast.LENGTH_SHORT).show();
			return;
		}
		BaseResponseModel base = StringUtil.fromJson(resultData, BaseResponseModel.class);
		if(1 == base.getStatus()){
			Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT).show();
			getActivity().finish();
		}else{
			Toast.makeText(getActivity(), "提交失败，请稍候尝试", Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean validate(){
		sContent = content.getText().toString().trim();
		sContract = contract.getText().toString().trim();
		if(StringUtil.isEmpty(sContent)){
			Toast.makeText(getActivity(), "反馈内容不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(!StringUtil.isEmpty(sContract)){
			if(ValidateUtil.isInteger(sContract)){
				if(!ValidateUtil.isMobile(sContract)){
					Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
					return false;
				}
			}else{
				if(!ValidateUtil.isEmail(sContract)){
					Toast.makeText(getActivity(), "请输入正确的邮箱地址", Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		}else{
			Toast.makeText(getActivity(), "联系方式不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
}
