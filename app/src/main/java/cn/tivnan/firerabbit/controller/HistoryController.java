package cn.tivnan.firerabbit.controller;

import android.content.ContentValues;
import android.content.Context;

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
    public Boolean addBookmark(String title, String url) {
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("url", url);
            return historyDataHelper.insertBookmark(values) > 1;
    }

}
