package cn.tivnan.firerabbit.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HistoryDataHelper extends MyDataHelper {

    private Context mContext;

    public HistoryDataHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    /**
     * 插入历史记录
     */
    public long insertHistory(ContentValues contentValues) {
        return getWritableDatabase().insert("history", null, contentValues);
    }
}
