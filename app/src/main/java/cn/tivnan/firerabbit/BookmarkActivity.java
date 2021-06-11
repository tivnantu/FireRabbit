package cn.tivnan.firerabbit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.tivnan.firerabbit.dao.BookmarkDataHelper;

public class BookmarkActivity extends AppCompatActivity {

    private List<Bookmark> bookmarkLIst = new ArrayList<>();
    //从数据库中读取bookmark数据
    BookmarkDataHelper dbHelper = new BookmarkDataHelper(this,"bookmark",null, 1);
//    SQLiteDatabase db = dbHelper.getWritableDatabase();

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

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.bookmark_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(bookmarkLIst);
        recyclerView.setAdapter(bookmarkAdapter);
        //实现点击事件
        bookmarkAdapter.setOnMyItemClickListener(new BookmarkAdapter.OnMyItemClickListener() {
            @Override
            public void myClick(View v, int pos) {
                //对Arraylist中的数据进行刷新
//                bookmarkLIst.remove(bookmarkLIst.get(pos));
                //对界面进行刷新
//                bookmarkAdapter.notifyDataSetChanged();
                //数据库中删除书签
                dbHelper.getWritableDatabase().delete("bookmark","url = ?", new String[]{bookmarkLIst.get(pos).getUrl()});
                initBookmarks();
            }
            @Override
            public void mLongClick(View v, int pos) {

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

    private void initBookmarks(){
//        Bookmark bookmark1 = new Bookmark(0, "baidu.com");
//        Bookmark bookmark2 = new Bookmark(2, "bing.com");
//        bookmarkLIst.add(bookmark1);
//        bookmarkLIst.add(bookmark2);

        //查询bookmark表中所有数据
        Cursor cursor = dbHelper.getWritableDatabase().query("bookmark",null,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                //遍历Cursor对象，取出数据装进bookList
                bookmarkLIst.add(new Bookmark(cursor.getString(cursor.getColumnIndex("title")),cursor.getString(cursor.getColumnIndex("url"))));
            } while (cursor.moveToNext());
        }
        Collections.reverse(bookmarkLIst);
        cursor.close();
    }


}