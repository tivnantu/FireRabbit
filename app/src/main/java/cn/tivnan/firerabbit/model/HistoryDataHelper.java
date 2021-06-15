package cn.tivnan.firerabbit.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HistoryDataHelper extends SQLiteOpenHelper {

    //创建书签sql
    public static final String CREATE_historyDB = "create table history(" +
            "id integer primary key autoincrement," +
            "title text ," +
            "url text)";

    private Context mContext;

    public HistoryDataHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_historyDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists history");
        onCreate(db);
    }

    /**
     * 插入历史记录
     */
    public long insertBookmark(ContentValues contentValues) {
        return getWritableDatabase().insert("history", null, contentValues);
    }
}
