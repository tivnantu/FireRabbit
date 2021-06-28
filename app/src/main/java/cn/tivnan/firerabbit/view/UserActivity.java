package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.controller.BookmarkController;
import cn.tivnan.firerabbit.entity.Bookmark;
import cn.tivnan.firerabbit.entity.History;
import cn.tivnan.firerabbit.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {
    private TextView tv_id, tv_username;
    private ItemGroup ig_edit, ig_bookmark, ig_history, ig_sync, ig_logout;
    private SharedPreferences pref;
    private String id, username, password, sessionId;//在服务器上保存用户登录状态，登录状态下需要带上sessionId给服务器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();

        //修改用户资料
        ig_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openUserInfoEditPage();
            }
        });
        //进入书签页面
        ig_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBookmarkPage();
            }
        });
        //键入历史记录界面
        ig_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHistoryPage();
            }
        });
        //同步用户数据
        ig_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder buider = getAlertDialogBuider();
                buider.setIcon(R.drawable.logo);
                buider.setTitle("云同步");
                buider.setMessage("云同步将把本地数据同步到云端，同时将云端数据保存到本地");
                buider.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address = "http://firerabbit.tivnan.cn/bookmark/sync";
                        HttpUtil.syncWithOkHttp(address, getBookmarkList(), sessionId, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                makeToast("云同步失败，请检查网络连接");
                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                //将服务器返回的用户账户的bookmark数据存储到本地数据库
                                String responseData = response.body().string();
                                Gson gson = new Gson();
                                Map map = gson.fromJson(responseData, Map.class);
                                List<Bookmark> bookmarkList = (List<Bookmark>) map.get("date");//TODO 检验是否可行
                                BookmarkController bookmarkController = getBookmarkController();
                                bookmarkController.addBookmarkList(bookmarkList);

                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        makeToast("云同步成功");
                                    }
                                });
                            }
                        });
                    }
                });
                buider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                buider.show();

            }
        });

        //退出登录
        ig_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送退出请求
                String address = "http://firerabbit.tivnan.cn/user/logout";
                HttpUtil.logoutWithOkHttp(address, sessionId);

                //删除本地保存用户信息的userInfo并返回前一个界面
//                SharedPreferences.Editor editor = pref.edit();
//                editor.clear();
//                editor.commit();
                File file = new File("/data/data/" + getPackageName() + "/shared_prefs/userInfo.xml");
                file.delete();
                finish();
            }
        });
    }

    private List getBookmarkList() {
        BookmarkController bookmarkController = new BookmarkController(this);
        return bookmarkController.getBookmarkList();
    }

    private BookmarkController getBookmarkController() {
        return new BookmarkController(this);
    }
    private void openUserInfoEditPage() {
        Intent intent = new Intent(this, UserInfoEditActivity.class);
        startActivity(intent);
        this.finish();
    }
    private void startBookmarkPage() {
        Intent intent = new Intent(this, BookmarkActivity.class);
        startActivity(intent);
    }
    private void startHistoryPage() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }


    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private AlertDialog.Builder getAlertDialogBuider() {
        return new AlertDialog.Builder(this);
    }

    private void init() {
        tv_id = (TextView)findViewById(R.id.id_tv);
        tv_username = (TextView)findViewById(R.id.username_tv);
        ig_edit = (ItemGroup)findViewById(R.id.edit_ig);
        ig_bookmark = (ItemGroup)findViewById(R.id.bookmark_ig);
        ig_history = (ItemGroup)findViewById(R.id.history_ig);
        ig_sync = (ItemGroup)findViewById(R.id.sync_ig);
        ig_logout = (ItemGroup)findViewById(R.id.logout_ig);

        //从SharedPreferences中取出id、昵称、密码、sessionId
        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        id = pref.getString("id", "");
        username = pref.getString("username","");
        password = pref.getString("password", "");
        sessionId = pref.getString("sessionId", "");
        tv_id.setText("ID: "+id);
        tv_username.setText("昵称: "+username);
    }


}