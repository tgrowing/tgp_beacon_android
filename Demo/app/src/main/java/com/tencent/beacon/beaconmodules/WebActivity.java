package com.tencent.beacon.beaconmodules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;
import com.tencent.beacon.event.open.BeaconJsReport;
import com.tencent.beacon.event.open.BeaconWebChromeClient;

public class WebActivity extends Activity {

    private static final String TAG = "WebActivity";

    private EditText mEditText;
    private String mUrl = "https://beacon.qq.com/";
    private WebView mWebView;
    private long exitTime = 0;
    private BeaconJsReport mBeaconJsReport;

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mEditText = findViewById(R.id.et_url);

        mWebView = (WebView) findViewById(R.id.myWebView);
        mWebView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
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

        // 开启内嵌H5通过App上报埋点的通路
        mBeaconJsReport = new BeaconJsReport();
        mBeaconJsReport.enableBridge(mWebView);

        // 注意：若 webview 需要setWebChromeClient，则需实现继承自BeaconWebChromeClient的WebChromeClient，并在enableBridge时传入，如下所示
        /*MyWebChromeClient myWebChromeClient = new MyWebChromeClient();
        mWebView.setWebChromeClient(myWebChromeClient);
        mBeaconJsReport.enableBridge(mWebView, myWebChromeClient);*/

        WebSettings webSettings = mWebView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 允许访问文件
        mWebView.getSettings().setAllowFileAccess(true);
        // 打开本地缓存提供JS调用
        mWebView.getSettings().setDomStorageEnabled(true);

        // 获取到UserAgentString
        String userAgent = webSettings.getUserAgentString();
        // 自定义标记:isApp
        webSettings.setUserAgentString(userAgent + " isApp");

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL); //支持内容重新布局

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);  //提高渲染的优先级
        webSettings.setStandardFontFamily("");//设置 WebView 的字体，默认字体为 "sans-serif"
        webSettings.setDefaultFontSize(20);//设置 WebView 字体的大小，默认大小为 16
        webSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8

        mEditText.setText(mUrl);
        //设置网址
        mWebView.loadUrl(mUrl);

    }

    public class MyWebChromeClient extends BeaconWebChromeClient {

        private static final String TAG = "MyWebChromeClient";

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.i(TAG, "onConsoleMessage:" + consoleMessage.message());
            // 注意：这里如果 return true 拦截了，SDK 将不会处理h5传到app端的消息。若需使用 app 端和 h5 的通路，请保持不拦截
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                JsPromptResult result) {
            Log.i(TAG, "onJsPrompt url:" + url + "， message：" + message + "， defaultValue：" + defaultValue);
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
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
        if (mBeaconJsReport != null) {
            mBeaconJsReport.disableBridge();
        }
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
