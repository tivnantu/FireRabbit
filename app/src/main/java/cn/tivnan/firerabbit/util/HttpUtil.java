package cn.tivnan.firerabbit.util;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class HttpUtil {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();

    //登录
    public static void loginWithOkHttp(String address, okhttp3.Callback callback) {
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //注册
    public static void registerWithOkHttp(String address, String id, String password, okhttp3.Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("username", "user" + id);//用户名在注册时由系统分配
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
//        RequestBody requestBody = new FormBody.Builder()
//                .add("id", id)
//                .add("id","default")
//                .add("password", password)
//                .build();
//        Request request = new Request.Builder()
//                .url(address)
//                .post(requestBody)
//                .build();

        client.newCall(request).enqueue(callback);
    }

    //修改用户信息
    public static void updateUserWithOkHttp(String address,
                                            String id, String newUsername,
                                            String newPassword,
                                            String sessionId,
                                            okhttp3.Callback callback) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("username", newUsername);
            jsonObject.put("password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .addHeader("cookie", sessionId)
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //退出登录
    public static void logoutWithOkHttp(String address) {
        try {
            Request request = new Request.Builder()
                    .url(address)
                    .build();
            client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
