package cn.tivnan.firerabbit.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HistoryDataHelper extends MyDataHelper {

    private Context mContext;

    public HistoryDataHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    public Cursor queryAllBookmarks() {
        return getWritableDatabase().query("history",null,null,null,null,null,null,null);
    }

    /**
     * 插入历史记录
     */
    public long insertHistory(ContentValues contentValues) {
        return getWritableDatabase().insert("history", null, contentValues);
    }


    //删除所有历史记录
    public void deleteAllHistory() {
        getWritableDatabase().delete("history", null, null);
    }

    //删除单个历史记录
    public void deleteHistoryById(String id) {
        getWritableDatabase().delete("history", "id = ?", new String[]{id});
    }

}
