package com.tidemedia.nntv.sliding.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.tidemedia.nntv.common.Preferences;
import com.tidemedia.nntv.model.LoginRes;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.MainActivity;
import com.tidemedia.nntv.util.PreferencesUtil;
import com.tidemedia.nntv.util.StringUtil;
public class RegFragment extends BaseFragment {
	static final String TAG = RegFragment.class.getSimpleName();
	
	private EditText username, email, password, repassword;
	private String sUsername, sEmail, sPassword, sRepassword;
	private Button regButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.reg_layout, null);
		username = (EditText)view.findViewById(R.id.username);
		email = (EditText)view.findViewById(R.id.email); 
		password = (EditText)view.findViewById(R.id.password); 
		repassword = (EditText)view.findViewById(R.id.repassword);
		regButton = (Button)view.findViewById(R.id.regButton);
		regButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!validate()){
					return;
				}
				toReg();
			}
		});
		return view;
	}
	
	private void toReg(){
		/*HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("username", sUsername);
		postParameter.put("password", sPassword);
		postParameter.put("email", sEmail);*/
		String url = Constants.REG + "&username=" + sUsername + "&password="+ sPassword + "&email=" + sEmail;
		Log.i(TAG, "" + url);
		ThreadManager.exeTask(this, APIContants.REG, null, url);
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		Log.i(TAG, "" + resultData);
		if (isDetached()) {
			return;
		}
		
		if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "提交失败，请稍候尝试", Toast.LENGTH_SHORT).show();
			return;
		}
		resultData = resultData.substring(18, resultData.length() - 2);
    	LoginRes base = StringUtil.fromJson(resultData, LoginRes.class);
		if(1 == base.getStatus()){
			PreferencesUtil.savePreference(getActivity(), Preferences.LOGIN_TYPE, Preferences.LOGIN_KEY, base.getUid());
			PreferencesUtil.savePreference(getActivity(), Preferences.NAME_TYPE, Preferences.NAME_KEY, sUsername);
			showToast("注册成功");
			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
		}else{
			Toast.makeText(getActivity(), base.getMsg(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean validate(){
		sUsername = username.getText().toString().trim();
		sEmail = email.getText().toString().trim(); 
		sPassword = password.getText().toString().trim(); 
		sRepassword = repassword.getText().toString().trim();
		
		if(StringUtil.isEmpty(sUsername)){
			showToast("用户名不能为空");
			return false;
		}
		
		if(StringUtil.isEmpty(sEmail)){
			showToast("邮箱地址不能为空");
			return false;
		}
		
		if(StringUtil.isEmpty(sPassword)){
			showToast("密码不能为空");
			return false;
		}
		
		if(StringUtil.isEmpty(sRepassword)){
			showToast("确认密码不能为空");
			return false;
		}
		
		if(!sPassword.equals(sRepassword)){
			showToast("密码与确认密码不一致");
			return false;
		}
		
		return true;
	}
}
