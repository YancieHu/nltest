package com.tidemedia.nntv.util;


import com.tidemedia.nntv.common.TvApplication;

import android.util.Log;

public class LogUtil {

	private static final boolean IS_LOG = TvApplication.mDebugMode;

	public static void d(String tag, String msg) {
        if (IS_LOG) {
            Log.d(tag, msg);
        }
	}

	public static void i(String tag, String msg) {
        if (IS_LOG) {
            Log.i(tag, msg);
        }
	}

	public static void e(String tag, String msg) {

        if (IS_LOG) {
            Log.e( tag, msg);
        }
	}
	
	public static void w(String tag, String msg) {
        if (IS_LOG) {
            Log.w(tag, msg);
        }
	}

    public static void v(String tag, String msg) {
        if (IS_LOG) {
            Log.v(tag, msg);
        }
    }
}
