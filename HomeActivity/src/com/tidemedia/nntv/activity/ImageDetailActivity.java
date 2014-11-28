package com.tidemedia.nntv.activity;

import java.util.HashMap;
import java.util.List;

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
import com.tidemedia.nntv.common.APIContants;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.ImgResModel;
import com.tidemedia.nntv.model.ImgResModel.ImgDetail;
import com.tidemedia.nntv.model.ImgResonseModel.ImgModel;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.MainActivity;
import com.tidemedia.nntv.sliding.fragment.ImageDetailFragment;
import com.tidemedia.nntv.util.StringUtil;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMWXHandler;

public class ImageDetailActivity extends BaseActivity {

	private ImgModel imageItem;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getData();
		if (StringUtil.isNotEmpty(id)) {
			getImageDetail();
		} else {
			finish();
		}
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		((TextView) findViewById(R.id.title)).setTextAppearance(this,
				R.style.textStyle_black_16);
		if (null != imageItem) {
			((TextView) findViewById(R.id.title)).setText(imageItem.getTitle());
		}
		findViewById(R.id.action_back).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	private void getData() {
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra("id");
			imageItem = (ImgModel) intent.getSerializableExtra("imageItem");
		}
	}

	private void getImageDetail() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("id", id);
		ThreadManager.exeTask(this, APIContants.IMG_DETAIL, parameters,
				Constants.IMG_DETAIL);
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (!StringUtil.isEmpty(resultData)) {
			ImgResModel imgContent = StringUtil.fromJson(resultData,
					ImgResModel.class);
			if (imgContent != null && imgContent.getResult() != null
					&& imgContent.getResult().size() != 0) {
				showNews(imgContent.getResult());
			} else {
				showNoResultPrompt();
			}
		} else {
			showNoResultPrompt();
		}
	}

	private void showNoResultPrompt() {
		Toast.makeText(this, "没有找到图片内容", Toast.LENGTH_LONG).show();
	}

	private void showNews(List<ImgDetail> list) {
		Fragment fragment = new ImageDetailFragment(list);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment).commit();
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
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("newsId", id);
			startActivity(intent);
			return true;
		case R.id.action_share:
			if (imageItem == null) {
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
			wxHandler.setWXTitle(imageItem.getTitle());
			// 支持微信朋友圈
			UMWXHandler circleHandler = mController.getConfig()
					.supportWXCirclePlatform(this, appID, contentUrl);
			circleHandler.setCircleTitle(imageItem.getTitle());
			circleHandler.setShowWords("http://www.nntv.cn/"
					+ " "
					+ imageItem.getTitle().replaceAll("<[A-z/ =']*>", "")
							.replaceAll("&nbsp;", ""));

			mController.setShareContent("http://www.nntv.cn/"
					+ " "
					+ imageItem.getTitle().replaceAll("<[A-z/ =']*>", "")
							.replaceAll("&nbsp;", ""));
			;
			mController.setShareMedia(new UMImage(this, imageItem
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
