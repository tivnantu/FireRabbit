package cn.tivnan.firerabbit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

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
        Bookmark bookmark1 = new Bookmark(0, "baidu.com");
        Bookmark bookmark2 = new Bookmark(2, "bing.com");
        Bookmark bookmark3 = new Bookmark(3, "google.com");
        bookmarkLIst.add(bookmark1);
        bookmarkLIst.add(bookmark2);
        bookmarkLIst.add(bookmark3);
    }
}