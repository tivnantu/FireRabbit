package cn.tivnan.firerabbit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.tivnan.firerabbit.dao.BookmarkDataHelper;

public class BookmarkActivity extends AppCompatActivity {

    private List<Bookmark> bookmarkLIst = new ArrayList<>();

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


        //从数据库中读取bookmark数据
        BookmarkDataHelper dbHelper = new BookmarkDataHelper(this,"bookmark",null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //查询bookmark表中所有数据
        Cursor cursor = db.query("bookmark",null,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                //遍历Cursor对象，取出数据装进bookList
                bookmarkLIst.add(new Bookmark(0, cursor.getString(cursor.getColumnIndex("title"))));
            } while (cursor.moveToNext());
        }
        Collections.reverse(bookmarkLIst);

        cursor.close();
    }
}