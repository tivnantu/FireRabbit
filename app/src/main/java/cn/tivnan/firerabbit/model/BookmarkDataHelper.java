package cn.tivnan.firerabbit.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class BookmarkDataHelper extends SQLiteOpenHelper {
    //创建书签sql
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

}