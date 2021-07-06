package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoEditActivity extends AppCompatActivity {
    private ItemGroup ig_edit_username, ig_edit_old_password, ig_edit_new_password, ig_edit_certify_new_password, ig_edit_certify;
    private String id, username, newUsername, password, oldPassword, newPassword, newPasswordCertify, address,sessionId;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        init();

        ig_edit_certify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = "http://firerabbit.tivnan.cn/user/updateUser";
                newUsername = ig_edit_username.getContentText();
                oldPassword = ig_edit_old_password.getContentText();
                newPassword = ig_edit_new_password.getContentText();
                newPasswordCertify = ig_edit_certify_new_password.getContentText();

                if (oldPassword.equals(password)) {//
                    if (!newPassword.trim().isEmpty()) {
                        if (newPassword.equals(newPasswordCertify)) {
                            //向服务器发送更改用户信息的请求
                            try {
                                updateUserWithOkHttp(address, id, newUsername, newPassword,sessionId);//请求成功后还要更新存在本地的用户信息
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            makeToast("两次输入的新密码不一致，请重新输入");
//                            ig_edit_new_password.clearContentText();
                            ig_edit_certify_new_password.clearContentText();
                        }
                    } else {
                        makeToast("新密码不能为空");
                    }
                } else {
                    makeToast("原密码错误");
                    ig_edit_old_password.clearContentText();
                }
            }
        });
    }

    private void updateUserWithOkHttp(String address, String id, String newUsername, String newPassword, String sessionId) throws JSONException {
        HttpUtil.updateUserWithOkHttp(address, id, newUsername, newPassword, sessionId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                makeToast("更改用户信息失败，请检查网络连接");
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
                            Toast.makeText(UserInfoEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            //用户信息更新成功后，还要修改本地的sharedPreference中的用户信息
                            updateUserWithSharedPreference(id, newUsername, newPassword);
                            openUserPage();
                        } else {
                            Toast.makeText(UserInfoEditActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }



    private void updateUserWithSharedPreference(String id, String newUsername, String newPassword) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.putString("username", newUsername);
        editor.putString("password", newPassword);
        editor.commit();
    }

    private void openUserPage() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        finish();
    }

    private void makeToast(String toast){
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        ig_edit_username = findViewById(R.id.edit_username_ig);
        ig_edit_old_password = findViewById(R.id.edit_old_password_ig);
        ig_edit_new_password = findViewById(R.id.edit_new_password_ig);
        ig_edit_certify_new_password = findViewById(R.id.edit_certify_new_password_ig);
        ig_edit_certify = findViewById(R.id.edit_certify_ig);

        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        id = pref.getString("id", "");
        username = pref.getString("username", "");
        password = pref.getString("password", "");
        sessionId = pref.getString("sessionId", "");
        newUsername = username;
        ig_edit_username.setUsername(username);

    }
}