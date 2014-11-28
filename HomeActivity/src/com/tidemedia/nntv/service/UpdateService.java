package com.tidemedia.nntv.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tidemedia.nntv.activity.UpdateEditionActivity;
import com.tidemedia.nntv.model.Upgrade;

public class UpdateService extends Service {
	private static String TAG = "upgradeContent";
	public static String VERSION = "upgradeContent";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(null == intent){
			return;
		}
		final Upgrade upgradeContent = (Upgrade) intent.getSerializableExtra("upgradeContent");
		new Thread() {
			public void run() {
				// if(checkUpdate()){//如果有更新，则显示更新界面UpdateActivity，类似一个对话框
				Intent update = new Intent(UpdateService.this, UpdateEditionActivity.class);
				update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				update.putExtra(VERSION, upgradeContent);
				startActivity(update);
			}
		}.start();

	}

}
