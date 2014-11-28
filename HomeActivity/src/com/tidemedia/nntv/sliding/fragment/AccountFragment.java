package com.tidemedia.nntv.sliding.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.RegActivity;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.common.Preferences;
import com.tidemedia.nntv.model.LoginRes;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.PreferencesUtil;
import com.tidemedia.nntv.util.StringUtil;

public class AccountFragment extends BaseFragment implements OnClickListener {
	
	private EditText userName;
	private EditText password;
	private View view;
	private TextView regTextView, inName;
	private Button loginButton, logOff;
	private String sUserName, sPassword;
	private ImageView userImage;
	private LinearLayout inLayout, offLayout;
	private OnLoginListener listener;
	
    String text = null;

    public AccountFragment() {
    }

    public AccountFragment(String text) {
        Log.e("Krislq", text);
        this.text = text;
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	if (activity instanceof OnLoginListener) {
    		listener = (OnLoginListener) activity;
    	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	view = inflater.inflate(R.layout.login_layout, null);
    	init();
    	/*String uid = PreferencesUtil.getPreference(getActivity(), Preferences.LOGIN_TYPE, Preferences.LOGIN_KEY);
    	String url = PreferencesUtil.getPreference(getActivity(), Preferences.ICON_TYPE, Preferences.ICON_KEY);
    	if(StringUtil.isNotEmpty(uid) && StringUtil.isNotEmpty(url)){
    		showIcon(url, userImage);
    	}*/
        return view;
    }
    
    private boolean validate(){
    	sUserName = userName.getText().toString().trim();
    	sPassword = password.getText().toString().trim();
		if(StringUtil.isEmpty(sUserName)){
			Toast.makeText(getActivity(), "用户名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(StringUtil.isEmpty(sPassword)){
			Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
    
    private void toLogin(){
		String url = Constants.LOGIN + "&username=" + sUserName + "&password=" + sPassword;
		ThreadManager.exeTask(this, APIContants.LOGIN, null, url);
    }
    
    private void init(){
    	userName = (EditText)view.findViewById(R.id.userName);
    	password = (EditText)view.findViewById(R.id.password);
    	regTextView = (TextView)view.findViewById(R.id.regTextView);
    	loginButton = (Button)view.findViewById(R.id.loginButton);
    	userImage = (ImageView)view.findViewById(R.id.userImage);
    	regTextView.setOnClickListener(this);
    	loginButton.setOnClickListener(this);
    	inLayout = (LinearLayout)view.findViewById(R.id.inLayout);
    	offLayout = (LinearLayout)view.findViewById(R.id.offLayout);
    	inName = (TextView)view.findViewById(R.id.inName);
    	logOff = (Button)view.findViewById(R.id.logOff);
    	logOff.setOnClickListener(this);
    	refreshUser();
    }
    
    public void refreshUser() {
    	String uid = PreferencesUtil.getPreference(getActivity(), Preferences.LOGIN_TYPE, Preferences.LOGIN_KEY);
    	String uName = PreferencesUtil.getPreference(getActivity(), Preferences.NAME_TYPE, Preferences.NAME_KEY);
    	String iconUrl = PreferencesUtil.getPreference(getActivity(), Preferences.ICON_TYPE, Preferences.ICON_KEY);
    	
    	if (StringUtil.isNotEmpty(uid) && StringUtil.isNotEmpty(uName)) {
    		inLayout.setVisibility(View.GONE);
			regTextView.setVisibility(View.GONE);
			loginButton.setVisibility(View.GONE);
			offLayout.setVisibility(View.VISIBLE);
			inName.setText(uName);
    	} else if (StringUtil.isNotEmpty(uid)) {
    		regTextView.setVisibility(View.GONE);
    	}
    }
    
    @Override
    public void onCallbackFromThread(String resultData, int taskId) {
    	if (isDetached()) {
			return;
		}
		
    	if(StringUtil.isEmpty(resultData)){
			Toast.makeText(getActivity(), "提交失败，请稍候尝试", Toast.LENGTH_SHORT).show();
			if (listener != null) {
				listener.onFail();
			}
			return;
		}
    	resultData = resultData.substring(15, resultData.length() - 2);
    	LoginRes base = StringUtil.fromJson(resultData, LoginRes.class);
		if(1 == base.getStatus()){
			String uid = base.getUid();
			String url = base.getProfile();
			PreferencesUtil.savePreference(getActivity(), Preferences.LOGIN_TYPE, Preferences.LOGIN_KEY, uid);
			PreferencesUtil.savePreference(getActivity(), Preferences.ICON_TYPE, Preferences.ICON_KEY, url);
			PreferencesUtil.savePreference(getActivity(), Preferences.NAME_TYPE, Preferences.NAME_KEY, base.getName());
			if(StringUtil.isNotEmpty(base.getName())){
				inName.setText(base.getName());
			}
			inLayout.setVisibility(View.GONE);
			regTextView.setVisibility(View.GONE);
			loginButton.setVisibility(View.GONE);
			offLayout.setVisibility(View.VISIBLE);
			//showIcon(url, userImage);
			if (listener != null) {
				listener.onSuccess();
			}
		}else{
			Toast.makeText(getActivity(), base.getMsg(), Toast.LENGTH_SHORT).show();
		}
		if (listener != null) {
			listener.onFail();
		}
    }
    
   /* private void showIcon(String url, final ImageView imageView){
    	Bitmap bitmap = AsyncImageLoader.getInstance().loadDrawable(url, new ImageCallback() {
			@Override
			public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
				if(imageDrawable != null && imageView != null){
					imageView.setImageBitmap(imageDrawable);
				}
			}
		});
		if (bitmap == null) {
			imageView.setImageResource(R.drawable.user_default);
		} else {
			imageView.setImageBitmap(bitmap);
		}
    }*/
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i("RESULT", "AccountFragment's onActivityResult->requestCode:" + requestCode + ", resultCode:" + resultCode);
    	if (resultCode == Activity.RESULT_OK) {
    		refreshUser();
    	}
    }
    
    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.regTextView:
			startActivityForResult(new Intent(getActivity(), RegActivity.class), 0);
			break;
		case R.id.loginButton:
			if(!validate()){
				return;
			}
			toLogin();
		case R.id.logOff:
			PreferencesUtil.clearPreference(getActivity(), Preferences.LOGIN_TYPE, Preferences.LOGIN_KEY);
			PreferencesUtil.clearPreference(getActivity(), Preferences.ICON_TYPE, Preferences.ICON_KEY);
			inLayout.setVisibility(View.VISIBLE);
			regTextView.setVisibility(View.VISIBLE);
			loginButton.setVisibility(View.VISIBLE);
			offLayout.setVisibility(View.GONE);
			userImage.setImageResource(R.drawable.user_default);
			break;
		default:
			break;
		}
    }
    
    public String getText(){
        return text;
    }

    public interface OnLoginListener {
    	void onSuccess();
    	void onFail();
    }
}
