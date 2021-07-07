package cn.tivnan.firerabbit.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.tivnan.firerabbit.adapter.BookmarkAdapter;
import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.controller.BookmarkController;
import cn.tivnan.firerabbit.entity.Bookmark;
import cn.tivnan.firerabbit.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BookmarkActivity extends AppCompatActivity {
    private List<Bookmark> bookmarkList = new ArrayList<>();
    private BookmarkController bookmarkController;
    private BookmarkAdapter bookmarkAdapter;
    private RecyclerView bookmarkRecycler;
    private LinearLayoutManager layoutManager;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private File bookmarkFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        initBookmarks();
        //实现点击事件
        bookmarkAdapter.setOnMyItemClickListener(new BookmarkAdapter.OnMyItemClickListener() {
            //单击跳转
            @Override
            public void myClick(View v, int pos) {
                // Intent intent = new Intent(v.getContext(), MainActivity.class);
                // intent.putExtra("url", bookmarkList.get(pos).getUrl());
                // startActivity(intent);
                Intent intent = new Intent();//没有任何参数（意图），只是用来传递数据
                intent.putExtra("url", bookmarkList.get(pos).getUrl());
                setResult(RESULT_OK, intent);
                finish();
            }

            //长按书签弹出popupMenu，可选择删除、编辑书签、复制链接、分享
            @Override
            public void mLongClick(View v, int pos) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_bookmark, popupMenu.getMenu());
                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                                //点击删除
                            case R.id.deleteItem:
                                if (bookmarkFile.exists()) {//只有在登录的状态下通知服务器
                                    removeBookmarkFromUser(pos);//通知服务器删除用户账号内保存的书签
                                }
                                bookmarkController.removeBookmarkByUrl(pos);//从本地数据库中删除
                                bookmarkAdapter.notifyDataSetChanged();//最后再通知adapter更新页面
                                break;
                                //点击编辑跳转至编辑页面
                            case R.id.editItem:
                                Intent intent = new Intent(v.getContext(), BookmarkEditActivity.class);
                                intent.putExtra("url", bookmarkList.get(pos).getUrl());
                                intent.putExtra("name", bookmarkList.get(pos).getName());
                                intent.putExtra("pos", pos);
                                startActivityForResult(intent, 1);
                                break;
                                //点击复制选中的书签
                            case R.id.copyItemLink:
                                    //获取剪贴板管理器
                                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    // 创建普通字符型ClipData
                                    ClipData mClipData = ClipData.newPlainText("urlCopied", bookmarkList.get(pos).getUrl());
                                    // 将ClipData内容放到系统剪贴板里。
                                    cm.setPrimaryClip(mClipData);
                                    Toast.makeText(v.getContext(), "复制成功", Toast.LENGTH_SHORT).show();
                                    break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });
    }


    private void removeBookmarkFromUser(int pos) {
        int id = bookmarkController.getBookmarkId(pos);
        String sessionId = pref.getString("sessionId", "");
        String address = "http://firerabbit.tivnan.cn/bookmark/delete?id=" + id;
        HttpUtil.removeBookmarkWithOkHttp(address, sessionId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookmarkActivity.this, "本地删除成功，请连接网络点击云同步删除云端书签", Toast.LENGTH_SHORT).show();
                        recordBookmarksNeedToBeDeleted(id);
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new Gson();
                Map map = gson.fromJson(responseData, Map.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (map.get("code").equals("200")) {
                            Toast.makeText(BookmarkActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BookmarkActivity.this, "本地删除成功，请连接网络点击云同步删除云端书签", Toast.LENGTH_SHORT).show();
                            //如果同步删除失败，则记录下要删除的书签id，用户下次点击同步时上传删除记录。
                            recordBookmarksNeedToBeDeleted(id);
                        }
                    }
                });
            }
        });
    }

    private void recordBookmarksNeedToBeDeleted(int id) {
        editor = getSharedPreferences("bookmarksNeedToBeDeleted", MODE_PRIVATE).edit();
        editor.putString(String.valueOf(System.currentTimeMillis()), String.valueOf(id));
        editor.apply();
    }

    //修改完成后回调此函数，
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //从BookmarkEditActivity获取书签修改结果并保存
                    int p = data.getIntExtra("pos", 0);
                    bookmarkController.modifyBookmark(
                            data.getStringExtra("newName"),
                            data.getStringExtra("newUrl"),
                            bookmarkList.get(p).getUrl());
//                    Log.d("testPos", String.valueOf(data.getIntExtra("pos", 0)));
//                    Log.d("testP", String.valueOf(p));

//                    更改书签后，刷新书签列表的页面
                    List<Bookmark> bookmarkLists = new ArrayList<>();
                    bookmarkLists = bookmarkController.getBookmarkList();//为什么不能通过重新加载bookmarkList的方式更新呢？？？？
//                    bookmarkList.set(p, new Bookmark(data.getStringExtra("newName"), data.getStringExtra("newUrl")));//必须更新bookmarkList才能在界面上更新
                    bookmarkList.clear();
                    bookmarkList.addAll(bookmarkLists);
                    bookmarkAdapter.notifyDataSetChanged();
//                    initBookmarks();//这样会及时更新但会导致再次点击item时无响应的情况
                }
                break;
            default:
        }
    }

    //加载书签页面
    private void initBookmarks(){
        bookmarkRecycler = (RecyclerView)findViewById(R.id.bookmark_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        bookmarkRecycler.setLayoutManager(layoutManager);

        //读取书签列表
        bookmarkController = new BookmarkController(this);
        bookmarkList = bookmarkController.getBookmarkList();

        pref = getSharedPreferences("userInfo", MODE_PRIVATE);
        bookmarkFile = new File("/data/data/" + getPackageName() + "/shared_prefs/userInfo.xml");

        bookmarkAdapter = new BookmarkAdapter(bookmarkList);
        bookmarkRecycler.setAdapter(bookmarkAdapter);
    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish(); // back button
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}