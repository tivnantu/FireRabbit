package cn.tivnan.firerabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private List<Bookmark> bookmarkLIst = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boookmark);

        initBookmarks();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.bookmark_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        BookmarkAdapter adapter = new BookmarkAdapter(bookmarkLIst);
        recyclerView.setAdapter(adapter);
    }
    private void initBookmarks(){
        Bookmark bookmark1 = new Bookmark(0, "baidu.com");
        Bookmark bookmark2 = new Bookmark(0, "bing.com");
        Bookmark bookmark3 = new Bookmark(0, "google.com");
    }
}