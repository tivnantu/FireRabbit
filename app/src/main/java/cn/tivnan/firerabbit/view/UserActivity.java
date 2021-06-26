package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import cn.tivnan.firerabbit.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        TextView tv_id = (TextView)findViewById(R.id.id_tv);
        TextView tv_username = (TextView)findViewById(R.id.username_tv);
        ItemGroup ig_edit = (ItemGroup)findViewById(R.id.edit_ig);
        ItemGroup ig_bookmark = (ItemGroup)findViewById(R.id.bookmark_ig);
        ItemGroup ig_history = (ItemGroup)findViewById(R.id.history_ig);
        ItemGroup ig_sync = (ItemGroup)findViewById(R.id.sync_ig);



    }
}