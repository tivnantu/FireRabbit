package cn.tivnan.firerabbit.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.tivnan.firerabbit.entity.Bookmark;
import cn.tivnan.firerabbit.entity.BookmarkVO;
import cn.tivnan.firerabbit.model.BookmarkDataHelper;

public class BookmarkController {
    private final BookmarkDataHelper bookmarkDBHelper;
    private List<Bookmark> bookmarkList;

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
    public Boolean addBookmark(int id, String title, String url) {

        if (isNoExistBoomark(url)) {
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("title", title);
            values.put("url", url);
            return bookmarkDBHelper.insertBookmark(values) > 1;
        }
        return false;
    }

    public void addBookmarkObj(BookmarkVO bookmarkvo) {
            ContentValues values = new ContentValues();
            values.put("id", bookmarkvo.getId());
            values.put("title", bookmarkvo.getTitle());
            values.put("url", bookmarkvo.getUrl());
            bookmarkDBHelper.insertBookmark(values);
    }

    public void addBookmarkList(List<Bookmark> bookmarkList) {
            for (int i = 0; i<bookmarkList.size(); i++){
                ContentValues values = new ContentValues();
                values.put("id", bookmarkList.get(i).getId());
                values.put("title", bookmarkList.get(i).getName());
                values.put("url", bookmarkList.get(i).getUrl());
                bookmarkDBHelper.insertBookmark(values);
            }
    }

    //将书签填充进BookmarkList
    public List<Bookmark> getBookmarkList() {
        Cursor cursor = bookmarkDBHelper.queryAllBookmarks();
        bookmarkList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                //遍历Cursor对象，取出数据装进bookList
                bookmarkList.add(new Bookmark(cursor.getInt(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("title")),cursor.getString(cursor.getColumnIndex("url"))));
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
    public void removeBookmarkByUrl(int pos) {
        bookmarkDBHelper.deleteBookmark(bookmarkList.get(pos).getUrl());
        bookmarkList.remove(pos);//别忘了更新bookList中的数据，不执行这一步的话adapter中的bookList不会更新的
    }

}
