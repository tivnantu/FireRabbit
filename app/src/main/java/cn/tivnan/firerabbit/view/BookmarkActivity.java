package cn.tivnan.firerabbit.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.tivnan.firerabbit.MainActivity;
import cn.tivnan.firerabbit.adapter.BookmarkAdapter;
import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.controller.BookmarkController;
import cn.tivnan.firerabbit.entity.Bookmark;

public class BookmarkActivity extends AppCompatActivity {

    private List<Bookmark> bookmarkList = new ArrayList<>();
    private BookmarkController bookmarkController;
    private BookmarkAdapter bookmarkAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        //添加标题栏返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

            //长按书签弹出popupMenu，可选择删除或编辑书签
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
                                bookmarkController.removeBookmark(bookmarkList.get(pos).getUrl());
                                bookmarkList = bookmarkController.getBookmarkList();
                                bookmarkAdapter.notifyDataSetChanged();
                                break;
                                //点击编辑跳转至编辑页面
                            case R.id.editItem:
                                Intent intent = new Intent(v.getContext(), BookmarkEditActivity.class);
                                intent.putExtra("url", bookmarkList.get(pos).getUrl());
                                intent.putExtra("name", bookmarkList.get(pos).getName());
                                intent.putExtra("pos", pos);
                                startActivityForResult(intent, 1);
                                break;
                            default:
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //加载书签页面
    private void initBookmarks(){
        recyclerView = (RecyclerView)findViewById(R.id.bookmark_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //读取书签列表
        bookmarkController = new BookmarkController(this);
        bookmarkList = bookmarkController.getBookmarkList();

        bookmarkAdapter = new BookmarkAdapter(bookmarkList);
        recyclerView.setAdapter(bookmarkAdapter);
    }

    //
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
                    //bookmarkList = bookmarkController.getBookmarkList();//为什么不能通过重新加载bookmarkList的方式更新呢？？？？
                    bookmarkList.set(p, new Bookmark(data.getStringExtra("newName"), data.getStringExtra("newUrl")));//必须更新bookmarkList才能在界面上更新
                    bookmarkAdapter.notifyDataSetChanged();
//                    initBookmarks();//这样会及时更新但会导致再次点击item时无响应的情况
                }
                break;
            default:
        }
    }
}