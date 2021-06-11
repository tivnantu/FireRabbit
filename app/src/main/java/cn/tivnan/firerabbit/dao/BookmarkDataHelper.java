package cn.tivnan.firerabbit.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import cn.tivnan.firerabbit.Bookmark;

public class BookmarkDataHelper extends SQLiteOpenHelper {
    //书签表
    public static final String CREATE_bookmarkDB = "create table bookmark(" +
            "id integer primary key autoincrement," +
            "title text ," +
            "url text)";

    private Context mContext;

    public BookmarkDataHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_bookmarkDB);
        Log.d("BookmarkDataHelper", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists bookmark");
        onCreate(db);
    }

}
