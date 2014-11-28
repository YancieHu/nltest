package com.tidemedia.nntv.sliding.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tidemedia.nntv.R;
import com.tidemedia.nntv.activity.PlayerActivity;
import com.tidemedia.nntv.common.Constants;
import com.tidemedia.nntv.model.DiscloseDetailModel;
import com.tidemedia.nntv.model.NewsModel;
import com.tidemedia.nntv.model.Video;
import com.tidemedia.nntv.model.DiscloseDetailModel.DisClose;
import com.tidemedia.nntv.net.ThreadManager;
import com.tidemedia.nntv.sliding.fragment.NewsFragment.JavaScriptInterface;
import com.tidemedia.nntv.util.AsyncImageLoader;
import com.tidemedia.nntv.util.StringUtil;
import com.tidemedia.nntv.util.ValidateUtil;
import com.tidemedia.nntv.util.AsyncImageLoader.ImageCallback;

public class DiscloseDetailFragment extends BaseFragment {
	private static final String TAG = "DiscloseDetailFragment";
	private NewsModel news;
	private TextView title;
	private TextView time;
	// private TextView content;
	private WebView content;
	private String dContent;
	private LinearLayout loadingView;
	private String id;
	private Context context;
	private static final String VIDEO_CONFIG = "<video onclick=\"playvideo(event, '%@')\" width=\"300\" height=\"225\" controls=\"controls\" src=\"%@\" style=\"margin:14px auto; display:block;\"></video>";

	public DiscloseDetailFragment(String id) {
		this.id = id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_layout, container,
				false);
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
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getDiscloseDetail();
	}
	
	public class JavaScriptInterface {

        public void startVideo(String videoAddress){     
        	Intent intent = new Intent(getActivity(), PlayerActivity.class);
			intent.putExtra("liveUrl", videoAddress);
			startActivity(intent);
        }
	}

	private void getDiscloseDetail() {
		String url = Constants.GET_DISCLOSE_DETAIL + "?id=" + id;
		Log.i(TAG, "url : " + url);
		ThreadManager.exeTask(this, 0, null, url);
	}
	
	private void getVideoUrl(String id) {
		String url = Constants.GET_VIDEO + id;
		ThreadManager.exeTask(this, 1, null, url);
	}

	@Override
	public void onCallbackFromThread(String resultData, int taskId) {
		if (isDetached()) {
			return;
		}
		if(taskId == 1){
			if(StringUtil.isNotEmpty(resultData)){
				loadingView.setVisibility(View.GONE);
				if (StringUtil.isNotEmpty(resultData)) {
					Video video = StringUtil.fromJson(resultData, Video.class);
					if (null != video) {
						String url = video.getUrl();
						if (StringUtil.isNotEmpty(url)) {
							String videoUrl = VIDEO_CONFIG.replaceAll("%@", url);
							if (dContent != null
									&& StringUtil.isNotEmpty(dContent)) {
								videoUrl += dContent;
							}
							content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
									parseToHtml(videoUrl), "text/html", "utf-8", null);
						} else {
							if (dContent != null
									&& StringUtil.isNotEmpty(dContent)) {
								content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
										parseToHtml(dContent), "text/html", "utf-8",
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
		}else{
			DiscloseDetailModel json = StringUtil.fromJson(resultData, DiscloseDetailModel.class);
			DisClose disclose = json.result.get(0);
			if (disclose != null) {
				title.setText(disclose.uname);
				time.setText(disclose.time);
				dContent = disclose.content;
				if (!disclose.is_video) {
					content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
					if (dContent != null && dContent.length() > 0) {
						content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
								parseToHtml(disclose.content), "text/html",
								"utf-8", null);
					}
				} else {
					String videoUrl = disclose.video_url;
					if (StringUtil.isNotEmpty(videoUrl)) {
						if (ValidateUtil.isInteger(videoUrl)) {
							getVideoUrl(videoUrl);
						} else {
							String url = VIDEO_CONFIG.replaceAll("%@", videoUrl);
							if (dContent != null && StringUtil.isNotEmpty(dContent)) {
								url += dContent;
							}
							content.loadDataWithBaseURL(Constants.GET_HOME_DETAIL,
									parseToHtml(url), "text/html", "utf-8", null);
						}
					}
				}
			}
		}
		/*Log.i(TAG, "result : " + resultData);
		loadingBar.setVisibility(View.GONE);
		if (StringUtil.isNotEmpty(resultData)) {
			DiscloseDetailModel json = StringUtil.fromJson(resultData, DiscloseDetailModel.class);
			if (json != null && json.result != null && json.result.size() != 0) {
				DisClose disclose = json.result.get(0);
				title.setText(disclose.uname);
				time.setText(disclose.time);
				content.setText(disclose.content);
				if (disclose.is_video) {
					webView.setVisibility(View.VISIBLE);
					webView.loadDataWithBaseURL(Constants.GET_HOME_DETAIL, parseToHtml(VIDEO_CONFIG.replaceAll("%@", disclose.video_url)), "text/html", "utf-8", null);
				} else {
					imageView.setVisibility(View.VISIBLE);
					Bitmap bmp = AsyncImageLoader.getInstance().loadDrawable(
							disclose.image_url, new ImageCallback() {

								@Override
								public void imageLoaded(Bitmap imageDrawable,
										String imageUrl) {
									if (imageDrawable != null) {
										imageView.setImageBitmap(imageDrawable);
									}
								}
							});
					if (bmp != null) {
						imageView.setImageBitmap(bmp);
					} else {
						imageView.setImageResource(0);
					}
				}
			}
		}*/
	}
	
	private void showNoResultPrompt() {
		Toast.makeText(getActivity(), "没有找到内容", Toast.LENGTH_LONG).show();
	}
	
	private String parseToHtml(String bodyContent) {
		StringBuilder sb = new StringBuilder();
		String begin = StringUtil.readFromFile(getActivity(), "begin.html");
		String end = StringUtil.readFromFile(getActivity(), "end.html");
		sb.append(begin).append(bodyContent).append(end);
		return sb.toString();
	}

}
