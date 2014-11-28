package com.tidemedia.nntv.common;

public class Constants {
		
	public static final String CAT_SERVICE_URL = "http://180.141.89.20:2080/";
	public static final String SERVICE_URL = "http://180.141.89.20:2088/";
	public static final String BASE_SERVICE = "http://user.nntv.cn/";
	public static final String APP_SERVICE = "http://app.nntv.cn/";
	
	public static final String HOMEPAGE = SERVICE_URL + "/index/index.json";
	public static final String NEWS_1 = SERVICE_URL + "/api/new_index.php";
	public static final String GET_NEWS_CAT = CAT_SERVICE_URL + "/news/nav_api.json";
	public static final String GET_IMG_CAT = SERVICE_URL + "/picture/pic_nav_api.json";
	public static final String GET_IMG_LIST = SERVICE_URL + "/api/photo_index.php";
	public static final String GET_COLUMN_LIST = SERVICE_URL + "/api/lm_index.php?pagesize=10";
	public static final String GET_LIVE_LIST = SERVICE_URL + "/api/live.php";
	public static final String GET_DISCLOSE_LIST = SERVICE_URL + "/api/reportlist.php";
	public static final String GET_DISCLOSE_DETAIL = SERVICE_URL + "api/report_content.php";
	public static final String GET_VOTE_LIST = SERVICE_URL + "app/e/vote.json";
	public static final String GET_WEATHER_LIST = "http://publicapp.cutv.com:8082/nanningTqApi.php?debug=0";
	public static final String GET_SEARCH_RESULT = SERVICE_URL + "/api/search.php";
	public static final String GET_HOME_DETAIL = SERVICE_URL + "/api/index_content.php?id=";
//	public static final String GET_HOME_LIST = SERVICE_URL + "/app/index/index.json";
	public static final String GET_HOME_LIST = APP_SERVICE + "/api/index_list.php";
	public static final String VOTE = SERVICE_URL + "api/vote.php";
	public static final String ABOUT = SERVICE_URL + "api/about.html";
	public static final String FEED_BACK = SERVICE_URL + "/api/feedback.php";
	public static final String LOGIN = BASE_SERVICE + "nnplatform/?mod=api&ac=appapi&m=login&inajax=1&utf8=1&jsoncallback=callback_login&m=login";
	public static final String REG = BASE_SERVICE + "nnplatform/?mod=api&ac=appapi&m=login&inajax=1&utf8=1&jsoncallback=callback_register&m=register";
	public static final String IMG_DETAIL = SERVICE_URL + "api/photo_content.php?";
	public static final String GET_COMMENT_LIST = SERVICE_URL + "api/comment.php";
	public static final String SBUMIT_COMMENT = SERVICE_URL + "api/comment.php";
	public static final String SBUMIT_DISCLOSE = SERVICE_URL + "api/report.php";
	public static final String COLUMN_WHOLE = SERVICE_URL + "api/lm_whole.php";
	public static final String COLUMN_SINGLE = SERVICE_URL + "api/lm_explode.php";
	public static final String GET_VIDEO = "http://user.nntv.cn/nnplatform/index.php?mod=api&ac=tidecms&m=getvideourl&return=json&inajax=1&globalid=";
	public static final String COLUMN_DETAIL = SERVICE_URL + "/api/lm_content.php?id=";
	public static final String GET_NEWS_DETAIL = SERVICE_URL + "/api/news_content.php?id=";
	public static final String UPGRADE = SERVICE_URL + "/version/version.json";
	public static final String TRAVEL = APP_SERVICE + "api/new_list.php";
	public static final String TRAVEL_DETAIL = APP_SERVICE + "/api/index_content.php?channelid=15565&id=";
	public static final String FOOD = APP_SERVICE + "api/new_list.php";
	public static final String FOOD_DETAIL = APP_SERVICE + "/api/index_content.php?channelid=15563&id=";
}
