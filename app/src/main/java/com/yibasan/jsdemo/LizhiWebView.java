package com.yibasan.jsdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.URI;

public class LizhiWebView extends FrameLayout {
    public LizhiWebView(@NonNull Context context) {
        super(context);
        init();
    }

    public LizhiWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LizhiWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        initView();
    }

    WebView webView;
    private static SoftReference<String> mJsCache;
    private String mLizhiJs;

    private void initView() {
        webView = new WebView(getContext().getApplicationContext());
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        //解决一些图片加载问题
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("yqy", "shouldOverrideUrlLoading url: " + url);
                view.loadUrl(url);
                return true;
            }
        });
        webView.addJavascriptInterface(new LizhiJsInterface(), "JsBridge");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("yqy", "shouldOverrideUrlLoading url=" + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.e("yqy", "shouldOverrideUrlLoading request=" + request.toString());
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mLizhiJs = getLizhiJs();
                Log.e("yqy", "onPageFinished mLizhiJs=" + mLizhiJs);
                webView.evaluateJavascript(mLizhiJs, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.e("yqy", "onReceiveValue=" + s);
                    }
                });
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("yqy", "onPageStarted url=" + url);
            }
        });
        addView(webView);
    }


    private String getLizhiJs() {
        String lizhiJs = null;
        InputStream is = null;

        if (mJsCache != null) {
            lizhiJs = mJsCache.get();

            if (!TextUtils.isEmpty(lizhiJs)) {
                Log.e("TAG", "%s getLizhiJs use cache jsbridge");
                return lizhiJs;
            }
        }

        try {
            is = getContext().getAssets().open("bridge.js");
            lizhiJs = "javascript:" + toString(is);
        } catch (IOException e) {
            e.printStackTrace();
//            MobclickAgent.reportError(getContext(), e);
        } catch (OutOfMemoryError error) {
            if (is != null) {
                try {
                    is.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        mJsCache = new SoftReference<>(lizhiJs);
        return lizhiJs;
    }


    public static String toString(InputStream inputstream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }

    public void destroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.setTag(null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }

    public void loadUrl(String s) {
        webView.loadUrl(s);
    }


    @SuppressLint("JavascriptInterface")
    private class LizhiJsInterface {

        @JavascriptInterface
        public void closeWebView() {
            Log.e("yqy", "closeWebView");
        }

        @JavascriptInterface
        public void reportEvent(String result) {
            Log.e("yqy", "reportEvent:result=" + result);
        }

        @JavascriptInterface
        public String getUserSession() {
            Log.e("yqy", "getUserSession");
            return "@JavascriptInterface";
        }

        @JavascriptInterface
        public String getMakeRecord() {
            Log.e("yqy", "getMakeRecord");
            return "getMakeRecord";
        }

        @JavascriptInterface
        public void onMakeRecord(String result) {
            Log.e("yqy", "onMakeRecord:result=" + result);
        }

        @JavascriptInterface
        public void log(String msg) {
            Log.e("yqy", "log:msg=" + msg);
        }
    }
}
