package com.yibasan.jsdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    LizhiWebView webView;
    Button btn_load;
    private FrameLayout frameLayout;

    MyDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_load = findViewById(R.id.btn_load);
        frameLayout = findViewById(R.id.xwalk_view);
        if (dialog == null) {
            dialog = new MyDialog(this);
        }

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog.show();
                Log.e("yqy", "setOnClickListener");
                //webView.loadUrl("javascript:LZGLJSBridge.on('heihei','bbbcccdd')");
                //webView.loadUrl("javascript:LZGLJSBridge.on('heihei','bbbcccdd')");

                //String js = "(function() { var event = new CustomEvent(\'LZGLNativeCallback\', {\"detail\":{\"ret\":{\"param2\":\"value2\",\"param1\":\"value1\"},\"id\":0}}); document.dispatchEvent(event)}());";
                String js = "LZGLJSBridge.on('heihei','bbbcccdd')";
                Log.e("yqy", "js=" + js);
                //webView.loadUrl(js);
                webView.evaluateJavascript(js, new ValueCallback() {
                    @Override
                    public void onReceiveValue(Object o) {
                        Log.i("yqy", "onReceiveValue o=" + o);
                    }
                });
                //webView.loadUrl("javascript:LZGLJSBridge.on('name',test)");
                //webView.loadUrl("javascript:callH5('Android给H5传递的参数')");
                //webView.loadUrl("javascript:javaCallJs(" + "'test'"+")");
            }
        });

        initView();
    }

    private void initView() {
        webView = new LizhiWebView(this);
        frameLayout.addView(webView);
        //webView.loadUrl("https://www.jianshu.com/p/e8eeec4e8d71");
        webView.loadUrl("file:///android_asset/index.html");
        //webView.loadUrl("file:///android_asset/JavaAndJavaScriptCall.html");
    }


    //监听BACK按键，有可以返回的页面时返回页面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }


}
