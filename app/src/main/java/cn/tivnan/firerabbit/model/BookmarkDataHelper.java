package cn.tivnan.firerabbit.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class BookmarkDataHelper extends MyDataHelper {

    private Context mContext;

    public BookmarkDataHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    /**
     * 根据url获得书签
     *
     * @param url
     * @return
     */
    public Cursor queryBookmarkByUrl(String url) {
        Cursor cursor = getWritableDatabase().rawQuery("select * from bookmark where url = ? ", new String[]{url});
        return cursor;
    }

    /**
     * 插入新的书签
     * @return
     */
    public long insertBookmark(ContentValues contentValues) {
        return getWritableDatabase().insert("bookmark", null, contentValues);
    }

    //查询所有书签
    public Cursor queryAllBookmarks() {
        return getWritableDatabase().query("bookmark",null,null,null,null,null,null,null);
    }

    //修改书签
    public void updateBookmark(String newName, String newUrl, String oldUrl) {
        ContentValues values = new ContentValues();
        values.put("title", newName);
        values.put("url",newUrl);
        getWritableDatabase().update("bookmark", values, "url = ?", new String[]{oldUrl});
    }

    //删除书签
    public void  deleteBookmark(String url) {
        getWritableDatabase().delete("bookmark", "url = ?", new String[]{url});
    }

}
