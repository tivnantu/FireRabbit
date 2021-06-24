package cn.tivnan.firerabbit.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
        //添加标题栏返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
                Log.d("onClickItem", String.valueOf(pos));

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
                                historyController.removeHistoryById(pos);
                                historyAdapter.notifyItemRemoved(pos);//最后再通知adapter更新页面
                                break;
                            //点击清空历史记录
                            case R.id.deleteAllItem:
                                //清空之前需要确认
                                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                                        .setTitle("您确定清空历史吗？")//设置对话框的标题
                                        //设置对话框的按钮
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("清空", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                historyController.removeAllHistory();
                                                historyList.clear();
                                                historyAdapter.notifyDataSetChanged();
                                            }
                                        }).create();
                                dialog.show();
                                break;
                            //点击复制选中的历史记录url
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
                Log.d("onClickDelete", String.valueOf(pos));
                historyController.removeHistoryById(pos);
//                historyAdapter.notifyItemRemoved(pos);//使用这种方式更新会导致pos错乱,函数里面的传入的参数position，
//                它是在进行onBind操作时确定的，在删除单项后，已经出现在画面里的项不会再有调用onBind机会，
//                这样它保留的position一直是未进行删除操作前的position值。

//                historyAdapter.notifyDataSetChanged();//解决办法1：整个列表重新加载

                historyAdapter.notifyItemRemoved(pos);//解决办法2：对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
                if (pos != historyList.size())
                    historyAdapter.notifyItemRangeChanged(pos,historyList.size() - pos);

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



}