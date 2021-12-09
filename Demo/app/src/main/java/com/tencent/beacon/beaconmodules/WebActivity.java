package com.tencent.beacon.beaconmodules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class WebActivity extends Activity {

    private static final String TAG = "WebActivity";

    private EditText mEditText;
    private String mUrl = "https://beacon.qq.com/";
    private WebView mWebView;
    private long exitTime = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mEditText = findViewById(R.id.et_url);

        mWebView = (WebView) findViewById(R.id.myWebView);
        mWebView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        mEditText.setText(mUrl);
        //设置网址
        mWebView.loadUrl(mUrl);
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    loadInputUrl();
                }
                return false;
            }
        });

        mWebView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = mWebView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL); //支持内容重新布局

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setTextZoom(2);//设置文本的缩放倍数，默认为 100
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);  //提高渲染的优先级
        webSettings.setStandardFontFamily("");//设置 WebView 的字体，默认字体为 "sans-serif"
        webSettings.setDefaultFontSize(20);//设置 WebView 字体的大小，默认大小为 16
        webSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
    }


    //我们需要重写回退按钮的时间,当用户点击回退按钮：
    //1.webView.canGoBack()判断网页是否能后退,可以则goback()
    //2.如果不可以连续点击两次退出App,否则弹出提示Toast
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次返回退出",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //super.onBackPressed();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void btnGoClicked(View view) {
        loadInputUrl();
    }

    public void loadInputUrl() {
        String url = mEditText.getText().toString();
        Log.i(TAG, "loadInputUrl:" + url);
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(getApplicationContext(), "请输入 url",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //设置网址
        mWebView.loadUrl(url);
    }

}
