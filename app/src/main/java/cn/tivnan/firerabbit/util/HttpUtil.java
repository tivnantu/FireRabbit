package cn.tivnan.firerabbit.util;

import org.json.JSONException;
import org.json.JSONObject;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    //登录
    public static void loginWithOkHttp(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
    //注册
    public static void registerWithOkHttp(String address, String id, String password, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject=new JSONObject();
        try {
                jsonObject.put("id", id);
                jsonObject.put("username", "user"+id);
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

}
