package com.tidemedia.nntv.sliding.fragment;

import java.net.URLDecoder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tidemedia.nntv.R;

public class JingdianFragment extends BaseFragment {

	private WebView mWebView;
	private Context mContext;
	private TextView textViewJingdian;

	String url;

	public JingdianFragment(String url) {
		this.url = url;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = this.getActivity();
		View view = (View) inflater
				.inflate(R.layout.layout_weather_first, null);
		mWebView = (WebView) view.findViewById(R.id.webViewWeather);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setDownloadListener(new MyWebViewDownLoadListener());
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.clearCache(true);
		mWebView.loadUrl(url);

		textViewJingdian = (TextView) view.findViewById(R.id.textViewJingdian);
		textViewJingdian.setVisibility(View.GONE);
		return view;
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
