package cn.tivnan.firerabbit.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.tivnan.firerabbit.entity.Bookmark;
import cn.tivnan.firerabbit.model.BookmarkDataHelper;

public class BookmarkController {
    private final BookmarkDataHelper bookmarkDBHelper;

    public BookmarkController(Context context) {
        this.bookmarkDBHelper = new BookmarkDataHelper(context, "FireRabbit", null, 1);
    }

    /**
     *
     * 是否存在已经包含该url的书签
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

    //将书签填充进BookmarkList
    public List<Bookmark> getBookmarkList() {
        Cursor cursor = bookmarkDBHelper.queryAllBookmarks();
        List<Bookmark> bookmarkList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                //遍历Cursor对象，取出数据装进bookList
                bookmarkList.add(new Bookmark(cursor.getString(cursor.getColumnIndex("title")),cursor.getString(cursor.getColumnIndex("url"))));
            } while (cursor.moveToNext());
        }
        Collections.reverse(bookmarkList);
        cursor.close();
        return bookmarkList;
    }

    //修改书签n
    public void modifyBookmark(String newName, String newUrl, String oldUrl) {
        bookmarkDBHelper.updateBookmark(newName, newUrl, oldUrl);
    }
    //删除书签
    public void removeBookmark(String url){
        bookmarkDBHelper.deleteBookmark(url);
    }

}
