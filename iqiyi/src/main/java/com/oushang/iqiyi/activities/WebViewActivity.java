package com.oushang.iqiyi.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.utils.StatusBarUtil;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_base.base.mvp.view.BaseActivityMVP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Author: zeelang
 * @Description: 加载网页的activity
 * @Time: 2021/8/9 18:27
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_WEBVIEW)
public class WebViewActivity extends BaseActivityMVP {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private String mUrl;

    @BindView(R.id.web_view)
    WebView mWebView;

    @BindView(R.id.web_title)
    TextView mWebTitle;

    @Override
    protected int setLayout() {
        return R.layout.activity_webview;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @OnClick(R.id.webview_back)
    public void onClickBack() {
        finish();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        StatusBarUtil.hideStatusBar(this);
        Intent intent  = getIntent();
        int themeMode = Settings.System.getInt(getContentResolver(), "show_mode", 0);
        Log.d(TAG, "themeMode:" + themeMode);

        mUrl = intent.getStringExtra(Constant.WEB_VIEW_URL);

        if (mUrl.equals(Constant.AGREEMENT_URL)) {
            mWebTitle.setText(getString(R.string.user_agreement_title));
        } else if (mUrl.equals(Constant.PRIVACY_URL)) {
            mWebTitle.setText(getString(R.string.privacy_protection_title));
        }

        mWebView.setBackgroundColor(0); // 设置背景色

        mWebView.setVisibility(View.INVISIBLE);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                Log.d(TAG, "uri:" + uri.toString());
                view.loadUrl(uri.toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished:" + themeMode);
                String cssPath = themeMode == 0?"service.css":"white.css";
                Log.d(TAG, "cssPath:" + cssPath);

                view.loadUrl("javascript:loadStyles('" + cssPath + "')");
                if(null != mWebView) mWebView.setVisibility(View.VISIBLE);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });
        mWebView.setBackgroundColor(0);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(true);

        String fileName = "";
        if (mUrl.equals(Constant.AGREEMENT_URL)) {
            fileName = "service.html";
        } else if (mUrl.equals(Constant.PRIVACY_URL)) {
            fileName = "privacy.html";
        }

        AssetManager assetManager = getAssets();
        try(InputStream inputStream = assetManager.open(fileName, AssetManager.ACCESS_BUFFER)) {
            String html = streamToString(inputStream);
            mWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String streamToString(InputStream inputStream) {
        if (inputStream == null) return "";
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try(Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            int n;
            while((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }


}
