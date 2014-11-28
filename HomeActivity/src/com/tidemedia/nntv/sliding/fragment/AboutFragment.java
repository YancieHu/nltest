package com.tidemedia.nntv.sliding.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class AboutFragment extends BaseFragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		WebView webView = new WebView(getActivity());
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.loadUrl(com.tidemedia.nntv.common.Constants.ABOUT);
		return webView;
	};
}
