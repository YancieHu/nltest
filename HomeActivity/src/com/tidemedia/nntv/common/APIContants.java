package com.tidemedia.nntv.common;

import android.os.Environment;

public class APIContants {

	public static final boolean DEBUG = false;
	public static final String LOGFILENAME = "task.log";
	public static final String SDPATH = Environment.getExternalStorageDirectory().getPath() + "//";
	
	public static final int HOMEPAGE = 1;
	public static final int NEWS_1 = 2;
	public static final int GET_CATETOEY = 3;
	public static final int GET_NEWS_CAT = 4;
	public static final int GET_IMG_CAT = 5;
	public static final int GET_IMG_LIST = 6;
	public static final int GET_COLUMN_LIST = 7;
	public static final int GET_LIVE_LIST = 8;
	public static final int GET_DISCLOSE_LIST = 9;
	public static final int GET_VOTE_LIST = 10;
	public static final int GET_SEARCH_RESULT = 11;
	public static final int FEED_BACK = 12;
	public static final int LOGIN = 13;
	public static final int REG = 14;
	public static final int IMG_DETAIL = 15;
	public static final int GET_COMMENT_LIST = 16;
	public static final int SUBMIT_COMMENT = 17;
	public static final int GET_WHOLE = 18;
	public static final int GET_SINGLE = 19;
	public static final int GET_UPGRADE = 20;
	public static final int TRAVEL = 21;
	public static final int FOOD = 22;
}
