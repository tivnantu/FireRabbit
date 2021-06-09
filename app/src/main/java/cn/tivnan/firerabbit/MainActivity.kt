package cn.tivnan.firerabbit

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.tivnan.firerabbit.dao.BookmarkDataHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var webView: WebView? = null

    var WEB_URL = "https://cn.bing.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        initWebView()
        buttonBack.setOnClickListener {
            if (webView!!.canGoBack()) {
                webView!!.goBack()  //返回上一个页面
            } else {
                finish()
            }
        }
        buttonForeward.setOnClickListener {
            if (webView!!.canGoForward()) {
                webView!!.goForward()  //返回上一个页面
            }
        }
        buttonHome.setOnClickListener {
            webView!!.loadUrl(WEB_URL)
        }
        buttonAddBookMark.setOnClickListener {
            addBookmarkDialog(webView!!.title, webView!!.url)
        }

        buttonBookMark.setOnClickListener {
            val intent = Intent(this@MainActivity, Boookmark::class.java)
            startActivity(intent)
        }
    }

    fun addBookmarkDialog(title: String, url: String) {
        val normalDialog = AlertDialog.Builder(this)
        normalDialog.setTitle(title)
        normalDialog.setMessage(url)
        normalDialog.setCancelable(true)
        normalDialog.setPositiveButton("确定"
        ) { dialog, which ->
            addBookmark(title,url)
        }
        normalDialog.setNegativeButton("关闭",null)
        normalDialog.show();
    }


    //数据库添加数据(书签表)
    fun addBookmark(title: String, url: String) {
        //第二个参数是数据库名
        val dbHelper = BookmarkDataHelper(this@MainActivity, "bookmark", null, 1);
        val db = dbHelper.writableDatabase
        val value = ContentValues().apply {
            put("title", title)
            put("url", url)
        }
        //insert（）方法中第一个参数是表名，第二个参数是表示给表中未指定数据的自动赋值为NULL。第三个参数是一个ContentValues对象
        db.insert("bookmark", null, value)
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
    }


    fun initWebView() {
        webView = findViewById(R.id.webview)
        webView?.loadUrl(WEB_URL)

        val webClient = object : WebViewClient() {
            // override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            //     // return false
            // }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.startsWith("http://") || url!!.startsWith("https://")) {
                    view!!.loadUrl(url)
                    return true
                } else {
                    // val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    // startActivity(intent)

                }
                return true
            }
        }

        //下面这些直接复制就好
        webView?.webViewClient = webClient

        var webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true  // 开启 JavaScript 交互
        webSettings.setAppCacheEnabled(true) // 启用或禁用缓存
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // 只要缓存可用就加载缓存, 哪怕已经过期失效 如果缓存不可用就从网络上加载数据
        webSettings.setAppCachePath(cacheDir.path) // 设置应用缓存路径

        // 缩放操作
        webSettings.setSupportZoom(false) // 支持缩放 默认为true 是下面那个的前提
        webSettings.builtInZoomControls = false // 设置内置的缩放控件 若为false 则该WebView不可缩放
        webSettings.displayZoomControls = false // 隐藏原生的缩放控件

        webSettings.blockNetworkImage = false // 禁止或允许WebView从网络上加载图片
        webSettings.loadsImagesAutomatically = true // 支持自动加载图片

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.safeBrowsingEnabled = true // 是否开启安全模式
        }

        webSettings.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
        webSettings.domStorageEnabled = true // 启用或禁用DOM缓存
        webSettings.setSupportMultipleWindows(true) // 设置WebView是否支持多窗口

        // 设置自适应屏幕, 两者合用
        webSettings.useWideViewPort = true  // 将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true  // 缩放至屏幕的大小
        webSettings.allowFileAccess = true // 设置可以访问文件

        webSettings.setGeolocationEnabled(true) // 是否使用地理位置

        webView?.fitsSystemWindows = true
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView?.loadUrl(WEB_URL)
    }

    //设置返回键的监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView!!.canGoBack()) {
                webView!!.goBack()  //返回上一个页面
                return true
            } else {
                finish()
                return true
            }
        }
        return false
    }
}