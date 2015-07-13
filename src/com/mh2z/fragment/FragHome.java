package com.mh2z.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mh2z.activity.R;

public class FragHome extends Fragment {
	private View view;
	private WebView webview;
	private String devbaseURL = "http://192.168.1.106/HDWiki/index.php";
	private String proBaseURL = "http://mhbb.mhedu.sh.cn:8080/hdwiki/index.php";
	private List<Cookie> cookies;

	@SuppressLint({ "InflateParams", "SetJavaScriptEnabled" }) @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.frag_home, null);
		webview = (WebView) view.findViewById(R.id.webView1_home);
		cookies = new ArrayList<Cookie>();
		WebSettings ws = webview.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		ws.setSupportZoom(true);
		ws.setBuiltInZoomControls(true);
		ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(true);

	

		// ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		webview.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if ((arg1 == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
					webview.goBack();
					return true;
				}
				return false;
			}
		});

		setCookie(getActivity().getApplicationContext());
		webview.loadUrl(devbaseURL+"?app-default");
		return view;
	}


	public void visitDoc(int did) {

		webview.loadUrl(devbaseURL + "?app-docview-"
				+ did);

	}

	public void visitCate(int cid) {

		webview.loadUrl(devbaseURL+ "?category-view-"
				+ cid);

	}

	

	

	public void getCookies(List<Cookie> cookies) {
		this.cookies = cookies;
		setCookie(getActivity().getApplicationContext());
	}

	@SuppressWarnings("deprecation")
	private void setCookie(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		cookieManager.setAcceptCookie(true);
		for (int i = 0; i < cookies.size(); i++) {
			Cookie cookie = cookies.get(i);
			String cookieName = cookie.getName();
			String cookieValue = cookie.getValue();
			String domainString = cookie.getDomain();
//			Log.i("FRAGHOME",cookieName);
//			Log.i("FRAGHOME",cookieValue);
//			Log.i("FRAGHOME",domainString);
			if (!cookieName.isEmpty() && !cookieValue.isEmpty() && !domainString.isEmpty()) {
				cookieManager.setCookie(domainString,cookieName + "=" + cookieValue);
			}		
		}

//		cookieManager.setCookie(baseURL, "uid=123");
		CookieSyncManager.getInstance().sync();
		if (cookieManager.getCookie(devbaseURL) != null) {
			System.out.println(cookieManager.getCookie(devbaseURL));
		}
//		webview.loadUrl(baseURL+"?app-get_top_cate");
//		System.out.println(cookieManager.getCookie(baseURL));
	}
	
	@SuppressWarnings("deprecation")
	public void clearCookies(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}
}
