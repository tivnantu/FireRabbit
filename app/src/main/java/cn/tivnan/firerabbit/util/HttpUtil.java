package cn.tivnan.firerabbit.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.List;

import cn.tivnan.firerabbit.entity.Bookmark;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

//在服务器上保存用户登录状态，登录状态下需要带上sessionId给服务器
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
    public static void registerWithOkHttp(String address,
                                          String id,
                                          String password,
                                          okhttp3.Callback callback) {
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
                                            String id,
                                            String newUsername,
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

    //用户数据同步
    public static void syncWithOkHttp(String address, List<Bookmark> list, String sessionId, okhttp3.Callback callback) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("bookmarkList", jsonArray);
            for (int i=0; i<list.size(); i++) {
                JSONObject jo = new JSONObject();
                jsonArray.put(i, jo);
                jo.put("id", list.get(i).getId());
                jo.put("title", list.get(i).getName());
                jo.put("url", list.get(i).getUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
//        RequestBody requestBody = RequestBody.create(JSON, json);
        RequestBody requestBody = RequestBody.Companion.create(json, JSON);
        Request request = new Request.Builder()
                .addHeader("cookie", sessionId)
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //退出登录
    public static void logoutWithOkHttp(String address, String sessionId) {
        try {
            Request request = new Request.Builder()
                    .addHeader("cookie", sessionId)
                    .url(address)
                    .build();
            client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
