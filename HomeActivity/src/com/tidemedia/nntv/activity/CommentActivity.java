package com.tidemedia.nntv.activity;

import java.net.URLEncoder;
import java.util.HashMap;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.common.Preferences;
import com.tidemedia.nntv.model.BaseResponseModel;
import com.tidemedia.nntv.model.CommentListResponseModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.fragment.CommentListFragment;
import com.tidemedia.nntv.util.PreferencesUtil;
import com.tidemedia.nntv.util.StringUtil;

public class CommentActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = CommentActivity.class.getSimpleName();
	private EditText commentInput;
	private ImageView submitBtn;
	private String newsId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_layout);
		Intent intent = getIntent();
		if (intent == null) {
			showToast("传入参数错误");
			finish();
			return;
		}
		
		newsId = intent.getStringExtra("newsId");
		if (newsId == null) {
			showToast("传入参数错误");
			finish();
			return;
		}
		
		commentInput = (EditText) findViewById(R.id.commentInput);
		submitBtn = (ImageView) findViewById(R.id.submitBtn);
		submitBtn.setOnClickListener(this);
//		newsId = "2142552";
		
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.title)).setText("评论");
		
		getCommentList();
	}
	
	private void getCommentList() {
		String url = Constants.GET_COMMENT_LIST + "?item_id=" + newsId;
		Log.i(TAG, "comment list url : " + url);
		ThreadManager.exeTask(this, APIContants.GET_COMMENT_LIST, null, url);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submitBtn:
			submitComment();
			break;
		}
	}

	private void submitComment() {
		String uid = PreferencesUtil.getPreference(this, Preferences.LOGIN_TYPE, Preferences.LOGIN_KEY);
		String name = PreferencesUtil.getPreference(this, Preferences.NAME_TYPE, Preferences.NAME_KEY);
		if (StringUtil.isEmpty(uid)) {
			showToast("需要登录后才能进行评论");
			Intent intent = new Intent(this, LoginOrRegisterActivity.class);
			startActivity(intent);
			return;
		}
		
		String comment = commentInput.getText().toString();
		if (comment.length() == 0) {
			Toast.makeText(this, "评论不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		/*HashMap<String, String> postParameter = new HashMap<String, String>();
		postParameter.put("item_id", newsId);
		postParameter.put("user_id", uid);
		postParameter.put("content", comment);*/
		String encodeComment = comment;
		String encodeName = name;
		try {
			encodeComment = URLEncoder.encode(comment, "UTF-8");
			encodeName = URLEncoder.encode(name, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		String url = Constants.SBUMIT_COMMENT + "?item_id=" + newsId + "&user_id=" + uid + "&content=" + encodeComment + "&username=" + encodeName;
		Log.i(TAG, "" + url);
		ThreadManager.exeTask(this, APIContants.SUBMIT_COMMENT, null, url);
	}
	
	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		Log.i(TAG, "result : " + resultData);
		switch (taskId) {
		case APIContants.GET_COMMENT_LIST:
			parseCommentList(resultData);
			break;
		case APIContants.SUBMIT_COMMENT:
			parseCommentResult(resultData);
			break;
		}
	}

	private void parseCommentResult(String resultData) {
		Log.i(TAG, "" + resultData);
		if (!StringUtil.isEmpty(resultData)) {
			BaseResponseModel json = StringUtil.fromJson(resultData, BaseResponseModel.class);
			if (json != null && json.getMessage() != null) {
				Toast.makeText(this, json.getMessage(), Toast.LENGTH_SHORT).show();
				commentInput.setText(null);
				getCommentList();
			}
		}
	}
	
	private String toUnicode(String str){
		String result = "";
		 for(int i = 0;i<str.length();i++){
			 result += "\\u" + Integer.toHexString(str.charAt(i));
	          // System.out.print("\\u" + Integer.toHexString(str.charAt(i))); 
	       }
		 return result;
	}

	private void parseCommentList(String resultData) {
		Log.i(TAG, "comment list result : " + resultData);
		if (!StringUtil.isEmpty(resultData)) {
			CommentListResponseModel json = StringUtil.fromJson(resultData, CommentListResponseModel.class);
			if (json != null && json.result != null && json.result.size() != 0) {
				CommentListFragment fragment = new CommentListFragment(json.result);
				getFragmentManager().beginTransaction().replace(R.id.commentListLayout, fragment).commit();
			}
		}
	}
}
