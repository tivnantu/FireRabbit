package cn.tivnan.firerabbit.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.tivnan.firerabbit.entity.Bookmark;
import cn.tivnan.firerabbit.entity.History;
import cn.tivnan.firerabbit.model.HistoryDataHelper;

public class HistoryController {

    private final HistoryDataHelper historyDataHelper;

    public HistoryController(Context context) {
        this.historyDataHelper =new HistoryDataHelper(context, "FireRabbit", null, 1);
    }

    /**
     * 添加历史记录
     * @param title
     * @param url
     * @return
     */
    public Boolean addHistory(String title, String url) {
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("url", url);
            return historyDataHelper.insertHistory(values) > 1;
    }

    //将书签填充进BookmarkList
    public List<History> getHistoryList() {
        Cursor cursor = historyDataHelper.queryAllBookmarks();
        List<History> historyList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                //遍历Cursor对象，取出数据装进bookList
                historyList.add(new History(cursor.getString(cursor.getColumnIndex("title")),cursor.getString(cursor.getColumnIndex("url"))));
            } while (cursor.moveToNext());
        }
        Collections.reverse(historyList);
        cursor.close();
        return historyList;
    }
    //删除所有历史记录
    public void removeAllHistory() {
        historyDataHelper.deleteAllHistory();
    }
    //删除单条历史记录
    public void removeHistory(String url) {
        historyDataHelper.deleteHistory(url);
    }

}
