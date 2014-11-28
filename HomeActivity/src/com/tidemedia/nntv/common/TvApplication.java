package com.tidemedia.nntv.common;

import java.io.File;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.receiver.MyReceiver;

public class TvApplication extends FrontiaApplication {

	/*
	 * public TvApplication(){
	 * 
	 * }
	 */

	private static Context mContext;

	public static final boolean mDebugMode = true;

	private static TvApplication mInstance = null;

	public static int mVersionCode;

	public static String mVersionName;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	public static Context getContext() {
		return mContext;
	}

	public static TvApplication getInstance() {
		/*
		 * if(null == mInstance){ mInstance = new TvApplication(); }
		 */
		return mInstance;
	}

	@Override
	public void onCreate() {
		mInstance = this;
		PackageInfo pinfo;
		try {
			pinfo = this.getPackageManager().getPackageInfo(
					this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			mVersionCode = pinfo.versionCode;
			mVersionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY, "EN4QCdLAQTE7dglN7PRgjPkm");
		MyReceiver receiver = new MyReceiver(this);
		IntentFilter filter = new IntentFilter();
		String action = "com.baidu.android.pushservice.action.notification.CLICK";
		filter.addAction(action);
		filter.addAction("com.baidu.android.pushservice.action.MESSAGE");
		// 动态注册BroadcastReceiver
		registerReceiver(receiver, filter);

		SDKInitializer.initialize(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(500);
		mLocationClient = new LocationClient(getApplicationContext()); //
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		mLocationClient.start();
		mLocationClient.requestLocation();

		super.onCreate();
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {

		ThreadManager.shutdownRightnow();
		super.onTerminate();
	}

	public String getDirPath(String name) {
		String path = "";
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			java.io.File file = new File(Environment
					.getExternalStorageDirectory().getPath() + "/jmtv/");
			if (!file.exists()) {
				if (!file.mkdirs()) {
					return path;
				}
			}
			file = new File(Environment.getExternalStorageDirectory().getPath()
					+ "/jmtv/" + name);
			if (!file.exists()) {
				if (file.mkdirs()) {
					path = file.getAbsolutePath();
				}
			} else {
				path = file.getAbsolutePath();
			}
		} else {
			File dir = getDir(name, Context.MODE_PRIVATE
					| Context.MODE_WORLD_READABLE
					| Context.MODE_WORLD_WRITEABLE);
			path = dir.getAbsolutePath();
		}
		// System.out.println(path);
		return path;
	}

	BDLocation mLocation = null;

	public BDLocation getLocation() {
		return mLocation;
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			mLocation = location;
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}
	}
}
