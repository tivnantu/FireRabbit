package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.adapter.HistoryAdapter;
import cn.tivnan.firerabbit.controller.HistoryController;
import cn.tivnan.firerabbit.entity.History;

public class HistoryActivity extends AppCompatActivity {

    private List<History> historyList = new ArrayList<>();
    private HistoryController historyController;
    private HistoryAdapter historyAdapter;
    private RecyclerView historyRecycler;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initHistories();

    }
    private void initHistories() {
        historyRecycler = (RecyclerView)findViewById(R.id.history_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        historyRecycler.setLayoutManager(layoutManager);

        historyController = new HistoryController(this);
        historyList = historyController.getHistoryList();

        historyAdapter = new HistoryAdapter(historyList);
        historyRecycler.setAdapter(historyAdapter);

        historyAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent();//没有任何参数（意图），只是用来传递数据
                intent.putExtra("url", historyList.get(pos).getUrl());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLongClick(View v, int pos) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_history, popupMenu.getMenu());
                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //点击删除
                            case R.id.deleteItem:
                                historyController.removeHistory(historyList.get(pos).getUrl());//从数据库中删除
                                historyList.remove(pos);//别忘了更新bookList中的数据，不执行这一步的话adapter中的bookList不会更新的
                                historyAdapter.notifyItemRemoved(pos);//最后再通知adapter更新页面
                                break;
                            //点击编辑跳转至编辑页面
                            case R.id.deleteAllItem:
                                historyController.removeAllHistory();
                                historyList.clear();
                                historyAdapter.notifyDataSetChanged();
                                break;
                            //点击复制选中的书签
                            case R.id.copyItemLink:
                                //获取剪贴板管理器
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                // 创建普通字符型ClipData
                                ClipData mClipData = ClipData.newPlainText("urlCopied", historyList.get(pos).getUrl());
                                // 将ClipData内容放到系统剪贴板里。
                                cm.setPrimaryClip(mClipData);
                                Toast.makeText(v.getContext(), "复制成功", Toast.LENGTH_SHORT).show();
                                break;
                            //点击分享选中的书签
                            case R.id.shareItem:

                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }

            @Override
            public void onItemDeleteClick(View v, int pos) {
                historyController.removeHistory(historyList.get(pos).getUrl());
                historyList.remove(pos);
                historyAdapter.notifyItemRemoved(pos);
            }
        });
    }



}