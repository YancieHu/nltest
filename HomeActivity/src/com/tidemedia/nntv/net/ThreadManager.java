package com.tidemedia.nntv.net;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.tidemedia.nntv.R;

public class ThreadManager {
	
	//public static AlertDialog dialog;
	private static Context mContext;
	public static Thread thread = null;
	public static boolean isGo = true;
	public static ExecutorService threadPool = Executors.newFixedThreadPool(2);
	
	static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String resultData = (String) msg.obj;
			int taskId = msg.arg1;
			if ("�����쳣".equals(resultData)) {
				Toast.makeText(mContext, "�����쳣", Toast.LENGTH_SHORT).show();
				/*if (dialog != null && dialog.getWindow() != null
						&& dialog.isShowing())
					dialog.cancel();*/
			} else if (isGo) {
				/*if (dialog != null && dialog.getWindow() != null
						&& dialog.isShowing()) {
					try {
						dialog.dismiss();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}*/
				ThreadCallBack callBack = (ThreadCallBack) msg.getData()
						.getSerializable("callback");
				callBack.onCallbackFromThread(resultData, taskId);
			}
		}
	};

	public static void exeTask(final ThreadCallBack callBack, final int taskId, final HashMap<String, String> postParameter, final String reqUrl) {
		ThreadManager.isGo = true;
		NetManager.stop = false;
		
		if(callBack instanceof Fragment){
			mContext = (Context) ((Fragment)callBack).getActivity();
		}else{
			mContext = (Context)callBack;
		}
		
		if (!NetManager.checkNetWork(mContext)) {
			return;
		}
		/*dialog = new AlertDialog.Builder(mContext).create();
		dialog.show();
		
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		View loadingView = LayoutInflater.from(mContext).inflate(R.layout.dialog_loading, null);
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		
		dialog.getWindow().setContentView(loadingView, lp);
		dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				NetManager.stop = true;
				isGo = false;
			}
		});*/

		// dialog.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface arg0) {
		// // TODO Auto-generated method stub
		// LogUtil.printInfo("222222222222");
		// }
		// });
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String resultData = sendToServer(taskId, postParameter, reqUrl);
				if (isGo) {
					Message msg = new Message();
					msg.arg1 = taskId;
					msg.obj = resultData;
					msg.getData().putSerializable("callback", callBack);
					handler.sendMessage(msg);
				}
			}
		});
		isGo = true;
		threadPool.execute(thread);
	}

	public static void exeTask(final ThreadCallBack callBack, final int taskId,
			final HashMap<String, String> postParameter, final String reqUrl,
			final boolean dialogIsCancelable, final boolean dialogIsAppear) {
		ThreadManager.isGo = true;
		NetManager.stop = false;
		mContext = (Context) callBack;
		/*
		 * if (!CheckNetWorkUtil.netWorkIsAvailable(mContext)) {
		 * if(dialogIsAppear) { dialog =
		 * DialogHelper.showNetworkDialog(mContext, dialogIsCancelable,new
		 * OnCancelListener() {
		 * 
		 * @Override public void onCancel(DialogInterface arg0) {
		 * NetManager.stop = true; isGo= false;
		 * callBack.onCancelFromThread("û�п��õ�����",taskId); //if(isGoBack)
		 * //((Activity)mContext).finish(); } }); } else {
		 * callBack.onCancelFromThread("û�п��õ�����",taskId);
		 * //callBack.onCallbackFromThread("û�п��õ�����", taskId); } return; }
		 */
		//if (dialogIsAppear) {
			// dialog = new Dialog(mContext,R.style.NobackDialog);
			// FrameLayout parentLayout =
			// ((FrameLayout)Constants.loadingView.getParent());
			// if(null != parentLayout){
			// parentLayout.removeAllViews();
			// }
			// Constants.loadingView.startAnim();
			// dialog.setContentView(Constants.loadingView);
			// dialog.setCancelable(dialogIsCancelable);
			// dialog.show();
			// Constants.loadingView.getCancle().setOnClickListener(new
			// OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// if(null != dialog && dialog.isShowing()){
			// dialog.dismiss();
			// NetManager.stop = true;
			// isGo= false;
			// ((Activity)mContext).finish();
			// }
			// }
			// });
			// dialog.setOnCancelListener(new OnCancelListener() {
			// @Override
			// public void onCancel(DialogInterface arg0) {
			// NetManager.stop = true;
			// isGo= false;
			// ((Activity)mContext).finish();
			// }
			// });
			/*
			 * dialog = DialogHelper.showProgress(mContext,
			 * dialogIsCancelable,new OnCancelListener() {
			 * 
			 * @Override public void onCancel(DialogInterface arg0) {
			 * NetManager.stop = true; isGo= false;
			 * callBack.onCancelFromThread("",taskId); //if(isGoBack)
			 * //((Activity)mContext).finish(); } });
			 */
		//}

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String resultData = sendToServer(taskId, postParameter, reqUrl);
				if (isGo) {
					Message msg = new Message();
					msg.arg1 = taskId;
					msg.obj = resultData;
					msg.getData().putSerializable("callback", callBack);
					handler.sendMessage(msg);
				}
			}
		});
		isGo = true;
		threadPool.execute(thread);
	}

	public static String sendToServer(int taskId, HashMap<String, String> hashMap, String reqUrl) {
		String resultData = NetManager.doPost(reqUrl, hashMap);

		return resultData;
	}

	/**
	 * �رգ����ȴ�����ִ����ɣ�������������
	 */
	public static void shutdown() {
		if (threadPool != null) {
			threadPool.shutdown();
		}
	}

	/**
	 * �رգ������رգ���������������ִ�е��̣߳�������������
	 */
	public static void shutdownRightnow() {
		if (null != threadPool) {
			threadPool.shutdownNow();
			try {
				// ���ó�ʱ���̣�ǿ�ƹر���������
				threadPool.awaitTermination(1, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
