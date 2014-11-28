package com.tidemedia.nntv.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.baidu.android.pushservice.PushConstants;
import com.tidemedia.nntv.activity.BaseActivity;
import com.tidemedia.nntv.activity.HomeActivity;
import com.tidemedia.nntv.activity.ImageDetailActivity;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.model.Push;
import com.tidemedia.nntv.util.CommonUtils;
import com.tidemedia.nntv.util.LogUtil;
import com.tidemedia.nntv.util.StringUtil;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private Application mApplication;

	public MyReceiver(Application application) {
		mApplication = application;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) {
			String customContentString = intent
					.getStringExtra(PushConstants.EXTRA_EXTRA);
			String type = "";
			String id = "";
			if (!TextUtils.isEmpty(customContentString)) {
				JSONObject customJson = null;
				try {
					customJson = new JSONObject(customContentString);
					type = customJson.getString("type");
					id = customJson.getString("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if ("news".equals(type)) {
				Intent pushIntent = new Intent(context, NewsActivity.class);
				pushIntent.putExtra("newsId", id);
				pushIntent.putExtra("toFlag", "home");
				pushIntent.putExtra("from", "push");
				pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mApplication.startActivity(pushIntent);
			} else if ("video".equals(type)) {
				Intent pushIntent = new Intent(context, NewsActivity.class);
				pushIntent.putExtra("newsId", id);
				pushIntent.putExtra("toFlag", "home");
				pushIntent.putExtra("from", "push");
				pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(pushIntent);
			} else if ("picture".equals(type)) {
				Intent pushIntent = new Intent(context,
						ImageDetailActivity.class);
				pushIntent.putExtra("id", id);
				pushIntent.putExtra("toFlag", "home");
				pushIntent.putExtra("from", "push");
				pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mApplication.startActivity(pushIntent);
			}
		}
		//
		// Bundle bundle = intent.getExtras();
		// String value = bundle.getString("cn.jpush.android.EXTRA");
		// if (StringUtil.isNotEmpty(value)) {
		// Push push = StringUtil.fromJson(value, Push.class);
		//
		// String type = push.getType();
		// String channel = push.getChannel();
		// if ("tuji".equals(type)) {
		// Intent typeIntent = new Intent(context,
		// ImageDetailActivity.class);
		// typeIntent.putExtra("id", push.getId());
		// Log.v("aaaaaaaaa", "aaaaaa");
		// // typeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(typeIntent);
		// return;
		// }
		//
		// Intent pushIntent = new Intent(context, NewsActivity.class);
		//
		// LogUtil.i(TAG, "push value-->" + value);
		//
		// pushIntent.putExtra("newsId", push.getId());
		// if ("toutiao".equals(type)) {
		// pushIntent.putExtra("toFlag", "home");
		// } else {
		// pushIntent.putExtra("toFlag", type);
		// }
		// pushIntent.putExtra("channel", channel);
		// pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(pushIntent);
		// mBaseActivity.startActivity(pushIntent)
	}
	/*
	 * Log.d("ASFDSDFSAF", value); //pushIntent.putExtra("newsId",
	 * bundle.getString("id")); pushIntent.putExtra("toFlag", "home");
	 * pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 * context.startActivity(pushIntent);
	 */

	// } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
	// .getAction())) {
	// Log.d(TAG,
	// "[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
	// + bundle.getString(JPushInterface.EXTRA_EXTRA));
	// // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
	// // 打开一个网页等..
	//
	// } else {
	// Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
	// }
	// }

	// 打印所有的 intent extra 数据
	// private static String printBundle(Bundle bundle) {
	// StringBuilder sb = new StringBuilder();
	// for (String key : bundle.keySet()) {
	// if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
	// sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
	// } else {
	// sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
	// }
	// }
	// return sb.toString();
	// }

	/*
	 * //send msg to MainActivity private void processCustomMessage(Context
	 * context, Bundle bundle) { if (MainActivity.isForeground) { String message
	 * = bundle.getString(JPushInterface.EXTRA_MESSAGE); String extras =
	 * bundle.getString(JPushInterface.EXTRA_EXTRA); Intent msgIntent = new
	 * Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
	 * msgIntent.putExtra(MainActivity.KEY_MESSAGE, message); if
	 * (!ExampleUtil.isEmpty(extras)) { try { JSONObject extraJson = new
	 * JSONObject(extras); if (null != extraJson && extraJson.length() > 0) {
	 * msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras); } } catch
	 * (JSONException e) {
	 * 
	 * }
	 * 
	 * } context.sendBroadcast(msgIntent); } }
	 */
}
