package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.util.HttpUtil;

public class UserActivity extends AppCompatActivity {
    private TextView tv_id, tv_username;
    private ItemGroup ig_edit, ig_bookmark, ig_history, ig_sync, ig_logout;
    private SharedPreferences pref;
    private String id, username, password;

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

            }
        });
        //键入历史记录界面
        ig_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //同步用户数据
        ig_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //退出登录
        ig_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器发送退出请求
                String address = "http://firerabbit.tivnan.cn/user/logout";
                HttpUtil.logoutWithOkHttp(address);

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

    private void init() {
        tv_id = (TextView)findViewById(R.id.id_tv);
        tv_username = (TextView)findViewById(R.id.username_tv);
        ig_edit = (ItemGroup)findViewById(R.id.edit_ig);
        ig_bookmark = (ItemGroup)findViewById(R.id.bookmark_ig);
        ig_history = (ItemGroup)findViewById(R.id.history_ig);
        ig_sync = (ItemGroup)findViewById(R.id.sync_ig);
        ig_logout = (ItemGroup)findViewById(R.id.logout_ig);

        //从SharedPreferences中取出id、昵称、密码
        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        id = pref.getString("id", "");
        username = pref.getString("username","");
        password = pref.getString("password", "");
        tv_id.setText("ID: "+id);
        tv_username.setText("昵称: "+username);
    }

    private void openUserInfoEditPage() {
        Intent intent = new Intent(this, UserInfoEditActivity.class);
        startActivity(intent);
    }
}