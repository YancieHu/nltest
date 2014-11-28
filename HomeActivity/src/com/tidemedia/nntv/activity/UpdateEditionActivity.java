package com.tidemedia.nntv.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import com.tidemedia.nntv.common.Preferences;
import com.tidemedia.nntv.common.TvApplication;
import com.tidemedia.nntv.model.Upgrade;
import com.tidemedia.nntv.service.DownloadService;
import com.tidemedia.nntv.service.UpdateService;
import com.tidemedia.nntv.util.StringUtil;

import android.view.View;
import android.view.Window;

public class UpdateEditionActivity extends BaseActivity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Upgrade mUpgradeContent;
	public static String updateURL;
	private String mVersionName = "";
	
	private ProgressDialog download;
	protected static final int GUI_STOP_NOTIFIER = 0x108;
	protected static final int GUI_THREADING_NOTIFIER = 0x109;
	protected static final int GUI_ERROR_NOTIFIER = 0x110;
	protected static final int GUI_IO_NOTIFIER = 0x111;
	protected static final int GUI_INTERRUPTED_NOTIFIER = 0x112;
	protected static final int GUI_PROGRESS_NOTIFIER = 0x113;// 开始进度条的进度值显示
	protected static final int GUI_UPDATE_CONTENT_NOTIFIER = 0x114;// 有更新文本
	private int count = 0;
	private long total;
	private SharedPreferences prefs;
	private long newVersion;
	private String apkFile = Environment.getExternalStorageDirectory() + "/Hotel_Tujia.apk";
	private DownloadService.DownloadBinder binder;

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (DownloadService.DownloadBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	class DownThread extends Thread {

		private boolean cancel = false;
		private File file;

		public void run() {

			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(updateURL);
			HttpResponse response = null;
			FileOutputStream fileOutputStream = null;
			InputStream is = null;
			try {
				response = client.execute(get);
				if (response.getStatusLine().getStatusCode() != 200)
					throw new IOException("StatusCode!=200");
				HttpEntity entity = response.getEntity();
				total = entity.getContentLength();
				mHandler.sendEmptyMessage(GUI_PROGRESS_NOTIFIER);
				is = entity.getContent();
				if (is != null) {

					file = new File(apkFile);
					fileOutputStream = new FileOutputStream(file);

					byte[] buf = new byte[1024];
					int ch = -1;
					while ((ch = is.read(buf)) != -1) {
						// 判断线程是否被中断
						if (cancel)
							throw new InterruptedException();
						count += ch;
						fileOutputStream.write(buf, 0, ch);
						mHandler.sendEmptyMessage(GUI_THREADING_NOTIFIER);
					}
					if (count == total)
						mHandler.sendEmptyMessage(GUI_STOP_NOTIFIER);
					fileOutputStream.flush();
					if (fileOutputStream != null)
						fileOutputStream.close();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(GUI_ERROR_NOTIFIER);
				if (file.exists())
					file.delete();

			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(GUI_IO_NOTIFIER);
			} catch (InterruptedException e) {
				e.printStackTrace();
				if (file.exists())
					file.delete();
				mHandler.sendEmptyMessage(GUI_INTERRUPTED_NOTIFIER);
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		private void setCancel(boolean isCancel) {
			cancel = isCancel;
		}

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GUI_THREADING_NOTIFIER:
				download.setProgress(count);
				break;
			case GUI_STOP_NOTIFIER:
				download.dismiss();
				prefs.edit()
						.putLong(Preferences.KEY_LATEST_VERSION,
								newVersion).commit();
				// 表示已下载但还未安装
				prefs.edit()
						.putBoolean(
								Preferences.KEY_LATEST_VERSION_INSTALL,
								false).commit();
				install();
				break;
			case GUI_IO_NOTIFIER:
			case GUI_ERROR_NOTIFIER:
				if (download != null)
					download.dismiss();
				new AlertDialog.Builder(UpdateEditionActivity.this)
						.setCancelable(false)
						.setMessage("应用程序更新出错")
						.setPositiveButton("退出",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										stopService();
									}
								}).show();
				break;
			case GUI_INTERRUPTED_NOTIFIER:
				download.dismiss();
				new AlertDialog.Builder(UpdateEditionActivity.this)
						.setCancelable(false)
						.setMessage("取消更新应用程序")
						.setPositiveButton("退出",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										stopService();
										// 如果放弃本次更新，将不再进行提示
										prefs.edit()
												.putLong(
														Preferences.KEY_LATEST_VERSION,
														newVersion).commit();
										prefs.edit()
												.putBoolean(
														Preferences.KEY_LATEST_VERSION_INSTALL,
														true).commit();
									}
								}).show();
				break;
			case GUI_PROGRESS_NOTIFIER:
				download.setMax((int) total);
				download.setProgress(0);
				break;
			case GUI_UPDATE_CONTENT_NOTIFIER:
				// 更新内容
				download();
				break;
			}
		}
	};


	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		mUpgradeContent = (Upgrade)getIntent().getSerializableExtra("upgradeContent");
		if (mUpgradeContent != null) {			
			updateURL = mUpgradeContent.getUpdate();
			String version = mUpgradeContent.getVersion();
			mVersionName = version;
			if (Double.parseDouble(version) != Double.parseDouble(TvApplication.mVersionName )&& StringUtil.isNotEmpty(updateURL)) {

				download();
				Intent intent = new Intent(this, DownloadService.class);
				intent.putExtra("updateURL", updateURL);
				startService(intent); // 如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁
				bindService(intent, conn, Context.BIND_AUTO_CREATE);
				return;
			}
		} 
		this.finish();
	}

	private void install() {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(new File(apkFile)), "application/vnd.android.package-archive");
		startActivity(i);
		stopService();
	}
	
	/*private void backToHome(){
		UpdateEditionActivity.this.finish();
		Intent home = new Intent(Intent.ACTION_MAIN);
		home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		home.addCategory(Intent.CATEGORY_HOME);
		startActivity(home);
	}*/

	/**
	 * 【弹出下载提示对话框】
	 */
	private void download() {
		new DownThread();
		/*UpdateEditionActivity.this.finish();
		binder.start();*/
		String alertMessage = "版本号：" + mVersionName;
		
			new AlertDialog.Builder(this)
			.setTitle("更新")
			.setCancelable(false)
			.setMessage(alertMessage)
			.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					UpdateEditionActivity.this.finish();
				}
			})
			.setPositiveButton("去升级",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							UpdateEditionActivity.this.finish();
							binder.start();
							UpdateEditionActivity.this.finish();
						}
					})
			.setNegativeButton("以后提醒",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							stopService();
							dialog.dismiss();
							UpdateEditionActivity.this.finish();
						}
					}).show();
		
	}
	/**
	 * stopSelf，服务关闭自身
	 */
	private void stopService() {
		Intent stop = new Intent(this, UpdateService.class);
		stopService(stop);
		finish();;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		prefs = null;
		if (null != binder) {
			unbindService(conn);
		}
	}

	public void cancel(View view) {
		if (null != binder && !binder.isCancelled()) {
			binder.cancel();
		}
	}
}
