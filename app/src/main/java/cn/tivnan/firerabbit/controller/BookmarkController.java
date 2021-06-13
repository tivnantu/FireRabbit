package cn.tivnan.firerabbit.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.tivnan.firerabbit.model.BookmarkDataHelper;

public class BookmarkController {
    private final BookmarkDataHelper bookmarkDBHelper;

    public BookmarkController(Context context) {
        this.bookmarkDBHelper = new BookmarkDataHelper(context, "bookmaek", null, 1);
    }

    /**
     * 是否存在已经包含该url的书签
     *
     * @param url
     * @return
     */
    public Boolean isNoExistBoomark(String url) {
        Cursor cursor = bookmarkDBHelper.queryBookmarkByUrl(url);
        return cursor.getCount() < 1;
    }


    /**
     * 添加书签
     * @param title
     * @param url
     * @return
     */
    public Boolean addBookmark(String title, String url) {

        if (isNoExistBoomark(url)) {
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("url", url);
            return bookmarkDBHelper.insertBookmark(values) > 1;
        }
        return false;
    }
}
