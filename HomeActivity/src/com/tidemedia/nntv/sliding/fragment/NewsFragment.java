package com.tidemedia.nntv.sliding.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.NewsActivity;
import com.tidemedia.nntv.activity.PlayerActivity;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.model.Video;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.ValidateUtil;

public class NewsFragment extends BaseFragment {
	private NewsModel news;
	private TextView title;
	private TextView time;
	// private TextView content;
	private WebView content;
	private String newsContent;
	private LinearLayout loadingView;

	private static final String VIDEO_CONFIG = "<video onclick=\"playvideo(event, '%@')\" width=\"300\" height=\"225\" controls=\"controls\" src=\"%@\" style=\"margin:14px auto; display:block;\"></video>";
	private static final String VIDEO_CONFIG1 = "<video onclick=\"playvideo(event, '%@')\" width=\"300\" height=\"225\" controls=\"controls\" src=\"%@\"";
	private static final String VIDEO_CONFIG2 = " poster=\"%@\" style=\"margin:14px auto; display:block;\"></video>";
	
	public NewsFragment(NewsModel news) {
		this.news = news;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_layout, container, false);
		loadingView = (LinearLayout) view.findViewById(R.id.loadingView);
		title = (TextView) view.findViewById(R.id.title);
		time = (TextView) view.findViewById(R.id.time);
		content = (WebView) view.findViewById(R.id.content);
		content.setVerticalScrollBarEnabled(false);
		content.getSettings().setJavaScriptEnabled(true);
		JavaScriptInterface jsInterface = new JavaScriptInterface();
		content.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				loadingView.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				super.onPageStarted(view, url, favicon);
			}
		});
		content.addJavascriptInterface(jsInterface, "JSInterface");
		if (news != null) {
			title.setText(news.getTitle());
			time.setText(news.getTime());
			newsContent = news.getContent();
			if (!news.isIs_video()) {
				content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
				if (newsContent != null && newsContent.length() > 0) {
					content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
							parseToHtml(news.getContent()), "text/html",
							"utf-8", null);
				}
			} else {
				String videoUrl = news.getVideo_url();
				if (StringUtil.isNotEmpty(videoUrl)) {
					if (ValidateUtil.isInteger(videoUrl)) {
						getVideoUrl(videoUrl);
					} else {
//						String url = VIDEO_CONFIG.replaceAll("%@", videoUrl);
//						if (newsContent != null
//								&& StringUtil.isNotEmpty(newsContent)) {
//							url += newsContent;
//						}
//						content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
//								parseToHtml(url), "text/html", "utf-8", null);
						String url = "";
						if (StringUtil.isNotEmpty(news.getImage_url())) {
							url = VIDEO_CONFIG1.replaceAll("%@", videoUrl);
							String temp = VIDEO_CONFIG2.replaceAll("%@", news.getImage_url());
							url = url + temp;
						} else {
							url = VIDEO_CONFIG.replaceAll("%@", videoUrl);
						}
						if (newsContent != null
								&& StringUtil.isNotEmpty(newsContent)) {
							url += newsContent;
						}
						content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
								parseToHtml(url), "text/html", "utf-8", null);
					}
				}
			}
		}
		return view;
	}

	public class JavaScriptInterface {

		public void startVideo(String videoAddress) {
			Intent intent = new Intent(getActivity(), PlayerActivity.class);
			intent.putExtra("liveUrl", videoAddress);
			intent.putExtra("preview", news.getImage_url());
			startActivity(intent);
		}
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		
		if(StringUtil.isNotEmpty(resultData)){
		loadingView.setVisibility(View.GONE);
		if (StringUtil.isNotEmpty(resultData)) {
			Video video = StringUtil.fromJson(resultData, Video.class);
			if (null != video) {
				String url = video.getUrl();
				if (StringUtil.isNotEmpty(url)) {
//					String videoUrl = VIDEO_CONFIG.replaceAll("%@", url);
//					if (newsContent != null
//							&& StringUtil.isNotEmpty(newsContent)) {
//						videoUrl += newsContent;
//					}
					
					String videoUrl = "";
					if (StringUtil.isNotEmpty(news.getImage_url())) {
						videoUrl = VIDEO_CONFIG1.replaceAll("%@", url);
						String temp = VIDEO_CONFIG2.replaceAll("%@", news.getImage_url());
						videoUrl = videoUrl + temp;
					} else {
						videoUrl = VIDEO_CONFIG.replaceAll("%@", url);
					}
					
					if (newsContent != null
							&& StringUtil.isNotEmpty(newsContent)) {
						videoUrl += newsContent;
					}
					
					content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
							parseToHtml(videoUrl), "text/html", "utf-8", null);
				} else {
					if (newsContent != null
							&& StringUtil.isNotEmpty(newsContent)) {
						content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
								parseToHtml(newsContent), "text/html", "utf-8",
								null);
					}
				}
			} else {
				showNoResultPrompt();
			}
		} else {
			showNoResultPrompt();
		}
		}
	}

	private void showNoResultPrompt() {
		Toast.makeText(getActivity(), "没有找到新闻内容", Toast.LENGTH_LONG).show();
	}

	private void getVideoUrl(String id) {
		String url = Constants.GET_VIDEO + id;
		ThreadManager.exeTask(this, 0, null, url);
	}

	private String parseToHtml(String bodyContent) {
		StringBuilder sb = new StringBuilder();
		String begin = StringUtil.readFromFile(getActivity(), "begin.html");
		String end = StringUtil.readFromFile(getActivity(), "end.html");
		sb.append(begin).append(bodyContent).append(end);
		// sb.append("<html><body>").append(bodyContent).append("</body></html>");
		return sb.toString();
	}
}
