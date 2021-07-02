package cn.tivnan.firerabbit;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.tivnan.firerabbit.controller.BookmarkController;
import cn.tivnan.firerabbit.controller.HistoryController;
import cn.tivnan.firerabbit.view.BookmarkActivity;
import cn.tivnan.firerabbit.view.HistoryActivity;
import cn.tivnan.firerabbit.view.LoginOrRegisterActivity;
import cn.tivnan.firerabbit.view.UserActivity;

public class MainActivity extends AppCompatActivity {


    //webView所加载的主页链接
    private final static String HOME_URL = "file:///android_asset/web/mainpage.html";
    private WebView webView;
    private String URL_NOW;
    private EditText topTitle;
    private boolean invisibleMod, nightMod;
    private String css;
    private AlphaAnimation black500ms;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initNightMod();
        initView();
    }


    /**
     * 设置主界面各控件功能
     */
    private void initView() {

        //核心的webView，网页内容在此呈现，打开App先加载主页
        webView = findViewById(R.id.webview);
        initWebView(webView);
        //控件和变量初始化
        topTitle = findViewById(R.id.url);
        findViewById(R.id.menu2).setVisibility(View.INVISIBLE);
        findViewById(R.id.menu3).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonLess).setVisibility(View.GONE);
        invisibleMod = false;
        nightMod = false;

        //返回按钮，返回后一个网页
        findViewById(R.id.buttonBack).setOnClickListener(v -> {
            //没有前一个网页则无反应
            if (webView.canGoBack()) {
                webView.goBack();  //返回后一个页面
            }
        });

        //前进按钮，前进到前一个网页
        findViewById(R.id.buttonForward).setOnClickListener(v -> {
            //没有前一个网页则无反应
            if (webView.canGoForward()) {
                webView.goForward();  //前进前一个页面
            }
        });

        //添加书签按钮，将当前页面添加到书签
        findViewById(R.id.buttonAddMark).setOnClickListener(v -> {
            if(webView.getUrl().equals(HOME_URL))
                return;
            //增加书签按钮，把当前页面制作成书签存储起来
            String url = webView.getUrl();
            String title = webView.getTitle();
            int id = (int)(System.currentTimeMillis()%1000000000);//取当前时间作为id
            addBookmarkDialog(MainActivity.this, id, title, url);
        });

        //书签按钮，跳转到书签界面
        findViewById(R.id.buttonMarks).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, BookmarkActivity.class);
            startActivityForResult(intent, 1);
        });

        //历史记录按钮，跳转到历史记录界面
        findViewById(R.id.buttonHistory).setOnClickListener(v -> {
            //历史记录按钮
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, HistoryActivity.class);
            startActivityForResult(intent, 2);
        });

        //用户按钮，跳转到用户界面
        findViewById(R.id.buttonUser).setOnClickListener(v ->{
            //用户按钮
            //根据userInfo的存在与否判断登录状态，若存在，则为已登录，若不存在，则为未登录
            File file = new File("/data/data/" + getPackageName() + "/shared_prefs/userInfo.xml");
            if (file.exists()) {
                Intent intent = new Intent(this, UserActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginOrRegisterActivity.class);
                startActivity(intent);
            }

        });

        //主页按钮，返回主页
        findViewById(R.id.buttonHome).setOnClickListener(v -> webView.loadUrl(HOME_URL));

        //导航栏展开按钮，展开导航栏
        findViewById(R.id.buttonMore).setOnClickListener(v ->{//实际处理button的click事件的方法
            menuUnfold();
        });

        //导航栏折叠按钮，折叠导航栏
        findViewById(R.id.buttonLess).setOnClickListener(v ->{//实际处理button的click事件的方法
            menuFold();
        });

        //退出按钮，退出浏览器
        findViewById(R.id.buttonQuit).setOnClickListener(v ->{
            System.exit(0);
        });

        //顶部网址栏（搜索框）点击事件
        topTitle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!topTitle.getText().toString().equals(webView.getUrl()))
                    return;
                topTitle.setText(URL_NOW); //添加这句后实现效果
                Spannable content = topTitle.getText();
                Selection.selectAll(content);
            }
        });

        //顶部网址栏聚焦和失焦事件
        topTitle.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    topTitle.setText(URL_NOW);
                } else {
                    topTitle.setText(webView.getTitle());
                }
            }
        });

        //顶部刷新按钮，点击刷新网页
        findViewById(R.id.buttonRefresh).setOnClickListener(v -> {
            webView.reload();
        });

        //顶部跳转按钮，跳转到目标链接或搜索
        findViewById(R.id.buttonGoto).setOnClickListener(v -> {
            String url = topTitle.getText().toString();
            if(url.equals(webView.getTitle()))
                webView.reload();
            else if(url.startsWith("http://") || url.startsWith("https://"))
                webView.loadUrl(url);
            else if (URLUtil.isNetworkUrl("http://"+url)&&URLUtil.isValidUrl("http://"+url))
                webView.loadUrl("http://"+url);
            else if (URLUtil.isNetworkUrl("https://"+url)&&URLUtil.isValidUrl("https://"+url))
                webView.loadUrl("https://"+url);
            else
                webView.loadUrl("https://cn.bing.com/search?q=" + url);
        });

        //无痕模式按钮，开启无痕模式
        findViewById(R.id.buttonInvisibleMod).setOnClickListener(v -> {
            invisibleMod = true;
            findViewById(R.id.buttonVisibleMod).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonInvisibleMod).setVisibility(View.GONE);
            createDialog("您已进入无痕模式");
        });

        //无痕模式按钮，取消无痕模式
        findViewById(R.id.buttonVisibleMod).setOnClickListener(v -> {
            invisibleMod = false;
            findViewById(R.id.buttonVisibleMod).setVisibility(View.GONE);
            findViewById(R.id.buttonInvisibleMod).setVisibility(View.VISIBLE);
            createDialog("您已退出无痕模式");
        });

        //分享按钮，将当前页面链接复制到剪贴板
        findViewById(R.id.buttonShare).setOnClickListener(v ->{
            if(webView.getUrl().equals("file:///android_asset/web/mainpage.html"))
                return;
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("urlCopied", webView.getUrl());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            createDialog("已将当前页面链接复制到剪贴板");
        });

        //设置文件下载监听器
        webView.setDownloadListener(new DownloadListener(){
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                //添加到下载记录;
            }
        });

        //文件下载管理按钮，打开手机系统的下载管理
        findViewById(R.id.buttonDownload).setOnClickListener(v -> {
            // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
            PackageInfo packageinfo = null;
            try {
                packageinfo = getPackageManager().getPackageInfo("com.android.providers.downloads.ui", 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageinfo == null) {
                //BDToast.showToast(getText(R.string.app_not_found).toString());
                Toast.makeText(getApplicationContext(), "无法打开 下载管理", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent resolveIntent = getPackageManager().getLaunchIntentForPackage("com.android.providers.downloads.ui");// 这里的packname就是从上面得到的目标apk的包名
            startActivity(resolveIntent);// 启动目标应用
        });

        //夜间模式按钮，开启夜间模式
        findViewById(R.id.buttonNightMod).setOnClickListener( v -> {
            nightMod = true;
            createDialog("您已进入夜间模式");
            nightModSwitch();
            webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" + "var style = document.createElement('style');" + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + css + "');" + "parent.appendChild(style)" + "})();");
        });

        //夜间模式按钮，关闭夜间模式
        findViewById(R.id.buttonDayMod).setOnClickListener( v -> {
            nightMod = false;
            createDialog("您已退出夜间模式");
            nightModSwitch();
        });

        findViewById(R.id.main).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int preHeight = 0;
            @Override
            public void onGlobalLayout() {
                int heightDiff = findViewById(R.id.main).getRootView().getHeight() - findViewById(R.id.main).getHeight();
                System.out.println("height differ = " + heightDiff);
                //在数据相同时，减少发送重复消息。因为实际上在输入法出现时会多次调用这个onGlobalLayout方法。
                if (preHeight == heightDiff) {
                    return;
                }
                preHeight = heightDiff;
                menuFold();
            }
        });

    }



    private void initWebView(WebView webView) {

        WebViewClient webClient = new WebViewClient() {

            boolean if_load;

            // override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            //     // return false
            // }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    // webView.loadDataWithBaseURL("file:///android_asset/web", html, "text/html", "UTF-8", null);

                    if_load=false;
                    webView.loadUrl(url);
                    return true;
                } else {
                    // val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    // startActivity(intent)

                }
                return true;
            }

            //页面完成即加入历史记录
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(nightMod){
                    webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" + "var style = document.createElement('style');" + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + css + "');" + "parent.appendChild(style)" + "})();");
                    findViewById(R.id.nightGlasses).startAnimation(black500ms);
                }

                findViewById(R.id.nightGlasses).setVisibility(View.INVISIBLE);
                if (if_load) {
                    if(webView.getUrl().equals("file:///android_asset/web/mainpage.html")){
                        URL_NOW = "";
                        topTitle.setText("欢迎使用FireRabbit！");
                        return;
                    }
                    URL_NOW = webView.getUrl();
                    topTitle.setText(view.getTitle());
                    if(invisibleMod)
                        return;
                    new HistoryController(MainActivity.this).addHistory(view.getTitle(), view.getUrl());
                    if_load = false;
                }
            }

            //页面开始
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                topTitle.setText(view.getUrl());

                if(nightMod)
                    findViewById(R.id.nightGlasses).setVisibility(View.VISIBLE);

                if_load = true;
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

        webSettings.setBlockNetworkImage(false); // 禁止或允许WebView从网络上加载图片
        webSettings.setLoadsImagesAutomatically(true);// 支持自动加载图片

        //允许访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);

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
    private void addBookmarkDialog(Context context,int id, String title, String url) {
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(url)
                .setCancelable(true);
        normalDialog.setNegativeButton("关闭", null);
        normalDialog.setPositiveButton("确定", (dialog, which) -> {
            new BookmarkController(context).addBookmark(id, title, url);
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

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: //接受书签url并且返回
                if (resultCode == RESULT_OK) {
                    String url = data.getStringExtra("url");
                    webView.loadUrl(url);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    String url = data.getStringExtra("url");
                    webView.loadUrl(url);
                }
                break;
        }
    }

    //弹出一条附带消息的对话框
    private void createDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(message);
        dialog.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }
        );
        dialog.show();
    }

    //折叠导航栏，相关UI切换
    private void menuFold(){
        findViewById(R.id.menu3).setVisibility(View.INVISIBLE);
        findViewById(R.id.menu2).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonBack).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonForward).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonHome).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonPages).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonLess).setVisibility(View.GONE);
        findViewById(R.id.buttonMore).setVisibility(View.VISIBLE);
    }

    //展开导航栏，相关UI切换
    private void menuUnfold(){
        findViewById(R.id.buttonBack).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonForward).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonHome).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonPages).setVisibility(View.INVISIBLE);
        findViewById(R.id.menu2).setVisibility(View.VISIBLE);
        findViewById(R.id.menu3).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonMore).setVisibility(View.GONE);
        findViewById(R.id.buttonLess).setVisibility(View.VISIBLE);
        if(invisibleMod){
            findViewById(R.id.buttonVisibleMod).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonInvisibleMod).setVisibility(View.GONE);
        }else{
            findViewById(R.id.buttonVisibleMod).setVisibility(View.GONE);
            findViewById(R.id.buttonInvisibleMod).setVisibility(View.VISIBLE);
        }

        if(nightMod){
            findViewById(R.id.buttonNightMod).setVisibility(View.GONE);
            findViewById(R.id.buttonDayMod).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.buttonNightMod).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonDayMod).setVisibility(View.GONE);
        }
    }

    //夜间模式相关组件初始化，使用css注入实现网页页面的夜间模式
    //css注入代码：
    //webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" + "var style = document.createElement('style');" + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + css + "');" + "parent.appendChild(style)" + "})();");
    private void initNightMod(){
        InputStream in = getResources().openRawResource(R.raw.night);
        byte[] buffer = new byte[0];
        try {
            buffer = new byte[in.available()];
            in.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        css = Base64.encodeToString(buffer, Base64.NO_WRAP);
        black500ms = new AlphaAnimation(1, 1);
        black500ms.setDuration(500);
    }

    //夜间模式，相关UI切换
    private void nightModSwitch(){
        if(nightMod){
            findViewById(R.id.buttonDayMod).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonNightMod).setVisibility(View.GONE);

            findViewById(R.id.buttonAddMark).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonMarks).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonHistory).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonDownload).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonVisibleMod).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonInvisibleMod).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.menu3).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonShare).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonNightMod).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonDayMod).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonNothing).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonQuit).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonUser).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.menu2).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonBack).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonForward).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonHome).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonPages).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonMore).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonLess).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.menu1).setBackgroundColor(Color.parseColor("#000000"));
            webView.setBackgroundColor(Color.parseColor("#202020"));

            findViewById(R.id.topLayout).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonRefresh).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.url).setBackgroundColor(Color.parseColor("#000000"));
            findViewById(R.id.buttonGoto).setBackgroundColor(Color.parseColor("#000000"));
            topTitle.setTextColor(Color.parseColor("#646464"));
        }else{
            findViewById(R.id.buttonDayMod).setVisibility(View.GONE);
            findViewById(R.id.buttonNightMod).setVisibility(View.VISIBLE);

            findViewById(R.id.buttonAddMark).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonMarks).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonHistory).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonDownload).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonVisibleMod).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonInvisibleMod).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.menu3).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonShare).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonNightMod).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonDayMod).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonNothing).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonQuit).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonUser).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.menu2).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonBack).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonForward).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonHome).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonPages).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonMore).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonLess).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.menu1).setBackgroundColor(Color.parseColor("#FFFFFF"));
            webView.setBackgroundColor(Color.parseColor("#FFFFFF"));

            findViewById(R.id.topLayout).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonRefresh).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.url).setBackgroundColor(Color.parseColor("#FFFFFF"));
            findViewById(R.id.buttonGoto).setBackgroundColor(Color.parseColor("#FFFFFF"));
            topTitle.setTextColor(Color.parseColor("#000000"));
        }
    }

}
