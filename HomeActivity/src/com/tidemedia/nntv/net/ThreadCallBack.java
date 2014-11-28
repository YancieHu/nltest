package com.tidemedia.nntv.net;

import java.io.Serializable;

public interface ThreadCallBack extends Serializable {

	public void onCallbackFromThread(String resultData, int taskId);

	public void onCancelFromThread(String msg, int taskId);
}
