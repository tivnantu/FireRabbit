package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.util.CheckEditForButton;
import cn.tivnan.firerabbit.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class LoginOrRegisterActivity extends AppCompatActivity {
    private EditText editText_name, editText_password;
    private Button button_login, button_register;
    private CheckBox checkBox;
    String id, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = String.valueOf(editText_name.getText());
                password = String.valueOf(editText_password.getText());
                String address = "http://firerabbit.tivnan.cn/user/signin" + "?id=" + id + "&password=" + password;
                loginWithOkHttp(address);
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = String.valueOf(editText_name.getText());
                password = String.valueOf(editText_password.getText());
                String address = "http://firerabbit.tivnan.cn/user/signup";
                registerWithOkHttp(address, id, password);
            }
        });


    }

    private void init() {
        //添加标题栏返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_login_or_register);
        editText_name = findViewById(R.id.user_name);
        editText_password = findViewById(R.id.user_password);
        button_login = findViewById(R.id.login);
        button_register = findViewById(R.id.register);
        checkBox = findViewById(R.id.checkbox);

        //监听用户名和密码两个输入框，都不为空时按钮设置为可点击状态
        //1.创建工具类对象 设置监听空间
        CheckEditForButton checkEditForButton = new CheckEditForButton(button_login, button_register);
        //2.把所有被监听的EditText设置进去
        checkEditForButton.addEditText(editText_name, editText_password);
        //3.根据接口回调的方法,分别进行操作
        checkEditForButton.setListener(new CheckEditForButton.EditTextChangeListener() {
            @Override
            public void allHasContent(boolean isHasContent) {
                if (isHasContent) {
//                    button_login.setTextColor(Color.parseColor("#FF148F"));
//                    button_register.setTextColor(Color.parseColor("FF148F"));
                } else {
//                    button_login.setTextColor(Color.parseColor("#000000"));
//                    button_register.setTextColor(Color.parseColor("#000000"));//TODO bug:用户名和密码同时填充时登录/注册界面闪退
                }
            }
        });

        //设置显示或隐藏密码
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    //如果选中，显示密码
                    editText_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    editText_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });
    }

    //实现登录
    public void loginWithOkHttp(String address) {
        HttpUtil.loginWithOkHttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginOrRegisterActivity.this, "登录失败onFailure，请检查网络是否连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //得到服务器返回的具体内容
                String responseData = response.body().string();
                Gson gson = new Gson();
                Map map = gson.fromJson(responseData, Map.class);
                Map data = (Map) map.get("date");
//                借助runOnUiThread方法进行线程转换，因为回调接口在子线程中运行，子线程内不可以执行任何UI操作
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (map.get("code").equals("200")) {//返回json文件中code=200则登录成功
                            //保存sessionId


                            Toast.makeText(LoginOrRegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            //登录成功时将用户信息利用SharedPreferences存储起来（退出登录时将此信息删除），此信息有两个用途
                            //一是文件的存在与否可以判断用户是否登录
                            //二是在用户登录的情况下，可以在用户界面展示用户信息
                            String sessionId = getSessionId(response);
                            saveUserInfo(String.valueOf(data.get("id")), String.valueOf(data.get("username")), String.valueOf(data.get("password")), sessionId);
                            //登录成功即跳转到用户界面
                            openUserPage();
                        } else {
                            Toast.makeText(LoginOrRegisterActivity.this, "登录失败，请检查id和password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //实现注册
    public void registerWithOkHttp(String address, String id, String password) {
        HttpUtil.registerWithOkHttp(address, id, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //对异常情况进行处理
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseData = response.body().string();
                Gson gson = new Gson();
                Map map = gson.fromJson(responseData, Map.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (map.get("code").equals("200")) {

                            Toast.makeText(LoginOrRegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                            //注册后跳转到登录界面，此时也要保存用户信息到SharedPreference
                            String sessionId = getSessionId(response);
                            saveUserInfo(id, "user" + id, password, sessionId);
                            openUserPage();
                        } else {
                            Toast.makeText(LoginOrRegisterActivity.this, "注册失败，该id已注册", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void saveUserInfo(String id, String username, String password, String sessionId) {
        SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("sessionId", sessionId);
        editor.apply();
    }

    private void openUserPage() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        this.finish();
    }

    private String getSessionId(Response response) {
        //保存sessionId
        Headers headers = response.headers();
        List<String> cookies = headers.values("Set-Cookie");
        String session = cookies.get(0);
        return session.substring(0, session.indexOf(";"));
    }

}