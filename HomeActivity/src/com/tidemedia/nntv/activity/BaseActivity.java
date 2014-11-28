package com.tidemedia.nntv.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.push.example.MyPushMessageReceiver;
import com.google.gson.InstanceCreator;
import com.tidemedia.nntv.net.ThreadCallBack;

public class BaseActivity extends Activity implements ThreadCallBack {
	private Toast toast;
	public static BaseActivity instance;
//	public static Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			String type = "";
//			String id = "";
//			String customContentString=(String) msg.obj;
//			if (!TextUtils.isEmpty(customContentString)) {
//				JSONObject customJson = null;
//				try {
//					customJson = new JSONObject(customContentString);
//					type = customJson.getString("type");
//					id = customJson.getString("id");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//			if ("news".equals(type)) {
//				Intent pushIntent = new Intent(instance,
//						NewsActivity.class);
//				pushIntent.putExtra("newsId", id);
//				pushIntent.putExtra("toFlag", "home");
//				pushIntent.putExtra("from", "push");
//				pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				instance.startActivity(pushIntent);
//			} else if ("video".equals(type)) {
//				Intent pushIntent = new Intent(instance,
//						NewsActivity.class);
//				pushIntent.putExtra("newsId", id);
//				pushIntent.putExtra("toFlag", "home");
//				pushIntent.putExtra("from", "push");
//				pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				instance.startActivity(pushIntent);
//			} else if ("picture".equals(type)) {
//				Intent pushIntent = new Intent(instance,
//						ImageDetailActivity.class);
//				pushIntent.putExtra("id", id);
//				pushIntent.putExtra("toFlag", "home");
//				pushIntent.putExtra("from", "push");
//				instance.startActivity(pushIntent);
//			}
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		// instance=this;
		PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY,
				"EN4QCdLAQTE7dglN7PRgjPkm");
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelFromThread(String msg, int taskId) {
		// TODO Auto-generated method stub

	}

	public void showToast(String msg) {
		toast.setText(msg);
		toast.show();
	}
}
