package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.entity.Bookmark;

public class BookmarkEditActivity extends AppCompatActivity {
    private String name, url;
    private int pos;//回传给BookmarkActivity，定位
    private EditText bookmark_name, bookmark_url;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_edit);
        initBookmark();

        //点击完成按钮提交修改，回传修改后的newName和newUrl给BookmarkActivity
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = bookmark_name.getText().toString();
                String newUrl = bookmark_url.getText().toString();
                Intent intentBack = new Intent();
                intentBack.putExtra("newName", newName);
                intentBack.putExtra("newUrl", newUrl);
                intentBack.putExtra("pos", pos);
                setResult(RESULT_OK, intentBack);
                finish();
            }
        });
    }
    private void initBookmark(){
        bookmark_name = findViewById(R.id.bookmark_name);
        bookmark_url = findViewById(R.id.bookmark_url);
        confirm = findViewById(R.id.confirm_button);
        //获取从BookmarkActivity传入的intent中包含的书签name和url并显示
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");
        pos = intent.getIntExtra("pos", 0);
        bookmark_name.setText(name);
        bookmark_url.setText(url);

    }
}