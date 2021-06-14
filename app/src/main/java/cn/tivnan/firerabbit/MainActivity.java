package cn.tivnan.firerabbit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import cn.tivnan.firerabbit.controller.BookmarkController;
import cn.tivnan.firerabbit.view.BookmarkActivity;

public class MainActivity extends AppCompatActivity {


    //webView所加载的主页链接
    private final static String HOME_URL = "https://cn.bing.com";

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 设置主界面各控件功能
     */
    private void initView() {

        //核心的webView，网页内容在此呈现，打开App先加载主页
        WebView webView = findViewById(R.id.webview);
        initWebView(webView);

        //返回按钮，返回后一个网页
        findViewById(R.id.buttonBack).setOnClickListener(v -> {
            //没有前一个网页则无反应
            if (webView.canGoBack()) {
                webView.goBack();  //返回后一个页面
            }
        });

        //前进按钮，前进到前一个网页
        findViewById(R.id.buttonForeward).setOnClickListener(v -> {
            //没有前一个网页则无反应
            if (webView.canGoForward()) {
                webView.goForward();  //前进前一个页面
            }
        });

        //主页按钮，返回主页
        findViewById(R.id.buttonHome).setOnClickListener(v -> webView.loadUrl(HOME_URL));

        //增加书签按钮，把当前页面制作成书签存储起来
        findViewById(R.id.buttonAddBookMark).setOnClickListener(v -> {
            String url = webView.getUrl();
            String title = webView.getTitle();
            addBookmarkDialog(MainActivity.this, title, url);
        });

        //书签按钮，跳转到书签界面
        findViewById(R.id.buttonBookMark).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
            startActivity(intent);
        });
    }

    private void initWebView(WebView webView) {


        WebViewClient webClient = new WebViewClient() {
            // override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            //     // return false
            // }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    webView.loadUrl(url);
                    return true;
                } else {
                    // val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    // startActivity(intent)

                }
                return true;
            }
        };


        //下面这些直接复制就好
        webView.setWebViewClient(webClient);

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);   // 开启 JavaScript 交互
        webSettings.setAppCacheEnabled(true); // 启用或禁用缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 只要缓存可用就加载缓存, 哪怕已经过期失效 如果缓存不可用就从网络上加载数据
        webSettings.setAppCachePath(getCacheDir().getPath()); // 设置应用缓存路径

        // 缩放操作
        webSettings.setSupportZoom(false); // 支持缩放 默认为true 是下面那个的前提
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);// 隐藏原生的缩放控件

        webSettings.setBlockNetworkImage(true); // 禁止或允许WebView从网络上加载图片
        webSettings.setLoadsImagesAutomatically(true);// 支持自动加载图片

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(true); // 是否开启安全模式
        }


        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// 支持通过JS打开新窗口
        webSettings.setDomStorageEnabled(true);// 启用或禁用DOM缓存
        webSettings.setSupportMultipleWindows(true);// 设置WebView是否支持多窗口
        webSettings.setUseWideViewPort(true);// 将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        webSettings.setAllowFileAccess(true);// 设置可以访问文件
        webSettings.setGeolocationEnabled(true);// 是否使用地理位置

        webView.setFitsSystemWindows(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.loadUrl(HOME_URL);
    }

    /**
     * 确认添加标签的对话框
     *
     * @param title
     * @param url
     */
    private void addBookmarkDialog(Context context, String title, String url) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(url)
                .setCancelable(true);
        normalDialog.setNegativeButton("关闭", null);
        normalDialog.setPositiveButton("确定", (dialog, which) -> {
            new BookmarkController(context).addBookmark(title, url);
            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
        });
        normalDialog.show();
    }


    /**
     * 设置返回键
     * 如果页面可以继续后退则后退，否则退出应用
     *
     * @param keyCode 事件code
     * @param event   事件
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = findViewById(R.id.webview);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack(); //返回上一个页面
                return true;
            } else {
                finish(); //退出应用
                return true;
            }
        }
        return false;
    }
}
