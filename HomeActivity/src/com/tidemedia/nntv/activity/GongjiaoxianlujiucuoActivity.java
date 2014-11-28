package com.tidemedia.nntv.activity;

import java.net.URLDecoder;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tidemedia.nntv.R;

public class GongjiaoxianlujiucuoActivity extends BaseActivity {

	private WebView mWebView;
	String url = "http://tousu.baidu.com/map/add#2";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_gongjiaoluxian_jiucuo);

		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		ab.setCustomView(R.layout.action_bar_back1);
		findViewById(R.id.action_back).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((TextView) findViewById(R.id.title)).setText("公交线路纠错");

		mWebView = (WebView) findViewById(R.id.webViewJiucuo);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setDownloadListener(new MyWebViewDownLoadListener());
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.clearCache(true);
		mWebView.loadUrl(url);
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Log.i("tag", "url=" + url);
			Log.i("tag", "userAgent=" + userAgent);
			Log.i("tag", "contentDisposition=" + contentDisposition);
			Log.i("tag", "mimetype=" + mimetype);
			Log.i("tag", "contentLength=" + contentLength);
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	// Web视图
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.indexOf("bike") != -1 || url.indexOf(".gif") != -1
					|| url.indexOf(".png") != -1) {
				Uri uri = Uri.parse(url);
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				// Intent it = new Intent();
				// it.setAction("android.intent.action.VIEW");
				// it.setData(uri);
				startActivity(it);
			} else if (url.indexOf(".m3u8") != -1) {
				// BusinessUtil.playVideoBySysPlayer(instance, url);
				// BusinessUtil.playVideo(WebViewActivity.this, url, handler);
			} else if (url.startsWith("playvideo:")) {
				// //Log.i(tag, "web链接=="+url);
				int index = url.indexOf("playvideo:");
				int startIndex = index + 12;
				// int endIndex = url.lastIndexOf(".MOV");
				String strUrl = url.substring(startIndex);
				strUrl = URLDecoder.decode(strUrl);
				// //Log.i(tag, "切割后的链接=="+strUrl);
				// BusinessUtil.playVideoBySysPlayer(instance, strUrl);
				// BusinessUtil.playVideo(WebViewActivity.this, strUrl,
				// handler);
			} else if (url.startsWith("showsharemodule:")) {
				// 检查是否已经登录
				// checkLogin();
				// if (!isLogin) {
				// showLoginDialog_finish();
				// } else {
				// Intent intent = new Intent(WebViewActivity.this,
				// ShowShareActivity.class);
				// WebViewActivity.this.startActivity(intent);
				// }
			} else if (url.startsWith("openapk:")) {
				// view.loadData(data, mimeType, encoding);
			} else {
				view.loadUrl(url);
			}

			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
					+ "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

		}
	}

}
