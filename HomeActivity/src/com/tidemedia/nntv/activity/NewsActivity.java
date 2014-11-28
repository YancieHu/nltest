package com.tidemedia.nntv.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.NewsContentResponseModel;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.fragment.NewsFragment;
import com.tidemedia.nntv.util.StringUtil;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMWXHandler;

public class NewsActivity extends BaseActivity {

	private String toFlag = "";
	private String from = "";
	private NewsModel shareModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		String id = intent.getStringExtra("newsId");
		toFlag = intent.getStringExtra("toFlag");
		from = intent.getStringExtra("from");
		if (id == null) {
			finish();
			return;
		}

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.action_bar_back);
		findViewById(R.id.action_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((TextView) findViewById(R.id.title)).setTextAppearance(this,
				R.style.textStyle_black_16);
		((TextView) findViewById(R.id.title)).setText("新闻详情");
		String url = "";
		if (toFlag.equals("column")) {
			((TextView) findViewById(R.id.title)).setText("栏目详情");
			url = Constants.COLUMN_DETAIL + id;
		} else if (toFlag.equals("home") && from.equals("home_list")) {
			// 首页列表
			url = "http://180.141.89.20:2088/api/index_content.php?channelid=15553&id=" + id;
		} else if (toFlag.equals("home") && from.equals("home_gallery")) {
			// 首页轮播
			url = "http://180.141.89.20:2088/api/index_content.php?channelid=15084&id=" + id;
		} else if (toFlag.equals("home")) {
			url = Constants.GET_HOME_DETAIL + id;
		} else if (toFlag.equals("travel") || toFlag.equals("lvyou")) {
			url = Constants.TRAVEL_DETAIL + id;
		} else if (toFlag.equals("food") || toFlag.equals("meishi")) {
			url = Constants.FOOD_DETAIL + id;
		} else if (toFlag.equals("xinwen")) {
			url = Constants.GET_NEWS_DETAIL + id;
		} else {
			url = Constants.GET_NEWS_DETAIL + id;
		}
		/*
		 * if(isColumn){ //栏目 url = Constants.COLUMN_DETAIL + id; }else{ //首页
		 * url = Constants.GET_HOME_DETAIL + id; }
		 */
		ThreadManager.exeTask(this, 0, null, url);
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (!StringUtil.isEmpty(resultData)) {
			resultData = resultData.replace("\r\n", "");
			NewsContentResponseModel newsContent = StringUtil.fromJson(
					resultData, NewsContentResponseModel.class);
			if (newsContent != null && newsContent.result != null
					&& newsContent.result.size() != 0) {
				shareModel = newsContent.result.get(0);
				showNews(newsContent.result.get(0));
			} else {
				showNoResultPrompt();
			}
		} else {
			showNoResultPrompt();
		}
	}

	private void showNews(NewsModel news) {
		Fragment fragment = new NewsFragment(news);
		if (!isFinishing()) {
			getFragmentManager().beginTransaction()
					.replace(android.R.id.content, fragment).commit();
		}
	}

	private void showNoResultPrompt() {
		Toast.makeText(this, "没有找到新闻内容", Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.news, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_comment:
			if (null != shareModel) {
				Intent intent = new Intent(this, CommentActivity.class);
				intent.putExtra("newsId", shareModel.getId());
				startActivity(intent);
			}
			return true;
		case R.id.action_share:
			if (shareModel == null) {
				break;
			}
			final UMSocialService mController = UMServiceFactory
					.getUMSocialService("com.umeng.share", RequestType.SOCIAL);
			String appID = "wx4d7651ba975bbdeb";
			// 微信图文分享必须设置一个url
			String contentUrl = "http://www.nntv.cn/";
			// 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
			UMWXHandler wxHandler = mController.getConfig().supportWXPlatform(
					this, appID, contentUrl);
			// 设置分享标题
			wxHandler.setWXTitle(shareModel.getTitle());
			// 支持微信朋友圈
			UMWXHandler circleHandler = mController.getConfig()
					.supportWXCirclePlatform(this, appID, contentUrl);
			circleHandler.setCircleTitle(shareModel.getTitle());
			/*
			 * circleHandler.setShowWords("http://www.nntv.cn/" + " " +
			 * shareModel.getContent().replaceAll("<[A-z/ =']*>",
			 * "").replaceAll("&nbsp;", ""));
			 * 
			 * mController.setShareContent("http://www.nntv.cn/" + " " +
			 * shareModel.getContent().replaceAll("<[A-z/ =']*>",
			 * "").replaceAll("&nbsp;", ""));;
			 */
			circleHandler.setShowWords(shareModel.getTitle() + " "
					+ shareModel.getShare_url());
			mController.setShareContent(shareModel.getTitle() + " "
					+ shareModel.getShare_url());
			mController.setShareMedia(new UMImage(this, shareModel
					.getImage_url()));
			mController.openShare(this, false);
			return true;
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
